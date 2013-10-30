package drawler.crawl;

import java.util.*;
import java.util.regex.*;
import java.net.*;
import java.io.*;

/**
 * Drawler thread class
 * 
 * @author Flavio Tordini
 * @created 26 novembre 2001
 * 
 * @edit Kord(倪亚宇)
 */
public class DrawlerThread implements Runnable {

	private Drawler drawler;

	public DrawlerThread(Drawler drawler) {
		this.drawler = drawler;
	}

	@SuppressWarnings({ "static-access", "unchecked" })
	public void run() {

		System.out.println(Thread.currentThread().getName() + " start");
		drawler.addThread(this);

		// 线程任务：将队列中待浏览的网址取出，然后进行处理
		while (drawler.getQueuedSize() > 0) {
			ContextualURL contextualURL = drawler.popQueuedURL();
			process(contextualURL);
			if (drawler.getQueuedSize() >= drawler.maxThreads * 3)
				drawler.startThread();
		}

		drawler.removeThread(this);
		System.out.println(Thread.currentThread().getName() + " end");

		if (drawler.isNoThreads()) {
			drawler.endProcess();
		}
	}

	// 线程函数：处理URL，下载符合下载要求的文件，将符合浏览要求的网站加入队列
	@SuppressWarnings("static-access")
	public void process(ContextualURL contextualURL) {

		// 1.建立HTTP连接
		System.out.println("connecting to " + contextualURL.url);
		drawler.updateConnected(1);
		
		HttpURLConnection connection = DrawlerUtilities
				.getValidConnection(contextualURL.url);
		if (connection == null) {
			System.out.println("Null Connection");
			return;
		}

		// 2.判断是否符合扫描、下载条件
		contextualURL.scannable = DrawlerUtilities.isScannable(contextualURL, drawler.urlScan);
		contextualURL.downloadable = DrawlerUtilities.isDownloadable(contextualURL, drawler.urlDownload);
		if (!drawler.isVisitedLink(contextualURL.url)) {
			drawler.addVisitedLink(contextualURL.url);
		}
		
		// 3.扫描文件，将符合浏览要求的URL加入队列
		InputStream inputstream = null;
		if (contextualURL.scannable || contextualURL.downloadable) {
			inputstream = scanHTML(connection, contextualURL);
		}

		// 4.下载满足下载要求的文件
		if (contextualURL.downloadable) {
			File outputfile;
			URL fileurl = contextualURL.url;
			String filename = fileurl.getFile().replace("/", "-").replaceAll("[\\?=]", "-");
			outputfile = new File(drawler.localpath, filename);
			System.out.println("downloading to " + outputfile);
			drawler.updateResult(DrawlerUtilities.writeToDisk(inputstream,
					outputfile));
		}

		connection.disconnect();

	}

	// 扫描URL文件，返回输出流
	@SuppressWarnings("static-access")
	public InputStream scanHTML(HttpURLConnection connection,
			ContextualURL contextualURL) {

		InputStream inputstream = null;

		// 下载文件内容
		String content = DrawlerUtilities.downloadStringResource(connection);
		if (content == null) {
			System.out.println("unable to download " + connection.getURL());
			return null;
		}

		// 地址允许扫描，且深度小于要求深度，则继续扫描
		if (contextualURL.scannable && contextualURL.scanDepth <= drawler.scanDepth) {

			// 寻找<a href="...">标签，得到href值
			Pattern anchor = Pattern
					.compile("<( )*a .*?href( )*=( )*[\'\"].*?[\'\"].*?>");
			Matcher matcher = anchor.matcher(content);

			int top = 0;
			// 将页面内的前topN个URL加入队列
			while (matcher.find()) {
				// 得到URL对象
				String a = matcher.group();
				boolean inside = false;
				int sub_start = 0, sub_end = a.length() - 1;
				char quot = '\"';
				for (int i = a.indexOf("href"); i < a.length(); i++) {
					if (!inside && (a.charAt(i) == '\'' || a.charAt(i) == '\"')) {
						quot = a.charAt(i);
						sub_start = i + 1;
						inside = true;
						continue;
					} else if (inside && a.charAt(i) == quot) {
						sub_end = i;
						break;
					}
				}
				String link = a.substring(sub_start, sub_end);
				// System.out.println("Link found: " + link);
				URL linkurl = null;
				try {
					linkurl = new URL(link);
				} catch (Exception e) {
					try {
						if (link.startsWith("?"))
							linkurl = new URL(connection.getURL().toString().split("[?]")[0] +  link);
						else if (link.startsWith("/")) {
							linkurl = new URL("http://" + connection.getURL().getHost() + link);
						}
						else {
							String temp = connection.getURL().toString();
							linkurl = new URL(temp.substring(0, temp.lastIndexOf("/") + 1) + link);
						}
					} catch (Exception e2) {
						System.out.println("invalid link: " + link);
						continue;
					}
				}
				// System.out.println("Link found: " + linkurl.toString());

				// 保存新找到的链接信息，是否可继续浏览，是否可下载
				int linkscandepth = contextualURL.scanDepth;
				ContextualURL newcontextualURL = new ContextualURL(
						contextualURL.url, linkurl, linkscandepth + 1);
				newcontextualURL.originalLink = link;
				newcontextualURL.scannable = DrawlerUtilities.isScannable(
						newcontextualURL, drawler.urlScan);

				newcontextualURL.downloadable = DrawlerUtilities.isDownloadable(
						newcontextualURL, drawler.urlDownload) 
						&& top < drawler.downloadCount;

				// 是否链接已经浏览过
				boolean alreadyprocessed = false;
				if (drawler.isVisitedLink(linkurl)) {
					alreadyprocessed = true;
				} else {
					drawler.addVisitedLink(linkurl);
				}

				// 将新地址加入队列
				if (!alreadyprocessed) { 
					if (newcontextualURL.scannable || newcontextualURL.downloadable) {
						String msg = "link found: ";
						drawler.addQueueLink(newcontextualURL);
						if (newcontextualURL.downloadable) {
							msg = "resources found: ";
							top ++;
						}
						System.out.println(msg + linkurl);
					}
				}
			}
		}

		inputstream = new ByteArrayInputStream(content.getBytes());
		return inputstream;
	}

}

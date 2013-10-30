package examples.HongXiaoDong;

import drawler.crawl.Drawler;

public class CrawlAccept {

	public static void main(String[] args) throws Exception {
		
		Drawler drawler = new Drawler();

		String startURL = "http://www.eol.cn/html/g/2013zyjh/";
		
		drawler.scanDepth = 1000;
		drawler.downloadCount = 3000;
		drawler.maxThreads = 10;
		drawler.localpath = "output/www_eol_cn/accept/";

		drawler.urlScan.add("http://www\\.eol\\.cn/html/g/2013zyjh/");
		drawler.urlScan.add("http://www\\.eol\\.cn/html/g/2013zyjh/[0-9]+/[0-9]+\\.shtml");

		drawler.urlDownload.add("http://www\\.eol\\.cn/html/g/2013zyjh/[0-9]+/[0-9]+\\.shtml");
		drawler.urlDownload.add("http://www\\.eol\\.cn/html/g/2013zyjh/[0-9]+/\\.\\./[0-9]+/[0-9]+\\.shtml");

		drawler.followLink(startURL);

	}

}

package drawler.crawl;

import java.util.*;
import java.text.*;
import java.net.*;
import java.io.*;

public class Drawler {

    // 缓存地址
    public String localpath;
    public boolean isEnd;

    // 线程集合
    public  int maxThreads;
    private Set threads;
    private ThreadGroup threadGroup;

    // URL正则
    public  List<String> urlScan;
    public  int scanDepth;
    public  List<String> urlDownload;
    public  int downloadCount;
    
    // 已访问队列
    private Object visitedURLLock;
    private Queue<ContextualURL> visitedUrls;
    
    // 待访问队列
    private Object queuedURLLock;
    private Queue<ContextualURL> queuedUrls;
    
    // 结果统计
    private Object writtenResultLock;
    private long writtenBytesCount;
    private long writtenFilesCount;
    private long startTime;
    
    // 延时控制
    public  boolean toPause;
    public  long pauseperconnections;
    public  long pausemseconds;
    private Object pauseLock;
    private boolean isPaused;
    private long connectedPagesCount;
    private long loopConnectedPagesCount;

    public Drawler() throws Exception {

	maxThreads = 1;
        threads = new HashSet(32);
        threadGroup = new ThreadGroup("Drawler threads");

        visitedURLLock = new Object();
        queuedURLLock = new Object();
        writtenResultLock = new Object();
        pauseLock = new Object();

	urlScan = new Vector<String>();
	urlDownload = new Vector<String>();

	pauseperconnections = 0;
	pausemseconds = 0;
	toPause = false;
        
        isPaused = false;

	isEnd = false;

	// By default, we use LinkedList as URLQueue
	visitedUrls = new LinkedList<ContextualURL>();
	queuedUrls = new LinkedList<ContextualURL>();
    }

    public void setVisitedURLCache(Queue<ContextualURL> queue) {
	this.visitedUrls = queue;
    }

    public void setQueueURLCache(Queue<ContextualURL> queue) {
	this.queuedUrls = queue;
    }

    // 抓取一个URL
    public void followLink(ContextualURL contextualURL) {
	contextualURL.scannable = true;
	contextualURL.downloadable = DrawlerUtilities.isDownloadable(contextualURL, urlDownload);
	queuedUrls.offer(contextualURL);
        startThread();
    }

    public void followLink(String url) throws Exception {
	URL u = new URL(url);
	ContextualURL contextualurl = new ContextualURL(u, 1);
	followLink(contextualurl);
    }

    // 启动一个新的线程
    public void startThread() {

        if (threadGroup.activeCount() < this.maxThreads) {
            DrawlerThread aralethread = new DrawlerThread(this);
            new Thread(threadGroup, aralethread).start();
        }
    }

    // 所有线程结束后，执行
    public void endProcess() {

        long processingTime = System.currentTimeMillis() - startTime;
        String str_processingtime;
        if (processingTime < 1000) {
            str_processingtime = processingTime + "ms";
        } else {
            str_processingtime = processingTime / 1000 + "sec";
        }
        System.out.println("processing time: " + str_processingtime);

        NumberFormat nf = NumberFormat.getInstance();

        double megstodisk = (double) writtenBytesCount / 1048576.0;
        System.out.println("downloaded " + writtenFilesCount + " files.");
        System.out.println("written to disk: " 
        		+ nf.format(writtenBytesCount) 
        		+ "bytes (" 
        		+ nf.format(megstodisk) 
        		+ "Mb)");

	isEnd = true;
    }

    // 线程管理
    public void addThread(DrawlerThread thread) {
	this.threads.add(thread);
    }

    public void removeThread(DrawlerThread thread) {
	this.threads.remove(thread);
    }

    public int getThreadSize() {
	return this.threads.size();
    }

    public boolean isNoThreads() {
        return this.threads.isEmpty();
    }

    // 加锁的变量访问函数，防止多线程中数据错误
    public void addVisitedLink(ContextualURL link) {
    	synchronized(visitedURLLock) {
    		visitedUrls.offer(link);
    	}
    }
    
    public boolean isVisitedLink(ContextualURL link) {
    	synchronized(visitedURLLock) {
    		return this.visitedUrls.contains(link);
    	}
    }
    
    public void addQueueLink(ContextualURL contextualURL) {
    	synchronized(queuedURLLock) {
    		queuedUrls.offer(contextualURL);
    	}
    }
    
    public int getQueuedSize() {
    	synchronized(queuedURLLock) {
    		return queuedUrls.size();
    	}
    }
    
    public ContextualURL popQueuedURL() {
    	synchronized(queuedURLLock) {
    		return (ContextualURL)queuedUrls.poll();
    	}
    }
    
    public void updateResult(long bytewritten) {
    	synchronized(writtenResultLock) {
    		writtenBytesCount += bytewritten;
    		writtenFilesCount ++;
    	}
    }
    
    public boolean updateConnected(long num) {
    	synchronized(pauseLock) {
    		try {
			if (toPause) {
				long loop = this.pauseperconnections;
				connectedPagesCount += num;
				loopConnectedPagesCount += num;
				if (loopConnectedPagesCount >= loop) {
					loopConnectedPagesCount = 0;
					System.out.println("Get into pause: " 
							+ this.pausemseconds + "milliseconds later will recover.");
					Thread.currentThread().sleep(this.pausemseconds);
				}
			}
    		}
    		catch (InterruptedException e){
    			System.out.println(e);
    		}
    	}
    	return isPaused;
    }
}

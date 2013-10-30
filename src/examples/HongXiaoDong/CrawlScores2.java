package examples.HongXiaoDong;

import drawler.crawl.Drawler;

public class CrawlScores2 {

	public static void main(String[] args) throws Exception {
		
		int maxUnits = 10;
		Drawler[] drawlers = new Drawler[maxUnits];

		int pages = 103, index = 0;
		for (int page = 11; page <= pages; page++) {
			String url = "http://college.gaokao.com/schlist/p" + page + "/";
			while (drawlers[index]!=null && !drawlers[index].isEnd) {
				index = (index + 1) % drawlers.length;
			}	
			drawlers[index] = new Drawler();
			drawlers[index].scanDepth = 3;
			drawlers[index].downloadCount = 3000;
			drawlers[index].maxThreads = 10;
			drawlers[index].localpath = "output/www_gaokao_com/scores/";
			drawlers[index].urlDownload.add("http://college\\.gaokao\\.com/school/tinfo/[0-9]+/result/[0-9]+/[0-9]+/");
			drawlers[index].urlScan.add("http://college\\.gaokao\\.com/school/[0-9]+/");
			drawlers[index].urlScan.add("http://college\\.gaokao\\.com/school/tinfo/[0-9]+/result/[0-9]+/[0-9]+/");
			drawlers[index].urlScan.add("http://college\\.gaokao\\.com/schlist/p" + page + "/");
			drawlers[index].followLink(url);
			
			index = (index + 1) % drawlers.length;
		}
	}
}

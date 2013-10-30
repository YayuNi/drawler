package examples.HongXiaoDong;

import drawler.crawl.Drawler;

public class CrawlSchool {
	
	public static void main(String[] args) throws Exception {
		Drawler drawler = new Drawler();
	
		String startURL = "http://gkcx.eol.cn/soudaxue/queryschool.html?province=&schooltype=&keyWord1=&x=21&y=4&page=1";
		drawler.urlScan.add("http://gkcx\\.eol\\.cn/soudaxue/queryschool\\.html\\?province=&schooltype=&keyWord1=&x=21&y=4&page=[0-9]+");
		drawler.urlDownload.add("http://gkcx\\.eol\\.cn/soudaxue/queryschool\\.html\\?province=&schooltype=&keyWord1=&x=21&y=4&page=[0-9]+");

		drawler.scanDepth = 1000;
		drawler.downloadCount = 1000;
		drawler.maxThreads = 10;

		drawler.localpath = "output/www_gkcx_eol_cn/schools/";
		drawler.followLink(startURL);
	}

}

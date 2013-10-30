package examples.HongXiaoDong;

import drawler.crawl.Drawler;

public class CrawlProvince {
	
	public static void main(String[] args) throws Exception {
		Drawler drawler = new Drawler();
	
		String startURL = "http://gkcx.eol.cn/soudaxue/queryProvince.html?keyWord=&page=1";
		drawler.urlScan.add("http://gkcx\\.eol\\.cn/soudaxue/queryProvince\\.html\\?keyWord=&page=[0-9]+");
		drawler.urlDownload.add("http://gkcx\\.eol\\.cn/soudaxue/queryProvince\\.html\\?keyWord=&page=[0-9]+");

		drawler.scanDepth = 100;
		drawler.downloadCount = 100;
		drawler.maxThreads = 5;

		drawler.localpath = "output/www_gkcx_eol_cn/province/";
		drawler.followLink(startURL);
	}

}

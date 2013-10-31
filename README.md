drawler
=======

A lightweght Java framework for web pages crawling and datas matching.
This is a beta version, so some errors may occur when running.

Example 1: Crawl web pages from Internet
------
```Java
import drawler.crawl.Drawler;

public class HelloCrawler {

	public static void main(String args[]) {
		
		Drawler drawler = new Drawler();
		
		// The start pages that want to crawl
		String[] startURLs = new String[] {
			"http://www.dianping.com/member/4200487/reviews?pg=1&reviewCityId=0&reviewShopType=0&c=0&shopTypeIndex=0",
			"http://www.dianping.com/member/2691446/reviews?pg=1&reviewCityId=0&reviewShopType=0&c=0&shopTypeIndex=0",
			"http://www.dianping.com/member/437769/reviews?pg=1&reviewCityId=0&reviewShopType=0&c=0&shopTypeIndex=0",
			"http://www.dianping.com/member/50227/reviews?pg=1&reviewCityId=0&reviewShopType=0&c=0&shopTypeIndex=0",
			"http://www.dianping.com/member/19148264/reviews?pg=1&reviewCityId=0&reviewShopType=0&c=0&shopTypeIndex=0"
		};
		
		// Add the URL regex that want to scan for more links
		drawler.urlScan.add("http://www\\.dianping\\.com/member/[0-9]+/reviews\\?pg=[0-9]+&reviewCityId=0&reviewShopType=10&c=0&shopTypeIndex=1");
		// Add the URL regex that want to download
		drawler.urlDownload.add("http://www\\.dianping\\.com/member/[0-9]+/reviews\\?pg=[0-9]+&reviewCityId=0&reviewShopType=10&c=0&shopTypeIndex=1");
		
		// Set the scan depth of the drawler
		drawler.scanDepth = 100;
		// Set the count of pages to download in scanned page
		drawler.downloadCount = 10;
		
		// Set the number of thread to run the program
		drawler.maxThreads = 10;
		
		// Set the milliseconds pause per N connections
		drawler.toPause = true;
		drawler.pauseperconnections = 200;
		drawler.pausemseconds = 500000;
		
		// The folder to save the downloaded web pages
		drawler.localpath = "cache/www_dianping_com/";
		
		// Start the crawler
		for (String url : startURLs)
			drawler.followLink(url);
	}
	
}
```


Example 2: Find objective datas from downloaded web pages
------
```Java
import drawler.match.Matcher;
import drawler.match.io.FileInput;
import drawler.match.sql.SQLOutput;
import drawler.match.pattern.Filter;
import drawler.match.pattern.html.HTMLPattern;

public class HelloMatcher {

	public static void main(String[] args) {
		
		// The matcher object to find datas in web pages;
		Matcher matcher = new Matcher();

		// The file system source object. It can be expanded to other sources, like databases or online pages
		FileInput input = new FileInput();
		input.addPath("cache/www_dianping_com/");
		matcher.setInput(input);
		
		// The mysql output obejct. Can be expanded to other outputer, like file system.
		// The arguments are: database URL, user name, password, database name, table name
		SQLOutput output = new SQLOutput("localhost", "root", "root", "www_dianping_com", "peoples");
		matcher.setOutput(output);
		
		matcher.addPattern("shopID", "INT", new HTMLPattern(
			"<a href=\"http://www.dianping.com/shop/",
			"[0-9]+",
			"\" class=\"J_rpttitle\" title=\"\""));
			
		matcher.addPattern("taste", "INT", new HTMLPattern(
			"<span>口味：",
			"[0-9]",
			"<em class"));
			
		matcher.addPattern("environment", "INT", new HTMLPattern(
			"<span>环境：",
			"[0-9]",
			"<em class"));
			
		matcher.addPattern("service", "INT", new HTMLPattern(
			"<span>服务：",
			"[0-9]",
			"<em class"));
			
		matcher.addPattern("comment", "VARCHAR(1000)", new HTMLPattern(
			"<div class=\"mode-tc comm-entry\">",
			".*?",
			"</div>",
			new Filter() {
				public String filter(String content) {
					return content.replace("<br/>","");
				}
			}));
			
		matcher.addPattern("date", "DATE", new HTMLPattern(
			"<span class=\"col-exp\">.*?于",
			".*?",
			"</span>",
			new Filter() {
				public String filter(String content) {
					return "20" + content;
				}
			}));
			
		matcher.match();
	}
	
}
```

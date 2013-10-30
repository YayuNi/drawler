package examples.XinLe;

import drawler.crawl.Drawler;

public class Crawl {

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
            drawler.followLink(startURL);
    }

}

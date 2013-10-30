package examples.HongXiaoDong;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.net.*;

import drawler.crawl.Drawler;

public class CrawlScores {

	public static void main(String[] args) throws Exception {

		int maxUnits = 10;
		Drawler[] drawlers = new Drawler[maxUnits];
		String urlScan = "http://gkcx\\.eol\\.cn/schoolhtm/schoolSpecailtyMark/[0-9]+/schoolSpecailtyMark\\.htm";
		String urlDownload = "http://gkcx\\.eol\\.cn/schoolhtm/schoolPoint/[0-9]+/(li|wen|zong|tiyu|yishu)/schoolAreaPoint_[0-9]+_.*?\\.htm";
		for (int i=0; i<maxUnits; i++) {
			drawlers[i] = new Drawler();
			drawlers[i].urlScan.add(urlScan);
			drawlers[i].urlDownload.add(urlDownload);
			drawlers[i].scanDepth = 100;
			drawlers[i].downloadCount = 1000;
			drawlers[i].maxThreads = 1;
			drawlers[i].localpath = "output/www_gkcx_eol_cn/scores/";
		}

		// Read SchoolID from mysql
		String mysql_driver = "com.mysql.jdbc.Driver";
		String mysql_url = "jdbc:mysql://localhost:3306/?useUnicode=true&characterEncoding=UTF8";
		Class.forName(mysql_driver);
		Connection con = DriverManager.getConnection(mysql_url, "root", "root");
		Statement state = con.createStatement();
		String sql = "SELECT schoolID FROM www_gkcx_eol_cn.schools ORDER BY schoolID";
		ResultSet rs = state.executeQuery(sql);

		int index = 0;
		while (rs.next()) {
			int schoolID = rs.getInt("schoolID");
			String url = "http://gkcx.eol.cn/schoolhtm/schoolSpecailtyMark/" + schoolID + "/schoolSpecailtyMark.htm";
			while (!drawlers[index].isNoThreads()) {
				index = (index + 1) % drawlers.length;
			}
			drawlers[index].followLink(url);
		}
		con.close();
	}
}

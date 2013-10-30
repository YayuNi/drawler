package examples.HongXiaoDong;

import java.util.*;
import java.sql.*;

import drawler.match.Matcher;
import drawler.match.io.FileInput;
import drawler.match.sql.SQLOutput;
import drawler.match.pattern.Filter;
import drawler.match.pattern.Matchable;
import drawler.match.pattern.html.HTMLPattern;

public class MatchProvince {

	public static void main(String[] args) {
		Matcher matcher = new Matcher();

		FileInput input = new FileInput();
		input.addPath("output/www_gkcx_eol_cn/province");
		matcher.setInput(input);

		SQLOutput db = new SQLOutput("localhost", "root", "root", "www_gkcx_eol_cn", "provinces");
		matcher.setOutput(db);

		// Province
		matcher.addPattern("Province", "VARCHAR(500)", new HTMLPattern(
			"<td width=\"20%\" height=\"30\" align=\"center\" class=\"white_border_left01\">[ ]*",
			".*?",
			"[ ]*</td>"));
		
		// Year
		matcher.addPattern("Year", "INT", new HTMLPattern(
			"<td width=\"20%\" align=\"center\" class=\"white_border_left01\">[ ]*",
			"[0-9]+",
			"年")); 
		
		// Subject
		matcher.addPattern("Subject", "VARCHAR(500)", new HTMLPattern(
			"<td width=\"20%\" colspan=\"5\" align=\"center\" class=\"white_border_left01\">[ ]*",
			".*?",
			"[ ]*</td>"));

		// Batch
		matcher.addPattern("Batch", "VARCHAR(500)", new HTMLPattern(
			"<td width=\"20%\" align=\"center\" class=\"white_border_left01\">[ ]*",
			"[\u4E00-\u9FA5]+批",
			"[ ]*</td>"));

		// Score
		matcher.addPattern("Score", "INT", new HTMLPattern(
			"<td  width=\"20%\" align=\"center\" >[ ]*",
			"[0-9]+",
			"[ ]*</td>"));

		matcher.match();
	}
}

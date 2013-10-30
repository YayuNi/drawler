package examples.HongXiaoDong;

import java.util.*;
import java.sql.*;

import drawler.match.Matcher;
import drawler.match.io.FileInput;
import drawler.match.sql.SQLOutput;
import drawler.match.pattern.Filter;
import drawler.match.pattern.Matchable;
import drawler.match.pattern.html.HTMLPattern;

public class MatchSpeciality {

	public static void main(String[] args) {
		Matcher matcher = new Matcher();

		FileInput input = new FileInput();
		input.addPath("output/www_gkcx_eol_cn/speciality");
		matcher.setInput(input);

		SQLOutput db = new SQLOutput("localhost", "root", "root", "www_gkcx_eol_cn", "speciality");
		matcher.setOutput(db);

		// ID
		matcher.addPattern("ID", "INT", new HTMLPattern(
			"title=\"专业国标码:",
			"[0-9]+",
			"\"")); 

		// Name
		matcher.addPattern("Name", "VARCHAR(500)", new HTMLPattern(
			"<a  class=\"black\" href=\"/sphtm/[0-9]+/sp[0-9]+\\.htm\" >[ ]*",
			".*?",
			"[ ]*(</a>)?[ ]*</td>"));
		
		// Class
		matcher.addPattern("Class", "VARCHAR(500)", new HTMLPattern(
			"<a  class=\"black\" href=\"/sphtm/[0-9]+/spIntro.htm#[0-9]+\" >",
			".*?",
			"</a>"));

		// Category
		matcher.addPattern("Category", "VARCHAR(500)", new HTMLPattern(
			"<td width=\"10%\" align=\"center\"  class=\"white_border_left01\">[ ]*",
			"[\u4E00-\u9FA5]+",
			"[ ]*</td>"));

		matcher.match();
	}
}

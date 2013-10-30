package examples.HongXiaoDong;

import java.util.*;
import java.sql.*;

import drawler.match.Matcher;
import drawler.match.io.FileInput;
import drawler.match.sql.SQLOutput;
import drawler.match.pattern.Filter;
import drawler.match.pattern.Matchable;
import drawler.match.pattern.html.HTMLPattern;

public class MatchSchool {

	public static void main(String[] args) {
		Matcher matcher = new Matcher();

		FileInput input = new FileInput();
		input.addPath("output/www_gkcx_eol_cn/schools/");
		matcher.setInput(input);

		SQLOutput db = new SQLOutput("localhost", "root", "root", "www_gkcx_eol_cn", "school");
		matcher.setOutput(db);

		// SchoolID
		matcher.addPattern("SchoolID", "INT", new HTMLPattern(
			"<a  href=\"/schoolhtm/schoolTemple/school",
			"[0-9]+",
			"\\.htm\" class=\"black"));
		
		// SchoolName
		matcher.addPattern("SchoolName", "VARCHAR(500)", new HTMLPattern(
			"<a  href=\"/schoolhtm/schoolTemple/school[0-9]+\\.htm\" class=\"black\" title=\"",
			".*?",
			"\">")); 
		
		// Province
		matcher.addPattern("Province", "VARCHAR(500)", new HTMLPattern(
			"<td width=\"9%\" align=\"center\"   class=\"white_border_left01\">",
			".*?",
			"</td>"));

		// SchoolType
		matcher.addPattern("SchoolType", "VARCHAR(500)", new HTMLPattern(
			"<td width=\"12%\" align=\"center\"   class=\"white_border_left01\" title=\"",
			".*?",
			"\">"));

		// JiaoYuBu
		matcher.addPattern("JiaoYuBu", "VARCHAR(500)", new HTMLPattern(
			"<td width=\"12%\" align=\"center\"   class=\"white_border_left01\">[ ]+",
			".*?",
			"[ ]+</td>"));

		// 985
		matcher.addPattern("Is985", "VARCHAR(500)", new HTMLPattern(
			"<td width=\"10%\" align=\"center\"   class=\"white_border_left01\">[ ]+",
			".*?",
			"[ ]+</td>"));

		// 211
		matcher.addPattern("Is211", "VARCHAR(500)", new HTMLPattern(
			"<td width=\"10%\" align=\"center\"  >[ ]+",
			".*?",
			"[ ]+</td>"));

		matcher.match();
	}
}

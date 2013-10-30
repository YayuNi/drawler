package examples.HongXiaoDong;

import java.util.*;
import java.sql.*;

import drawler.match.Matcher;
import drawler.match.io.FileInput;
import drawler.match.sql.SQLOutput;
import drawler.match.pattern.Filter;
import drawler.match.pattern.Matchable;
import drawler.match.pattern.html.HTMLPattern;

public class MatchScores {

	public static void main(String[] args) {
		Matcher matcher = new Matcher();

		FileInput input = new FileInput();
		input.addPath("output/www_gkcx_eol_cn/scores");
		matcher.setInput(input);

		SQLOutput db = new SQLOutput("localhost", "root", "root", "www_gkcx_eol_cn", "score_details");
		matcher.setOutput(db);

		// School ID
		matcher.addPattern("SchoolID", "INT", new HTMLPattern(
			"<div class=\"m_right_nav\"><ul><li><a  href=\"/schoolhtm/schoolTemple/school",
			"[0-9]+",
			"\\.htm"));
		
		// School Name
		matcher.addPattern("SchoolName", "VARCHAR(500)", new HTMLPattern(
			"<div align=\"center\" ><h2>",
			".*?",
			"<font color='red'>")); 
		
		// Admission Year
		matcher.addPattern("Year", "INT", new HTMLPattern(
			"<font color='red'>",
			"[0-9]+",
			"</font>"));

		// Province
		matcher.addPattern("Province", "VARCHAR(500)", new HTMLPattern(
			"在<font color='red'>",
			".*?",
			"</font>"));

		// Subject
		matcher.addPattern("Subject", "VARCHAR(500)", new HTMLPattern(
			"<font color='red'>",
			"[\\u4E00-\\u9FFF]+",
			"</font></h2></div>"));

		// Major
		matcher.addPattern("Major", "VARCHAR(500)", new HTMLPattern(
			"<td bgcolor=\"#F2F2F2\">[ ]*",
			"[\\u4E00-\\u9FFF]+",
			"[ ]*</td>"));

		// Average Score
		matcher.addPattern("Average", "VARCHAR(100)", new HTMLPattern(
			"<td bgcolor=\"#F2F2F2\">.*?<td bgcolor=\"#FFFFFF\">",
			"([0-9]+|--)",
			"</td>"));

		// Maximum Score
		matcher.addPattern("Maximum", "VARCHAR(100)", new HTMLPattern(
			"<td bgcolor=\"#F2F2F2\">.*?<td bgcolor=\"#FFFFFF\">.*?<td bgcolor=\"#FFFFFF\">",
			"([0-9]+|--)",
			"</td>"));

		// Minimum Score
		matcher.addPattern("Minimum", "VARCHAR(100)", new HTMLPattern(
			"<td bgcolor=\"#F2F2F2\">.*?<td bgcolor=\"#FFFFFF\">.*?<td bgcolor=\"#FFFFFF\">.*?<td bgcolor=\"#FFFFFF\">",
			"([0-9]+|--)",
			"</td>"));

		// Batch
		matcher.addPattern("Batch", "VARCHAR(100)", new HTMLPattern(
			"<td bgcolor=\"#F2F2F2\">.*?<td bgcolor=\"#FFFFFF\">.*?<td bgcolor=\"#FFFFFF\">.*?<td bgcolor=\"#FFFFFF\">.*?<td bgcolor=\"#FFFFFF\">[ ]*",
			".*?",
			"[ ]*</td>"));

		matcher.match();
	}
}


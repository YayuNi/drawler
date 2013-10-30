package examples.HongXiaoDong;

import java.util.*;
import java.sql.*;

import drawler.match.Matcher;
import drawler.match.io.FileInput;
import drawler.match.sql.SQLOutput;
import drawler.match.pattern.Filter;
import drawler.match.pattern.Matchable;
import drawler.match.pattern.html.HTMLPattern;

public class MatchScores2 {

	public static void main(String[] args) {
		Matcher matcher = new Matcher();

		FileInput input = new FileInput();
		input.addPath("output/www_gaokao_com/scores/");
		matcher.setInput(input);

		SQLOutput db = new SQLOutput("localhost", "root", "root", "www_gaokao_com", "scores");
		matcher.setOutput(db);

		// School Name
		matcher.addPattern("SchoolName", "VARCHAR(500)", new HTMLPattern(
			"<p class=\"btnFsxBox\"><font>",
			".*?",
			"</font>"));
		
		// Province
		matcher.addPattern("Province", "VARCHAR(500)", new HTMLPattern(
			"各省市录取分数线（<font>",
			".*?",
			"</font>")); 
		
		// Subject
		matcher.addPattern("Subject", "VARCHAR(500)", new HTMLPattern(
			"→<font>",
			".*?",
			"</font>）"));

		// Year
		matcher.addPattern("Year", "INT", new HTMLPattern(
			"<tr class=\"sz[w]?\">[ ]*<td>",
			"[0-9]+",
			"</td>"));

		// Min
		matcher.addPattern("Min", "VARCHAR(100)", new HTMLPattern(
			"<tr.*?>[ ]*<td>[0-9]+</td>[ ]*<td.*?>",
			"([0-9]+|------)",
			"</td>"));

		// Max
		matcher.addPattern("Max", "VARCHAR(100)", new HTMLPattern(
			"<tr.*?>[ ]*<td>[0-9]+</td>[ ]*<td.*?>([0-9]+|------)</td>[ ]*<td.*?>",
			"([0-9]+|------)",
			"</td>"));

		// Average
		matcher.addPattern("Average", "VARCHAR(100)", new HTMLPattern(
			"id=\"pjf.*?blank\">",
			"([0-9]+|------)",
			"</a>"));

		// TouDang
		matcher.addPattern("TouDang", "VARCHAR(100)", new HTMLPattern(
			"id=\"pjf.*?blank\">.*?<td.*?>",
			"([0-9]+|------)",
			"</td>"));

		// Accept
		matcher.addPattern("Accept", "VARCHAR(100)", new HTMLPattern(
			"id=\"pjf.*?blank\">.*?<td.*?>.*?</td>[ ]*<td.*?>",
			"([0-9]+|------)",
			"</td>"));

		// Batch
		matcher.addPattern("Batch", "VARCHAR(100)", new HTMLPattern(
			"<td>",
			"第.*?批",
			"</td>"));

		matcher.match();
	}
}


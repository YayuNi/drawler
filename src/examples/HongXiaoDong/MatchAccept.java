package examples.HongXiaoDong;

import drawler.match.Matcher;
import drawler.match.io.FileInput;
import drawler.match.sql.SQLOutput;
import drawler.match.pattern.Filter;
import drawler.match.pattern.Matchable;
import drawler.match.pattern.html.HTMLPattern;

public class MatchAccept {

	public static void main(String[] args) {
		Matcher matcher = new Matcher();

		FileInput input = new FileInput();
		input.addPath("output/www_eol_cn/accept/");
		matcher.setInput(input);

		SQLOutput db = new SQLOutput("localhost", "root", "root", "www_eol_cn", "accepts");
		matcher.setOutput(db);

		// School Name
		matcher.addPattern("SchoolName", "VARCHAR(500)", new HTMLPattern(
			"<h1>",
			".*?",
			"<span>"));
		
		// Province
		matcher.addPattern("Province", "VARCHAR(500)", new HTMLPattern(
			"<span>.*?高招",
			".*?",
			"地区")); 
		
		// Year
		matcher.addPattern("Year", "INT", new HTMLPattern(
			"<span>",
			"[0-9]+",
			"年"));

		// Major
		matcher.addPattern("Major", "VARCHAR(100)", new HTMLPattern(
			"<tr class=\"c[0-9]{1}\"><td class=\"left\">",
			".*?",
			"</td>"));

		// Type
		matcher.addPattern("Type", "VARCHAR(100)", new HTMLPattern(
			"<tr.*?<td.*?<td>",
			".*?",
			"</td>"));

		// Level
		matcher.addPattern("Level", "VARCHAR(100)", new HTMLPattern(
			"<tr.*?<td.*?<td.*?<td>",
			".*?",
			"</td>"));

		// Subject
		matcher.addPattern("Subject", "VARCHAR(100)", new HTMLPattern(
			"<tr.*?<td.*?<td.*?<td.*?<td>",
			".*?",
			"</td>"));

		// Accept
		matcher.addPattern("Accept", "INT", new HTMLPattern(
			"<td>",
			"[0-9]+",
			"</td>"));

		matcher.match();
	}
}


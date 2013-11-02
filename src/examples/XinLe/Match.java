package examples.XinLe;

import drawler.match.Matcher;
import drawler.match.io.FileInput;
import drawler.match.sql.SQLOutput;
import drawler.match.pattern.Filter;
import drawler.match.pattern.html.HTMLPattern;

public class Match {

    public static void main(String[] args) {

        // The matcher object to find datas in web pages;
        Matcher matcher = new Matcher();

        // The file system source object. It can be expanded to other sources, like databases or online pages
        FileInput input = new FileInput();
        input.addPath("cache/www_dianping_com/");
        matcher.setInput(input);

        // The mysql output obejct. Can be expanded to other outputer, like file system.
        // The arguments are: database URL, user name, password, database name, table name
        SQLOutput output = new SQLOutput("localhost", "root", "root", "www_dianping_com", "members");
        matcher.setOutput(output);

	matcher.addPattern("userID", "INT", new HTMLPattern(
		"\"/msg/send/",
		"[0-9]+",
		"\""));

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

        matcher.addPattern("comment", "TEXT", new HTMLPattern(
            "<div class=\"mode-tc comm-entry\">",
            ".*?",
            "</div>",
            new Filter() {
                public String filter(String content) {
                    return content.replace("<br/>","").replace("<br>","").replace("&nbsp;","");
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

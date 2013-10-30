package drawler.match.pattern.html;

import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import drawler.match.pattern.Matchable;
import drawler.match.pattern.Filter;

public class HTMLPattern extends Matchable {
	
	// 数据的匹配格式, 以正则表达式和自定义数据格式的混合为主
	private String leftRegex, rightRegex;
	
	// 数据的匹配类型
	private String dataRegex;
	
	// 构造函数
	public HTMLPattern() {
		super();
	}
	
	public HTMLPattern(String leftRegex, String dataRegex, String rightRegex, Filter filter) {
		super();
		this.leftRegex = leftRegex;
		this.dataRegex = dataRegex;
		this.rightRegex = rightRegex;
		this.filter = filter;
	}
	
	public HTMLPattern(String leftRegex, String dataRegex, String rightRegex) {
		super();
		this.leftRegex = leftRegex;
		this.dataRegex = dataRegex;
		this.rightRegex = rightRegex;
	}
	
	public void setLeftRegex(String leftRegex) {
		this.leftRegex = leftRegex;
	}
	
	public String getLeftRegex() {
		return leftRegex;
	}
	
	public void setRightRegex(String rightRegex) {
		this.rightRegex = rightRegex;
	}
	
	public String getRightRegex() {
		return rightRegex;
	}
	
	public void setDataRegex(String dataRegex) {
		this.dataRegex = dataRegex;
	}
	
	public String getDataRegex() {
		return dataRegex;
	}

	// 返回匹配数据
	public List<String> run(String srcContent) {
		List<String> result = new Vector<String>();
		
		String regex = leftRegex + dataRegex + rightRegex;
		Pattern patAll = Pattern.compile(regex);
		Pattern patLeft = Pattern.compile(leftRegex);
		Pattern patRight = Pattern.compile(rightRegex);
		Matcher m = patAll.matcher(srcContent);
		while (m.find()) {
			String strAll = m.group();
			String strLeft = "", strRight = "";
			
			Matcher mLeft = patLeft.matcher(strAll);
			if (mLeft.find()) {
				strLeft = mLeft.group();
				strAll = strAll.replace(strLeft, "");
			}

			Matcher mRight = patRight.matcher(strAll);
			if (mRight.find()) {
				strRight = mRight.group();
				strAll = strAll.replace(strRight, "");
			}
			result.add(strAll);
		}
		return result;
	}
}

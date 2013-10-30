package drawler.match;

import java.util.List;
import java.util.Vector;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import drawler.match.pattern.Matchable;

public class Matcher {
	
	public enum Mode {
		NO_LOOP, MIN_LOOP, MAX_LOOP
	};
	
	protected Mode mode = Mode.NO_LOOP;
	// 在Matcher类中，只关心检索，而不关心输入输出的具体对象
	protected Inputable in;
	protected Outputable out;
	
	// 检索模式的集合
	protected List<Matchable> patterns;
	
	public Matcher() {
		patterns = new Vector<Matchable>();
	}
	
	public Matcher(Inputable in, Outputable out) {
		patterns = new Vector<Matchable>();
		setInput(in);
		setOutput(out);
	}
	
	public void setInput(Inputable in) {
		this.in = in;
	}
	
	public void setOutput(Outputable out) {
		this.out = out;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}
	
	public int addPattern(String title, String description, Matchable pattern) {
		pattern.setinfo(title, description);
		this.patterns.add(pattern);
		return this.patterns.size();
	}
	
	public int addPattern(String title, Matchable pattern) {
		pattern.setinfo(title, "");
		this.patterns.add(pattern);
		return this.patterns.size();
	}
	
	public int addPattern(Matchable pattern) {
		this.patterns.add(pattern);
		return this.patterns.size();
	}
	
	// 得到匹配结果的函数
	public void match() {

		if (!this.out.open(this.patterns)) {
			System.err.println("\nOpen output destination failed.");
			return;
		}

		while (in.hasNext()) {
			String content = in.next();
			String currentSrc = in.current();
			Iterator<Matchable> it = this.patterns.iterator();
			// System.out.println("In " + currentSrc);
			
			// 匹配结果
			while (it.hasNext()) {
				Matchable matcher = it.next();
				List<String> res = matcher.match(content);
				
				/*
				if (res.size() > 0) {
					int index = 0;
					Iterator its = res.iterator();
					System.out.println("<" + matcher.getTitle() + "> matched: ");
					while (its.hasNext()) {
						index++;
						System.out.println("[" + index + "] " + its.next());
					}
				}
				else {
					System.out.println("<" + matcher.getTitle() + "> mismatched. ");
				}
				*/
			}
		
			this.serial(this.patterns);
		}

		if (this.out.close())
			System.out.println("\nClose output destination.");
	}

	// Currently, implement MAX_LOOP only.
	public void serial(List<Matchable> patterns) {

		boolean[] flags = new boolean[patterns.size()];
		for (int i=0; i<flags.length; i++)
			flags[i] = false;
		boolean terminate = false;
		while (!terminate) {
			Map<String, String> map = new HashMap<String, String>();	
			int index = -1;
			String msg = "Generate object:\n";
			Iterator<Matchable> it = patterns.iterator();
			
			while (it.hasNext()) {
				index++;
				Matchable pattern = it.next();
				String field = pattern.getTitle();

				if (pattern.getSize() == 0) {
					flags = new boolean[0];
					break;
				}
				else {
					if (!pattern.hasNext()) {
						pattern.reset();
						flags[index] = true;
					}
					map.put(field, pattern.next());
				}
				msg += "\t" + field + ":" + map.get(field) + "\n";
			}
			
			terminate = true;
			for (int j=0; j<flags.length; j++)
				terminate &= flags[j];
			if (flags.length > 0) {
				System.out.println(msg);
				this.out.write(map);
			}
		}
	}
}

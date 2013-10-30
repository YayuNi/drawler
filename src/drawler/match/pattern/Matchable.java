package drawler.match.pattern;

import java.util.*;

public abstract class Matchable {
	
	private String title;
	private String description;
	
	protected Filter filter = null;
	
	public List<String> buffer;
	private boolean isUpdated;

	private int current;
	
	public Matchable() {
		this.clear();
		this.setinfo("", "");
	}
	
	public Matchable(String title, String description) {
		this.clear();
		this.setinfo(title, description);
	}
	
	private void clear() {
		if (buffer == null)
			buffer = new Vector();
		buffer.clear();
		isUpdated = false;
		filter = null;
		current = 0;
	}
	
	public void setinfo(String title, String description){
		this.title = title;
		this.description = description;
	}
	
	public void setFilter(Filter filter) {
		this.filter = filter;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getDescription() {
		return description;
	}

	public int getSize() {
		return buffer.size();
	}
	
	public List<String> match(String srcContent) {
		buffer = run(srcContent);
		isUpdated = true;
		return buffer;
	}
	
	// Get next matched string
	public String next() {
		if (filter != null)
			return filter.filter(pop());
		else	
			return pop();
	}

	// Get all matched string
	public List<String> group() {
		if (filter!=null) {
			List<String> result = new Vector<String>();
			Iterator it = buffer.iterator();
			while (it.hasNext()) {
				result.add(filter.filter(it.next().toString()));
			}
			return result;
		}
		else
			return buffer;
	}

	public String pop() {
		String result = "";
		if (this.hasNext())
			result =  buffer.get(this.current);
		this.current++;

		return result;
	}

	public boolean hasNext() {
		return this.current < this.getSize();
	}

	public void reset() {
		this.current = 0;
	}

	public abstract List<String> run(String srcContent);
}

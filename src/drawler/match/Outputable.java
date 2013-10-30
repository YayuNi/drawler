package drawler.match;

import java.util.Map;
import java.util.List;

import drawler.match.pattern.Matchable;

public interface Outputable {
	
	public boolean open(List<Matchable> patterns);

	public void write(Map<String, String> object);

	public boolean close();
}

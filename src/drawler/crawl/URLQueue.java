package drawler.crawl;

public interface URLQueue { 
	
	// Pop an URL	
	public ContextualURL pop();
 
	// Push an URL
	public int push(ContextualURL url);

	// Get queue size
	public int size();

}

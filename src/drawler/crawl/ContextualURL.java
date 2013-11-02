package drawler.crawl;

import java.util.*;
import java.net.*;

/**
 *  Represents an URL with additional context information.
 */
public class ContextualURL {

    /**
     *  the URL
     *
     *@since    28 novembre 2001
     */
    public URL url;

    /**
     *  the number of domain changes since the "homepage"
     *
     *@since    28 novembre 2001
     */
    public int scanDepth;

    /**
     *  URL changed according to user settings. (mainly for dynamic files)
     */
    public URL transformedURL;

    /**
     *  url should be scanned for new links.
     */
    public boolean scannable;

    /**
     *  url should be downloaded to disk.
     */
    public boolean downloadable;

    /**
     *  url has to be transformed.
     */
    public boolean renamable;

    /**
     *  The string found on the referring resource
     */
    public String originalLink;

    /**
     *  Description of the Field
     */
    public String token;


    /**
     *  Constructor for the ContextualURL object
     *
     *@param  url          url
     *@param  domainDepth
     *@since               28 novembre 2001
     */
    public ContextualURL(URL url, int scanDepth) {
        this.url = url;
        this.scanDepth = scanDepth;
    }

    public ContextualURL(String url, int scanDepth) throws Exception {
	this.url = new URL(url);
	this.scanDepth = scanDepth;
    }

    @Override
    public boolean equals(Object e) {
	return ((ContextualURL)e).url.toString().equals(this.url.toString());
    }
}


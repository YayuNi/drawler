package drawler.crawl;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.*;
import java.io.*;
import java.util.zip.*;

/**
 *  Collection of static methods used by Drawler.
 */
public class DrawlerUtilities {

    private static final int STREAM_BUFFER_SIZE = 4096;

    public static HttpURLConnection getValidConnection(URL url) {
        HttpURLConnection httpurlconnection = null;

        try {
            URLConnection urlconnection = url.openConnection();
            urlconnection.addRequestProperty("User-Agent", "Mozilla/4.0(compatible;MSIE 5.0;Windows NT;DigExt)"); 
            urlconnection.connect();

            if (!(urlconnection instanceof HttpURLConnection)) {
                System.out.println("Not an http connection: " + url);
                // urlconnection.disconnect();
                return null;
            }

            httpurlconnection = (HttpURLConnection) urlconnection;
            // httpurlconnection.setFollowRedirects(true);

            int responsecode = httpurlconnection.getResponseCode();

            switch (responsecode) {
                // here valid codes!
                case HttpURLConnection.HTTP_OK:
                case HttpURLConnection.HTTP_MOVED_PERM:
                case HttpURLConnection.HTTP_MOVED_TEMP:
		    // System.out.println("Code: " + responsecode);
                    break;
                default:
                    System.out.println("Invalid response code: " + responsecode + " " + url);
                    httpurlconnection.disconnect();
                    return null;
            }
        } catch (IOException ioexception) {
            System.out.println("unable to connect: " + ioexception);
            if (httpurlconnection != null) {
                httpurlconnection.disconnect();
            }
            return null;
        }

        return httpurlconnection;
    }


    /**
     *  Returns an InputStream from a connection. Retries 3 times.
     */
    public static InputStream getSafeInputStream(HttpURLConnection connection) {

        InputStream inputstream = null;

        for (int i = 0; i < 3; ) {
            try {
                inputstream = connection.getInputStream();
		String encoding = connection.getContentEncoding();
		if ("gzip".equals(encoding)) {
			inputstream = new GZIPInputStream(inputstream);
			System.out.println("This is a gzip page.");
		}
                break;
            } catch (IOException ioexception1) {
                System.out.println("error opening connection " + ioexception1);
                i++;
            }
        }

        return inputstream;
    }


    /**
     *  Gets the content attribute of the Drawler object
     */
    public static String downloadStringResource(HttpURLConnection connection) {

        StringBuffer content;
        InputStream inputstream = getSafeInputStream(connection);
        if (inputstream == null) {
            return null;
        }

        // load the Stream in a StringBuffer
        // Check encoding
        String contentType = connection.getContentType();
        String charset = "";
        for (String value : contentType.split(";")) {
            value = value.trim();
	    if (value.toLowerCase().startsWith("charset=")) 
	        charset = value.substring("charset=".length());
        }
        if ("".equals(charset))
	    charset = "UTF-8";

	String result = "";
	try {
            InputStreamReader isr = new InputStreamReader(inputstream, charset);
            content = new StringBuffer();

	    // Write bytes into buffer
            char buf[] = new char[STREAM_BUFFER_SIZE];
            int cnt = 0;
            while ((cnt = isr.read(buf, 0, STREAM_BUFFER_SIZE)) != -1) {
                content.append(buf, 0, cnt);
            }
            isr.close();
            inputstream.close();

            result = content.toString();
        } catch (Exception ioexception) {

            System.out.println(ioexception);
        }

        return result;
    }

    public static File URLToLocalFile(File basepath, URL url) {
        String[] tmp  = url.getPath().split("/");
        String localpathname = tmp[tmp.length-1];
        localpathname.replace('.', '-');

        File localfile = new File(basepath, localpathname);
        return localfile;
    }

    /**
     *  Streams to disk
     */
    public static long writeToDisk(InputStream inputstream, File outputfile) {
        long writtenBytesCount = 0;

        try {

            if (inputstream == null) {
                System.out.print("inputstream is null: " + outputfile);
                return writtenBytesCount;
            }

            // create local directory
            outputfile.getParentFile().mkdirs();

            FileOutputStream fileoutputstream = new FileOutputStream(outputfile);

            byte abyte0[] = new byte[STREAM_BUFFER_SIZE];
            int j;
            while ((j = inputstream.read(abyte0)) >= 0) {
                fileoutputstream.write(abyte0, 0, j);
                writtenBytesCount += j;
            }

            fileoutputstream.close();
            inputstream.close();
        } catch (FileNotFoundException fnfe) {
            System.out.println(fnfe);
        } catch (IOException ioexception) {
            System.out.println(ioexception);
        }

        return writtenBytesCount;
    }

    public static boolean isScannable(ContextualURL contextualURL, List<String> regex) {
        Iterator<String> it = regex.iterator();
	while (it.hasNext()) { 
    		Pattern urlregex = Pattern.compile(it.next());
		Matcher m = urlregex.matcher(contextualURL.url.toString());
		
		if (m.matches())
			return true;
	}
	return false;
    }
    
    public static boolean isDownloadable(ContextualURL contextualURL, List<String> regex) {
        Iterator<String> it = regex.iterator();
	while (it.hasNext()) { 
    		Pattern urlregex = Pattern.compile(it.next());
		Matcher m = urlregex.matcher(contextualURL.url.toString());
		
		if (m.matches())
			return true;
	}
	return false;
    }

    public static String transformLink(String link) {
        String transformed_link = null;

        transformed_link = link.replace('?','!');

        String transformed_link_lower = transformed_link.toLowerCase();
        if (!(transformed_link_lower.endsWith(".html") || transformed_link_lower.endsWith(".htm"))) {
            transformed_link = transformed_link + ".html";
        }

        // System.out.println("transformed_link: " + transformed_link);
        return transformed_link;
    }
}


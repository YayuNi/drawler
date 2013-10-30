package drawler.match.io;

import java.util.*;
import java.io.*;

import drawler.match.Inputable;

public class FileInput implements Inputable {

	private List<File> files;
	private Iterator<File> it;
	
	private File currentFile;
	
	public FileInput() {
		clear();
	}
	
	public FileInput(String inDir) {
		clear();
		addSource(new File(inDir));
	}
	
	// 加入文件源
	public int addPath(String f) {
		return addSource(new File(f));
	}
	
	public int addSource(File f) {
		if (f.isDirectory()) {
			File[] fileList = f.listFiles();
			for(File file : fileList) {
				addSource(file);
			}
		}
		else {
			files.add(f);
		}
		
		return files.size();
	}
	
	public void clear() {
		if (files == null) {
			files = new Vector<File>();
		}
		files.clear();
		it = null;
	}
	
	@Override
	public String current() {
		return currentFile.getName();
	}
	
	@Override
	public boolean hasNext() {
		if (it == null)
			it = files.iterator();
		return it.hasNext();
	}

	// 取出一个文件，读出其数据
	@Override
	public String next() {
		try{
			String content = null;
			if (it == null)
				it = files.iterator();
			if (it.hasNext()) {
				currentFile = it.next();
				StringBuffer buffer = new StringBuffer();
				BufferedReader reader = new BufferedReader(new FileReader(currentFile));
				
				String line;
				while ((line = reader.readLine()) != null) {
					buffer.append(line);
				}
				reader.close();
				content = buffer.toString();
			}

			return content;
		}
		catch(Exception e){
			System.err.println(e.toString());
			return "";
		}
	}	
}

package me.leopold.hubert.javawebserv;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Page {

	private byte[] content;
	private String pagename;
	
	public Page() {
		this("default_page.html", "<!DOCTYPE html><html><head><title>Default page</title></head><body>empty</body></html>");
	}
	
	public Page(String pagename, String initialContent) {
		this.pagename = pagename;
		this.content = initialContent.getBytes();
	}
	
	public void setPageName(String name) {
		this.pagename = name;
	}
	
	public String getPageName() {
		return this.pagename;
	}
	
	public void removeContent() {
		this.content = new byte[] {};
	}
	
	public void setContent(String content) {
		this.content = content.getBytes();
	}
	
	public void setRawContent(byte[] content) {
		this.content = content;
	}
	
	public void setFromFile(File file) {
		try {
			this.removeContent();
			this.content = Files.readAllBytes(file.toPath());
			setPageName(file.getPath());
		} catch (IOException e) {
			if(Server.verbose)System.err.println("unable to find requested file...");
		}
	}
	
	public byte[] getBytes() {
		return this.content;
	}
	
	public static Page get404Error(File root) {
		Page e404 = new Page();
		
		File file404 = null;
		if(root != null) {
			for(File file:root.listFiles()) {
				if(file.getName().equals("404.html"))file404 = file;
			}
		}
		
		if(file404 != null) {
			e404.setFromFile(file404);
		}else {
			e404.setPageName("404.html");
			e404.setContent("<!DOCTYPE html><html><head><title>404 Page not found...</title></head><body><h1>ERROR 404 Page not found...</h1></body></html>");
		}
		
		return e404;
	}
	
	public static Page get501Error(File root) {
		Page e404 = new Page();
		
		File file404 = null;
		if(root != null) {
			for(File file:root.listFiles()) {
				if(file.getName().equals("501.html"))file404 = file;
			}
		}
		
		if(file404 != null) {
			e404.setFromFile(file404);
		}else {
			e404.setPageName("501.html");
			e404.setContent("<!DOCTYPE html><html><head><title>501 Not implemented...</title></head><body><h1>ERROR 501 Not implemented...</h1></body></html>");
		}
		
		return e404;
	}
}

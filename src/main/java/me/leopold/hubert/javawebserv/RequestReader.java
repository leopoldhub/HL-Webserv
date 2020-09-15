package me.leopold.hubert.javawebserv;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public class RequestReader {

	private File root;

	private ManagerEvent manager;

	public RequestReader(File root) {
		this.root = root;
		if (!this.root.exists() || !this.root.isDirectory()) {
			if (!this.root.mkdirs()) {
				System.err.println("error creating file \"" + this.root.getAbsolutePath());
			}
		}
	}

	public void setManager(ManagerEvent manager) {
		this.manager = manager;
	}

	public void removeManager() {
		this.manager = null;
	}

	public void manage(Socket socket) {
		BufferedReader in = null;
		PrintWriter printer = null;
		BufferedOutputStream out = null;

		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			printer = new PrintWriter(socket.getOutputStream());
			out = new BufferedOutputStream(socket.getOutputStream());

			String method = "GET";
			String requestedPage = "/";
			String[] getargs = new String[] {};
			String[] postargs = new String[] {};
			String host = "";
			String agent = "";
			String cookie = "";

			List<String> clientHeaders = new ArrayList<>();

			boolean reading = true;
			String line = null;
			while (reading && (line = in.readLine()) != null) {
				clientHeaders.add(line);
				if (clientHeaders.size() == 1) {
					StringTokenizer tokenizer = new StringTokenizer(line);
					method = tokenizer.nextToken().toUpperCase();
					StringTokenizer rawpage = new StringTokenizer(tokenizer.nextToken(), "?");
					requestedPage = rawpage.nextToken();
					getargs = rawpage.hasMoreTokens() ? rawpage.nextToken().split("&") : new String[] {};
				}
				if (line.toLowerCase().startsWith("host: ")) {
					host = line.substring("host: ".length());
				} else if (line.toLowerCase().startsWith("user-agent: ")) {
					agent = line.substring("user-agent: ".length());
				} else if (line.toLowerCase().startsWith("cookie: ")) {
					cookie = line.substring("cookie: ".length());
				}
				if (line.length() == 0)
					reading = false;
				if (Server.verbose)
					System.out.println(line);
			}

			if (requestedPage.endsWith("/"))
				requestedPage += "index.html";

			if (method.equals("POST")) {
				StringBuilder postValue = new StringBuilder();
				while (in.ready()) {
					postValue.append((char) in.read());
				}
				if (Server.verbose)
					System.out.println("post values: " + postValue.toString());
				postargs = postValue.toString().split("&");
			}

			ClientInfos infos = new ClientInfos(method, requestedPage, getargs, postargs, host, agent, cookie,
					clientHeaders);

			if (Server.verbose)
				System.out.println(infos);

			Page pg = getRequestedPage(requestedPage);

			if (!method.equals("HEAD") && !method.equals("GET") && !method.equals("POST")) {
				pg = Page.get501Error(root);
			}

			List<String> ph = new ArrayList<>();

			if (manager != null) {
				pg = manager.getPage(pg, infos, socket);
			}

			ph.add("HTTP/1.1 200 OK");
			ph.add("Server: HL-WebServ : 1.0");
			ph.add("Date: " + new Date());
			String type;
			if (pg.getPageName().endsWith("/") || pg.getPageName().endsWith(".html")) {
				type = "text/html";
			} else if (pg.getPageName().endsWith(".txt")) {
				type = "text/plain";
			} else if (pg.getPageName().endsWith(".aac")) {
				type = "audio/aac";
			} else if (pg.getPageName().endsWith(".abw")) {
				type = "application/x-abiword";
			} else if (pg.getPageName().endsWith(".arc")) {
				type = "application/octet-stream";
			} else if (pg.getPageName().endsWith(".avi")) {
				type = "video/x-msvideo";
			} else if (pg.getPageName().endsWith(".azw")) {
				type = "application/vnd.amazon.ebook";
			} else if (pg.getPageName().endsWith(".bin")) {
				type = "application/octet-stream";
			} else if (pg.getPageName().endsWith(".bmp")) {
				type = "image/bmp";
			} else if (pg.getPageName().endsWith(".bzarch")) {
				type = "application/x-bzip";
			} else if (pg.getPageName().endsWith(".bz2")) {
				type = "application/x-bzip2";
			} else if (pg.getPageName().endsWith(".csh")) {
				type = "application/x-csh";
			} else if (pg.getPageName().endsWith(".css")) {
				type = "text/css";
			} else if (pg.getPageName().endsWith(".csv")) {
				type = "text/csv";
			} else if (pg.getPageName().endsWith(".doc")) {
				type = "application/msword";
			} else if (pg.getPageName().endsWith(".docx")) {
				type = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
			} else if (pg.getPageName().endsWith(".eot")) {
				type = "application/vnd.ms-fontobject";
			} else if (pg.getPageName().endsWith(".epub")) {
				type = "application/epub+zip";
			} else if (pg.getPageName().endsWith(".gif")) {
				type = "image/gif";
			} else if (pg.getPageName().endsWith(".ico")) {
				type = "image/x-icon";
			} else if (pg.getPageName().endsWith(".ics")) {
				type = "text/calendar";
			} else if (pg.getPageName().endsWith(".jar")) {
				type = "application/java-archive";
			} else if (pg.getPageName().endsWith(".jpg")) {
				type = "image/jpeg";
			} else if (pg.getPageName().endsWith(".jsJava")) {
				type = "application/javascript";
			} else if (pg.getPageName().endsWith(".json")) {
				type = "application/json";
			} else if (pg.getPageName().endsWith(".midi")) {
				type = "audio/midi";
			} else if (pg.getPageName().endsWith(".mpeg")) {
				type = "video/mpeg";
			} else if (pg.getPageName().endsWith(".mpkg")) {
				type = "application/vnd.apple.installer+xml";
			} else if (pg.getPageName().endsWith(".odp")) {
				type = "application/vnd.oasis.opendocument.presentation";
			} else if (pg.getPageName().endsWith(".ods")) {
				type = "application/vnd.oasis.opendocument.spreadsheet";
			} else if (pg.getPageName().endsWith(".odt")) {
				type = "application/vnd.oasis.opendocument.text";
			} else if (pg.getPageName().endsWith(".oga")) {
				type = "audio/ogg";
			} else if (pg.getPageName().endsWith(".ogv")) {
				type = "video/ogg";
			} else if (pg.getPageName().endsWith(".ogx")) {
				type = "application/ogg";
			} else if (pg.getPageName().endsWith(".otf")) {
				type = "font/otf";
			} else if (pg.getPageName().endsWith(".png")) {
				type = "image/png";
			} else if (pg.getPageName().endsWith(".pdf")) {
				type = "application/pdf";
			} else if (pg.getPageName().endsWith(".ppt")) {
				type = "application/vnd.ms-powerpoint";
			} else if (pg.getPageName().endsWith(".pptx")) {
				type = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
			} else if (pg.getPageName().endsWith(".rar")) {
				type = "application/x-rar-compressed";
			} else if (pg.getPageName().endsWith(".rtf")) {
				type = "application/rtf";
			} else if (pg.getPageName().endsWith(".sh")) {
				type = "application/x-sh";
			} else if (pg.getPageName().endsWith(".svg")) {
				type = "image/svg+xml";
			} else if (pg.getPageName().endsWith(".swf")) {
				type = "application/x-shockwave-flash";
			} else if (pg.getPageName().endsWith(".tar")) {
				type = "application/x-tar";
			} else if (pg.getPageName().endsWith(".tiff")) {
				type = "image/tiff";
			} else if (pg.getPageName().endsWith(".ts")) {
				type = "application/typescript";
			} else if (pg.getPageName().endsWith(".ttf")) {
				type = "font/ttf";
			} else if (pg.getPageName().endsWith(".vsd")) {
				type = "application/vnd.visio";
			} else if (pg.getPageName().endsWith(".wav")) {
				type = "audio/x-wav";
			} else if (pg.getPageName().endsWith(".weba")) {
				type = "audio/webm";
			} else if (pg.getPageName().endsWith(".webm")) {
				type = "video/webm";
			} else if (pg.getPageName().endsWith(".webp")) {
				type = "image/webp";
			} else if (pg.getPageName().endsWith(".woff")) {
				type = "font/woff";
			} else if (pg.getPageName().endsWith(".woff2")) {
				type = "font/woff2";
			} else if (pg.getPageName().endsWith(".xhtml")) {
				type = "application/xhtml+xml";
			} else if (pg.getPageName().endsWith(".xls")) {
				type = "application/vnd.ms-excel";
			} else if (pg.getPageName().endsWith(".xlsx")) {
				type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
			} else if (pg.getPageName().endsWith(".xml")) {
				type = "application/xml";
			} else if (pg.getPageName().endsWith(".xul")) {
				type = "application/vnd.mozilla.xul+xml";
			} else if (pg.getPageName().endsWith(".zip")) {
				type = "application/zip";
			} else if (pg.getPageName().endsWith(".3gp")) {
				type = "video/3gpp";
			} else if (pg.getPageName().endsWith(".3g2")) {
				type = "video/3gpp2";
			} else if (pg.getPageName().endsWith(".7z")) {
				type = "application/x-7z-compressed";
			} else {
				type = "application/octet-stream";
			}
			ph.add("Content-type: " + type);
			ph.add("Content-length: " + pg.getBytes().length);

			if (manager != null) {
				ph = manager.getHeaders(ph, infos, socket);
			}

			for (String hl : ph) {
				printer.println(hl);
			}
			printer.println();
			printer.flush();

			out.write(pg.getBytes());
			out.flush();

		} catch (IOException e) {
			if (Server.verbose)
				System.err.println("Server I/O Error:\n" + e);
		} finally {
			try {
				in.close();
				printer.close();
				out.close();
				socket.close();
			} catch (Exception e) {
				System.err.println("Error closing...\n" + e.getMessage());
			}
		}
	}

	public Page getRequestedPage(String requested) {
		Page page = new Page();
		File reqFile = new File(root, requested);
		page.setFromFile(reqFile);
		if (page.getBytes().length == 0 || !reqFile.getAbsolutePath().startsWith(root.getAbsolutePath())) {
			System.out.println("page not found!");
			page = Page.get404Error(root);
		}
		return page;
	}

}

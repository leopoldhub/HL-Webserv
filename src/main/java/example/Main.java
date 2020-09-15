package example;

import java.net.Socket;
import java.util.List;

import me.leopold.hubert.javawebserv.ClientInfos;
import me.leopold.hubert.javawebserv.ManagerEvent;
import me.leopold.hubert.javawebserv.Page;
import me.leopold.hubert.javawebserv.Server;

public class Main {

	public static Server server;

	public static void main(String[] args) {
		Server.verbose = true;
		server = new Server();
		server.getRequestReader().setManager(new ManagerEvent() {
			
			@Override
			public Page getPage(Page proposedPage, ClientInfos clientInfos, Socket connection) {
				if(clientInfos.getPageName().endsWith("/headers.html")) {
					System.out.println("user request headers");
					Page pg = new Page();
					pg.setPageName("myheaders.html");
					StringBuilder sb = new StringBuilder("<!DOCTYPE html><html><head><title>My Headers</title></head><body><h1>My Headers:</h1>");
					for(String line:clientInfos.getFullHeaders())sb.append("<p>"+line+"</p>");
					sb.append("</body></html>");
					pg.setContent(sb.toString());
					return pg;
				}
				return proposedPage;
			}
			
			@Override
			public List<String> getHeaders(List<String> proposedheaders, ClientInfos clientInfos, Socket connection) {
				return proposedheaders;
			}
		});
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {server.stop();}));
	}

}

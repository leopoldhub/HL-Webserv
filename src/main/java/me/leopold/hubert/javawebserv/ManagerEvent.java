package me.leopold.hubert.javawebserv;

import java.net.Socket;
import java.util.List;

public interface ManagerEvent {

	public Page getPage(Page proposedPage, ClientInfos clientInfos, Socket connection);
	
	public List<String> getHeaders(List<String> proposedheaders, ClientInfos clientInfos, Socket connection);
	
}

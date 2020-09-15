package me.leopold.hubert.javawebserv;

import java.util.Arrays;
import java.util.List;

import me.leopold.hubert.javawebserv.utils.ArrayCopier;

public class ClientInfos {

	private String method;
	private String page;
	private String[] getargs;
	private String[] postargs;
	private String host;
	private String agent;
	private String cookie;
	private List<String> fullHeaders;
	
	public ClientInfos(String method, String page, String[] getargs, String[] postargs, String host, String agent, String cookie, List<String> fullHeaders) {
		this.method = method;
		this.page = page;
		this.getargs = ArrayCopier.copy(getargs);
		this.postargs = ArrayCopier.copy(postargs);
		this.host = host;
		this.agent = agent;
		this.cookie = cookie;
		this.fullHeaders = fullHeaders;
	}

	public String getMethod() {
		return method;
	}

	public String getPageName() {
		return page;
	}

	public String[] getGETArgs() {
		return ArrayCopier.copy(getargs);
	}
	
	public String[] getPOSTArgs() {
		return ArrayCopier.copy(postargs);
	}
	
	public List<String> getFullHeaders() {
		return fullHeaders;
	}

	public String getHost() {
		return host;
	}

	public String getAgent() {
		return agent;
	}

	public String getCookie() {
		return cookie;
	}

	@Override
	public String toString() {
		return "ClientInfos [method=" + method + ", page=" + page + ", getargs=" + Arrays.toString(getargs)
				+ ", postargs=" + Arrays.toString(postargs) + ", host=" + host + ", agent=" + agent + ", cookie="
				+ cookie + ", fullHeaders=" + Arrays.toString(fullHeaders.toArray()) + "]";
	}
	
}

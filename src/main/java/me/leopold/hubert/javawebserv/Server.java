package me.leopold.hubert.javawebserv;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Server {

	public static void main(String... args) {
		new Server(args.length>0?new File(args[0]):new File("./www"), args.length>1?Integer.parseInt(args[1]):80);
	}
	
	private RequestReader requestReader;
	
	private Thread connectionListener = null;
	
	public static boolean verbose = false;
	
	private ServerSocket serverSocket;
	
	private File root;
	private int port;
	
	public Server() {
		this(new File("./www"));
	}
	
	public Server(File file) {
		this(file, 80);
	}
	
	public Server(File file, int port) {
		this.root = file;
		this.port = port;
		requestReader = new RequestReader(this.root);
		start();
	}
	
	public void start() {
		if(verbose)System.out.println("debug mode enabled!");
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("server started and listening on port: "+port);
		} catch (IOException e) {
			System.err.println("unable to start server!\n"+e.getMessage());
		}
		connectionListener = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while(!serverSocket.isClosed()) {
						if(verbose)System.out.println("awaiting connection...");
						Socket socket = serverSocket.accept();
						if(verbose)System.out.println("["+LocalDateTime.now()+"] Client attempt connection "+socket.getInetAddress()+" on port "+socket.getPort());
						if(!serverSocket.isClosed()) {
							new Thread(() -> {requestReader.manage(socket);}).run();
							if(verbose)System.out.println("["+LocalDateTime.now()+"] Client connection Opened! "+socket.getInetAddress()+" on port "+socket.getPort());
						}
					}
				} catch (IOException e) {
					System.err.println("error receiving client connection...\n"+e.getMessage());
				}
			}
		});
		connectionListener.start();
	}
	
	public void stop() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		connectionListener.interrupt();
		connectionListener.stop();
		connectionListener = null;
	}

	public RequestReader getRequestReader() {
		return requestReader;
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public File getRoot() {
		return new File(root.getPath());
	}

	public int getPort() {
		return port;
	}

}

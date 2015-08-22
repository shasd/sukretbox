package sukretbox;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ConnectionListener implements Runnable{
	private int port;
	private DataStore data;
	
	public ConnectionListener(DataStore data, int port) {
		this.data = data;
		this.port = port;
	}
	public ConnectionListener(DataStore data) {
		this.data = data;
		this.port = 12831;
	}
    public void run() {
    	try {
    		System.out.println("server started1");
    	    ServerSocket socket = new ServerSocket(port);
    	    socket.setSoTimeout(100);
    	    while(!Thread.interrupted()) {
    	    	
    	        Socket connection;
    	    	try {
    	            connection = socket.accept();
    	    	} catch (SocketTimeoutException e) {
    	        	continue;
    	        }
    	        System.out.println("connection accepted");
    	        new Thread(new ConnectionHandler(connection, data)).start();
    	    }
    	    System.out.println("Listener interrupted.");
    	    socket.close();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }

	//public static void main(String[] args){
	//	new Thread(new ConnectionListener(new S3Store())).start();
	//}
    
}

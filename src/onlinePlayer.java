import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class onlinePlayer extends Player{

	private static int port = 27010;
	private Inet4Address connectAddr;
	private ArrayList<Inet4Address> connectedAddr;
	private ServerSocket netHandler;
	private Socket netHandlerConn;
	public onlinePlayer(String name, int x, int y, boolean showBtn, int port) {
		super(name, x, y, showBtn);
		this.port = port;
		try {
			netHandler = new ServerSocket(port);
			netHandler.accept();
		} catch (IOException e) {
			System.out.println("Unable to open port!!!");
			e.printStackTrace();
		}
	}
	public onlinePlayer(String name, int x, int y, boolean showBtn) {
		this(name, x, y, showBtn, port);
	}
	
	public onlinePlayer(String name, int x, int y, boolean showBtn, Inet4Address connAddr) {
		super(name, x, y, showBtn);
		this.connectAddr = connAddr;
		try {
			netHandlerConn = new Socket(connAddr.toString(), port);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @param name
	 * @param x
	 * @param y
	 * @param showBtn
	 * @param connAddr
	 * @param port
	 * @param serverOrClient true if acting like server, false if acting like client
	 */
	public onlinePlayer(String name, int x, int y, boolean showBtn, Inet4Address connAddr, int port, boolean serverOrClient) {
		super(name, x, y, showBtn);
		if(serverOrClient){
			this.port = port;
			try {
				netHandler = new ServerSocket(port, Integer.MAX_VALUE, connAddr);
			} catch (IOException e) {
				System.out.println("Unable to open port!!!");
				e.printStackTrace();
			}
		}
		else{
			this.connectAddr = connAddr;
			try {
				netHandlerConn = new Socket(connAddr.toString(), port);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

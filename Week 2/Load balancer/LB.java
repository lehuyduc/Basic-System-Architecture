import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class LB extends Thread {

	static String hosts[] = {"localhost", "localhost"};
	static int ports[] = {8081,8082};
	static int nbPorts = 2;
	static Random rand = new Random();	

	Socket client;
	
	public LB(Socket s) {
		client = s;
	}
	
	public void run() {
		int servernum = rand.nextInt(nbPorts);
		
		try {
			Socket server = new Socket(hosts[servernum], ports[servernum]);
			
			OutputStream cos = client.getOutputStream();
			InputStream cis = client.getInputStream();
			
			OutputStream sos = server.getOutputStream();
			InputStream sis = server.getInputStream();
		
			byte data[] = new byte[1024];
			
			int whatiread = cis.read(data);	// read request from client
			sos.write(data, 0, whatiread);	// forward request to server
			whatiread = sis.read(data); // read response from server
			cos.write(data, 0, whatiread); // forward response to client
			
			server.close();
			client.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	public static void main(String[] args) {
		
		try {
			ServerSocket ss = new ServerSocket(8080);
			while (true) {
				Thread t = new LB(ss.accept());
				t.start();
			} 
		} catch (Exception e) {e.printStackTrace();}
	}
	
}

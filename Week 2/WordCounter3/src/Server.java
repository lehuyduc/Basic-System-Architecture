import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;

public class Server {
	
	public static final String hostip = "localhost";	
	public static final int port_offset = 100;
	public static final int buffer_size = 4096;
	
	private static boolean stop = false;
	
	private static int currentId = 0;
	private static final int maxCapacity = 100000;

	
	// This server can serve multiple clients at once.
	// When it receives a request from a process,
	// it creates a DaemonImpl and the DaemonImpl will work with the client.
	// Then, the Split/Launch process work with the Daemon like in the simple case.
	// And the server continue to listen to new client requests
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Server require 1 arg: port");
			return;
		}
		
		try {
			int port = Integer.parseInt(args[0]);
			try {
				LocateRegistry.createRegistry(port + port_offset);
			}
			catch (Exception e) {
				// registry is already created before, so do nothing
			} 
			ServerSocket serverSocket = new ServerSocket(port);			
			
			while (!Server.stop) {
				Socket clientSocket = serverSocket.accept(); 				
				DaemonImpl newDaemon = new DaemonImpl(clientSocket, Server.generateId());			
				new Thread(newDaemon).start();				
			}

			serverSocket.close();
		}
		catch (Exception e) {
			System.out.print("error caught in server");
			e.printStackTrace();
		}
	}
	
	// basic generate registry name for daemon.
	// This is the wrong way, but it's good enough for this project demo
	public static String generateId() {		
		if (currentId==maxCapacity) currentId = 0;
		currentId++;
		String res = Integer.toString(currentId);
		while (res.length() < 5) res = "0" + res;		
		return res;
	}
}

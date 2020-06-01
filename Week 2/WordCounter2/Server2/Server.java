import java.io.DataOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
	public static final String hostip = "localhost";	
	public static final int port_offset = 100;

	
	// This server can serve multiple clients at once.
	// When it receives a request from a Split process,
	// it create a Daemon and send the Daemon's port to the Split process.
	// Then, the Split process work with the Daemon like in the simple case.
	// And the server continue to listen to new requests
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Server require 1 arg: port");
			return;
		}
		
		try {
			int port = Integer.parseInt(args[0]);
			
			ServerSocket serverSocket = new ServerSocket(port);
			while (true) {
				Socket clientSocket = serverSocket.accept(); 
				ServerSocket daemonSocket;
				DaemonImpl newDaemon;
				
				while (true) {
					try {
						daemonSocket = new ServerSocket(0);
						newDaemon = new DaemonImpl(daemonSocket);
						break;
					}
					catch (Exception e) {
						// this means the port is already used by someone else
						// keep creating new Socket until we get a free socket 
					}
				}
				new Thread(newDaemon).start();
				
				// send the port of newDaemon to client
				DataOutputStream cdos = new DataOutputStream(clientSocket.getOutputStream());
				cdos.writeInt(daemonSocket.getLocalPort());
				
				// after that client will work with newDaemon. Server continue with new client
				cdos.close();				
				clientSocket.close();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}

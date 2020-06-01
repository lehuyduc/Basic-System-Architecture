
import java.util.Vector;
import java.io.File;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Split {
	public static final String hostip = "localhost";
	
	// args[0] = filename of file you want to split (in the same folder)
	// args[1..nbNodes] = ports of the servers
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Split need at least 2 args: filename and ports of servers");
			return;
		}
		
		try {
			System.out.println("Parsing input args");
			String filename = args[0];
			int nbNodes = args.length - 1; 
			Vector<Integer> serverPorts = new Vector<>();			
			for (int i=1; i<=nbNodes; i++) 
				serverPorts.add(Integer.parseInt(args[i]));	
			
			Vector<String> serverIps = new Vector<>(); 
			for (int i=0; i<nbNodes; i++) serverIps.add(hostip); // can be upgraded easily
			
			if (!Files.exists(Paths.get(filename))) {
				System.out.println(filename + ": File doesn't exist");
				return;
			}
			
			
			// -----------------------------
			// First connect with server, send "put" and filename  
			
			Vector<Socket> daemonSockets = new Vector<>();
			for (int i=0; i<nbNodes; i++) {
				String serverIp = serverIps.elementAt(i);
				int serverPort = serverPorts.elementAt(i);
				System.out.println("Connecting to " + serverIp + ":" + serverPort);				
				
				Socket socket = new Socket(serverIp, serverPort);
				
				OutputStream daemonOs = socket.getOutputStream();
				String message = "put " + filename + "\n";
				daemonOs.write(message.getBytes());
				daemonSockets.add(socket);
			}
			System.out.println("Connected to all servers");
			
			
			// ----------------------------
			// After we connected to all server, start sending file
			File file = new File(filename);
			Scanner scanner = new Scanner(file);	// slow, but good for short demo
			long fileSize = file.length();
			long newSize = fileSize / daemonSockets.size() + 1; 
			
			for (int i=0; i<daemonSockets.size(); i++) {
				String serverIp = serverIps.elementAt(i);
				int serverPort = serverPorts.elementAt(i);
				System.out.println("Uploading to servers at " + serverIp + ":" + serverPort);				

				Socket daemonSocket = daemonSockets.elementAt(i);
				
				OutputStream daemonOs = daemonSocket.getOutputStream();
				long bytesRead = 0;
				
				while (scanner.hasNext()) {
					String word = scanner.next();
					bytesRead += word.length() + 1; // +1 to include space or \n
					word = word + "\n";
					daemonOs.write(word.getBytes(), 0, word.length());
					if (bytesRead > newSize) break;
				}
				
				daemonOs.close();
				daemonSocket.close();
			}
			
			scanner.close();
		}
		catch (SocketTimeoutException e) {
			System.out.println("One or more server/daemon can't be connected");
		}
		catch (Exception e) {
			//e.printStackTrace();
			System.out.println("Step above failed");
		}
		
		System.out.println("Split.java success");
	}
}


import java.util.Vector;
import java.io.DataInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class Split {
	public static final String hostip = "localhost";
	
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Split need at least 2 args: filepath and ports of servers");
			return;
		}
		
		try {
			String filename = args[0];
			int nbNodes = args.length - 1; 
			Vector<Integer> serverPorts = new Vector<>();			
			for (int i=1; i<=nbNodes; i++) 
				serverPorts.add(Integer.parseInt(args[i]));			
			
			if (!Files.exists(Paths.get(filename))) {
				System.out.println(filename + ": File doesn't exist");
				return;
			}
			
			
			// -----------------------------
			// First part is connecting with servers, and get daemons' ports 
			
			Vector<Integer> daemonPorts = new Vector<>();
			for (int i=0; i<nbNodes; i++) {
				System.out.println("Trying to connect to server at " + serverPorts.elementAt(i));				
				Socket serverSocket = new Socket(hostip, serverPorts.elementAt(i));
				serverSocket.setSoTimeout(30000);				
				DataInputStream sdis = new DataInputStream(serverSocket.getInputStream());				
				daemonPorts.add(sdis.readInt());
				
				serverSocket.close();
				sdis.close();
			}
			System.out.println("Successfully received ports of the daemons");
			
			// ----------------------------
			// After we have the list of daemon, continue just like in the simple version
			File file = new File(filename);
			Scanner scanner = new Scanner(file);
			long fileSize = file.length();
			long newSize = fileSize / daemonPorts.size() + 1; 
			
			for (int i=0; i<daemonPorts.size(); i++) {
				Socket socket = new Socket(hostip, daemonPorts.elementAt(i));
				System.out.println("Connecting to daemon port " + daemonPorts.elementAt(i));
				OutputStream daemonOs = socket.getOutputStream();
				long bytesRead = 0;
				
				while (scanner.hasNext()) {
					String word = scanner.next();
					bytesRead += word.length() + 1; // +1 to include space or \n
					word = word + "\n";
					daemonOs.write(word.getBytes(), 0, word.length());
					if (bytesRead > newSize) break;
				}
				
				socket.close();
				daemonOs.close();
			}
			
			scanner.close();
			System.out.println("This client will work with the ports above");
		}
		catch (SocketTimeoutException e) {
			System.out.println("One or more server/daemon can't be connected");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}

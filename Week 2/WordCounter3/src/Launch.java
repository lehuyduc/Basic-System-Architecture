

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.Vector;

public class Launch {
	public static final String hostip = "localhost";
	public static final int port_offset = Server.port_offset;

	
	// args[0] = number of nodes
	// args[1..nbNodes] = ports of the daemons
	// args[nbNodes + 1] = filename of the result file (without ending .in, .out, ...)
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		
		try {
			System.out.println("Parsing input args");
			int nbNodes = Integer.parseInt(args[0]);
			String filename = args[nbNodes + 1]; 			 
			Vector<Integer> serverPorts = new Vector<>();
			for (int i=1; i<=nbNodes; i++) serverPorts.add(Integer.parseInt(args[i]));            
			
			Vector<String> serverIps = new Vector<>(); 
			for (int i=0; i<nbNodes; i++) serverIps.add(hostip); // can be upgraded easily
			
			Vector<Socket> daemonSockets = new Vector<>(); 
			Vector<String> daemonIds = new Vector<>();
			Daemon d[] = new Daemon[100];			
			WordCount wc = new WordCount();
			Callback cb = new CallbackImpl(nbNodes);

			
			// ---------------------
			// First connect with server, send "get" and filename  	
			long startConnect = System.currentTimeMillis(); 					
			
			for (int i=0; i<nbNodes; i++) {
				String serverIp = serverIps.elementAt(i);
				int serverPort = serverPorts.elementAt(i);
				System.out.println("Connecting to " + serverIp + ":" + serverPort);
				
				Socket socket = new Socket(serverIp, serverPort);
				
				OutputStream daemonOs = socket.getOutputStream();
				String message = "get " + filename + "\n";
				daemonOs.write(message.getBytes());				
				daemonSockets.add(socket);											
			}
			System.out.println("All servers connected");
			
			
			// ---------------------
			// Get names of daemons for Registry.lookup()
			
			for (int i=0; i<nbNodes; i++) {
				System.out.println("Getting daemon id from " + serverIps.elementAt(i) + ":" + serverPorts.elementAt(i));
				Socket socket = daemonSockets.elementAt(i);
				
				InputStream sis = socket.getInputStream();
				String daemonId = "";
				while (true) {
					char ch = (char)sis.read();
					if (ch=='\n') break;
					else daemonId = daemonId + ch;
				}			
				
				daemonIds.add(daemonId);
			}
			
			System.out.println("Received all daemon ids");

			// ---------------------
			// Get the daemons using registry_port = server_port + port_offset
						
			for (int i=0; i<daemonSockets.size(); i++) {
				String serverIp = serverIps.elementAt(i);
				int serverPort = serverPorts.elementAt(i);
				System.out.println("Getting daemon from " + serverIp + ":" + serverPort);
				
				Registry registry = LocateRegistry.getRegistry(serverIp, serverPort + port_offset);
				String daemonId = daemonIds.elementAt(i);
				d[i] = (Daemon)registry.lookup("//" + serverIp + "/daemon" + daemonId);
				// if any daemon can't be connected, the client will throw exception and exit
			}
			
			System.out.println("All daemons connected");

			long endConnect = System.currentTimeMillis();

			// ---------------------
			// All daemons are found, call them at the same time
			System.out.println("Sending RMI request");
			for (int i=0; i<nbNodes; i++) {
				new Thread(new LaunchSlave(d[i], wc, filename+".in", filename+".out",cb)).start();
			}
			cb.waitforall();
			System.out.println("RMI callback finished");
			
			
			// ---------------------
			// Download file from daemon servers
			
			long startDownload = System.currentTimeMillis();
			
			Vector<String> blockmaps = new Vector<>();
			for (int i=0; i<nbNodes; i++) {
				System.out.println("Downloading from " + serverIps.elementAt(i) + ":" + serverPorts.elementAt(i));
				
				int bytesRead;
				byte[] buffer = new byte[Server.buffer_size];
				String blockmap = filename + "-" + i + ".map";
				blockmaps.add(blockmap);
				
				Socket socket = daemonSockets.elementAt(i);
				InputStream sis = socket.getInputStream();
				FileOutputStream fos = new FileOutputStream(blockmap);
											
				while (true) {
					bytesRead = sis.read(buffer, 0, buffer.length);
					if (bytesRead <= 0) break;
					fos.write(buffer, 0, bytesRead);
				}
				
				sis.close();
				fos.close();
				socket.close();
			}
			
			long endDownload = System.currentTimeMillis();						
						
			// file integrity checking here, but since the project doesn't ask for this feature,
			// I make this very simple
			System.out.println("Checking file integrity");
			for (int i=0; i<nbNodes; i++) {
				boolean fileFound = true;
				Scanner scanner = new Scanner(new File(filename + "-" + i + ".map"));
				if (!scanner.hasNextLine()) {scanner.close(); continue;}
				
				String firstLine = scanner.nextLine();			
				if (firstLine.equals("FILE_NOT_FOUND - 404")) fileFound = false;								
				scanner.close();
				
				if (!fileFound) {
					String serverName = serverIps.elementAt(i) + ":" + serverPorts.elementAt(i);
					System.out.println("File " + filename + " not found on " + serverName);
					System.exit(0);
				}
			}
			
			// ---------------------
			
			System.out.println("Executing reduce");
			wc.executeReduce(blockmaps, filename + ".reduce");
			
			System.out.println("Launch.java success\n");
			long endTime = System.currentTimeMillis();
			
			long totalTime = endTime - startTime;
			long networkTime = (endConnect - startConnect) + (endDownload - startDownload);
			long usefulTime = totalTime - networkTime;
			
			System.out.println("Total time = " + totalTime + "ms");
			System.out.println("Network time = " + networkTime + "ms");
			System.out.println("Useful time = " + usefulTime + "ms");
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Step above failed");
		}	
		
		System.exit(0);
	}
}

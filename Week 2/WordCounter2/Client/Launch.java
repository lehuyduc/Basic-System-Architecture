

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Vector;

public class Launch {
	public static final String hostip = "localhost";
	public static final int port_offset = Server.port_offset;

	public static void main(String[] args) {
		try {
			int nbNodes = Integer.parseInt(args[0]);
			String outputName = args[nbNodes + 1]; 			 
			Vector<Integer> ports = new Vector<>();
			for (int i=1; i<=nbNodes; i++) ports.add(Integer.parseInt(args[i]));            
			
			Daemon d[] = new Daemon[100];			
			WordCount wc = new WordCount();
			Callback cb = new CallbackImpl(nbNodes);
			
			// ---------------------
			System.out.println("Trying to connect with daemons");
			for (int i=0; i<nbNodes; i++) {
				int port = ports.elementAt(i);
				Registry registry = LocateRegistry.getRegistry(port + port_offset);
				d[i] = (Daemon)registry.lookup("//localhost/daemon" + port);
				// if any daemon can't be connected, the client will throw exception and exit				
			}
			System.out.println("All daemons  connected");
			
			// if all daemons are found, then call them at the same time
			for (int i=0; i<nbNodes; i++) {
				int port = ports.elementAt(i);
				d[i].call(wc, "block" + port + ".in", "block" + port + ".out", cb);
			}
			cb.waitforall();
			System.out.println("Callback wait finished");
			
			// ---------------------
			// Download file from daemon servers
			Vector<String> blockres = new Vector<>();
			for (int i=0; i<nbNodes; i++) {
				Socket socket = new Socket(hostip, ports.elementAt(i));
				InputStream sis = socket.getInputStream();
				FileOutputStream fos = new FileOutputStream("block" + i + ".res");
				int bytesRead;
				byte[] buffer = new byte[1024];
				blockres.add("block" + i + ".res");
				
				while (true) {
					bytesRead = sis.read(buffer, 0, buffer.length);
					if (bytesRead <= 0) break;
					fos.write(buffer, 0, bytesRead);
				}
				sis.close();
				fos.close();
				socket.close();
			}
			
			System.out.println("Execute reduce");
			wc.executeReduce(blockres, outputName);			
		}
		catch (Exception e) {
			//e.printStackTrace();
			System.out.println("Step above failed");
		}
		
		System.out.println("Launch.java finished\n");
	}
}

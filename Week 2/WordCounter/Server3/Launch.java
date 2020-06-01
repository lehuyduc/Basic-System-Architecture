

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Vector;

public class Launch {
	public static final String hostip = "localhost";
	public static final int port_offset = 20;
	
	public static void main(String[] args) {
		try {
			int nbnodes = args.length - 1; // we reserve args[0] for future update
			Vector<Integer> ports = new Vector<>();
			for (int i=1; i<=nbnodes; i++) ports.add(Integer.parseInt(args[i]));
            
			
			Daemon d[] = new Daemon[100];			
			WordCount wc = new WordCount();
			Callback cb = new CallbackImpl(nbnodes);
			
			for (int i=0; i<nbnodes; i++) {
				// Naming.lookup require external rmiregistry console 
				//d[i] = (Daemon)Naming.lookup("//localhost/daemon" + i);
				Registry registry = LocateRegistry.getRegistry(ports.elementAt(i) + port_offset);
				d[i] = (Daemon)registry.lookup("//localhost/daemon" + i); // this cause crash 
				d[i].call(wc, "block" + i + ".in", "block" + i + ".out", cb);
			}
			cb.waitforall();
			System.out.println("Callback wait finished");
			
			// ---------------------
			// Download file from daemon servers
			Vector<String> blockres = new Vector<>();
			for (int i=0; i<nbnodes; i++) {
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
			wc.executeReduce(blockres, "finalresults.txt");			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Launch.java finished\n");
	}
}

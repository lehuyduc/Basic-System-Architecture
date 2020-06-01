

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class DaemonImpl extends UnicastRemoteObject implements Daemon {
	public static final String hostip = "localhost";
	public static final int port_offset = 20;
	int id;
	int port;

	public DaemonImpl() throws RemoteException {
		super();
	}
	
	public DaemonImpl(int id,int port) throws RemoteException {
		super();
		this.id = id;
		this.port = port;
	}

	@Override
	public void call(Map m, String blockin, String blockout, Callback cb) throws RemoteException {
		System.out.println("Daemon" + id + " is called");
		Slave s = new Slave(m, blockin, blockout, cb);
		s.start();
	}

	public static void main(String args[]) {
		if (args.length != 2) {
			System.out.println("Daemon needs 2 arg: daemonid (name), port number");
			return;
		}
				
		try {
			int id = Integer.parseInt(args[0]);
			int port = Integer.parseInt(args[1]);
			Registry registry = LocateRegistry.createRegistry(port+port_offset);
			
			try {
				Naming.unbind("//localhost/daemon" + id);
				System.out.println("Unbind successful");
			}
			catch (Exception e) {
				System.out.println("No unbind needed");
			}
			
			registry.rebind("//localhost/daemon" + id, new DaemonImpl(id, port));
			System.out.println("Binded with name: " + "//localhost/daemon" + id);
			
			ServerSocket daemonSocket = new ServerSocket(port);
			
			System.out.println("Waiting for connection");
			Socket clientSocket = daemonSocket.accept();
			System.out.println("Connected with Split.java");
			InputStream cis = clientSocket.getInputStream();
			FileOutputStream fos = new FileOutputStream("block" + id + ".in");
			byte[] buffer = new byte[1024];
			int bytesRead;
			
			while (true) {
				bytesRead = cis.read(buffer, 0, buffer.length);
				if (bytesRead <= 0) break;
				fos.write(buffer, 0, bytesRead);
			}
			
			cis.close();
			fos.close();
			clientSocket.close();
			
			// ----------------------------------------
			// MAP - COUNT THE WORD
			// We don't actually need to code anything here,
			// Launch.java will RMI daemonImpl.call()
			
			// ----------------------------------------
			// SEND MAPPED FILE TO CLIENT
			clientSocket = daemonSocket.accept(); // accept a request from Launch.java
			System.out.println("Accepted request from Launch");
			OutputStream cos = clientSocket.getOutputStream();
			FileInputStream fis = new FileInputStream("block" + id + ".out");
			while (true) {
				bytesRead = fis.read(buffer, 0, buffer.length);
				if (bytesRead <= 0) break;
				cos.write(buffer, 0, bytesRead);;
			}
			
			cos.close();
			fis.close();
			clientSocket.close();			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
			
		System.out.println("End of program");
	}
}




import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class DaemonImpl extends UnicastRemoteObject implements Daemon, Runnable {
	public static final String hostip = "localhost";
	public static final int port_offset = Server.port_offset;
	
	int port;
	ServerSocket daemonSocket;
	Registry registry;
	String registryName;

	public DaemonImpl() throws RemoteException {
		super();
	}
	
	public DaemonImpl(ServerSocket serverSocket) throws RemoteException {
		super();
		daemonSocket = serverSocket;
		port = serverSocket.getLocalPort();
		registryName = "//localhost/daemon" + port;
		
		registry = LocateRegistry.createRegistry(port + port_offset);
		registry.rebind(registryName, this);
		System.out.println(registryName + " ready to serve");
	}

	@Override
	public void call(Map map, String blockin, String blockout, Callback cb) throws RemoteException {
		System.out.println(registryName + " is called");
		map.executeMap(blockin, blockout);
		cb.completed();
	}

	@Override
	public void run() {
		try {
			System.out.println(registryName + " waiting for connection");
			daemonSocket.setSoTimeout(60000); // Split.java has 1 minute to connect
			Socket clientSocket = daemonSocket.accept();
			System.out.println(registryName + " connected with Split process");
			
			InputStream cis = clientSocket.getInputStream();
			FileOutputStream fos = new FileOutputStream("block" + port + ".in"); // each existing Daemon correspond to 1 client, so we can use the Daemon port as the client ID 
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
			daemonSocket.setSoTimeout(1200000);   // Launch.java has 12 minutes to connect after Split
			clientSocket = daemonSocket.accept(); // accept a request from Launch.java
			System.out.println(registryName + " accepted request from Launch");
			OutputStream cos = clientSocket.getOutputStream();
			FileInputStream fis = new FileInputStream("block" + port + ".out");
			while (true) {
				bytesRead = fis.read(buffer, 0, buffer.length);
				if (bytesRead <= 0) break;
				cos.write(buffer, 0, bytesRead);;
			}
			
			cos.close();
			fis.close();
			clientSocket.close();	

			System.out.println(registryName + " finished serving\n");
		}
		catch (SocketTimeoutException e) {
			System.out.println(registryName + " time out\n");
		}
		catch (Exception e) {
			e.printStackTrace();			
		}
			
		try {
			daemonSocket.close();
			registry.unbind(registryName);
		}
		catch (Exception e) {
			System.out.println(registryName + " failed to close socket or unbind registry");
		}		
	}
}


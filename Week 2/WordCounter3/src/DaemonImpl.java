
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Semaphore;

public class DaemonImpl extends UnicastRemoteObject implements Daemon, Runnable {
	public static final String hostip = "localhost";
	public static final int port_offset = Server.port_offset;
	
	Socket clientSocket;
	Registry registry;
	String registryId;
	String registryName;
	Semaphore waitRMI; 
	
	
	public DaemonImpl() throws RemoteException {
		super();
	}
		
	public DaemonImpl(Socket socket, String newRegistryId) throws RemoteException {
		super();
		clientSocket = socket;
		
		registryId = newRegistryId;
		registryName = "//localhost/daemon" + registryId;
		registry = LocateRegistry.getRegistry(socket.getLocalPort() + port_offset);				
		registry.rebind(registryName, this);
		
		waitRMI = new Semaphore(1);
	}

	@Override
	public void call(Map map, String blockin, String blockout, Callback cb) throws RemoteException 
	{
		System.out.println(registryName + ": RMI called");
		
		if (Files.exists(Paths.get(blockin)))
			map.executeMap(blockin, blockout);	
		else {
			System.out.println(registryName + ": file '" + blockin + "' doesn't exist");			
			try {
				FileOutputStream fos = new FileOutputStream(blockout);
				fos.write("FILE_NOT_FOUND - 404\n".getBytes());
				fos.close();
			} catch (Exception ex) {
				System.out.println(registryName + ": error in handling FileNotFound error");
				//ex.printStackTrace();
			}
		}
		
		cb.completed();
		waitRMI.release();
	}
	
	//----------
	
	public void runPut(String filename) throws IOException {
		System.out.println(registryName + ": putting file '" + filename + "'");
		
		byte[] buffer = new byte[Server.buffer_size];
		int bytesRead;
			
		InputStream cis = clientSocket.getInputStream();
		FileOutputStream fos = new FileOutputStream(filename + ".in"); 		
		while (true) {			
			bytesRead = cis.read(buffer, 0, buffer.length);
			if (bytesRead <= 0) break;
			fos.write(buffer, 0, bytesRead);
		}
		
		cis.close();
		fos.close();
		clientSocket.close();
		
		System.out.println(registryName + ": putting file '" + filename + "' success");
	}
	
	/****************/
	public void runGet(String filename) throws IOException, InterruptedException {
		System.out.println(registryName + ": getting file '" + filename + "'");
		
		byte[] buffer = new byte[Server.buffer_size];
		int bytesRead;
		OutputStream cos = null;
		
		try {
			waitRMI.acquire();  // explain below
		
			cos = clientSocket.getOutputStream(); 
			cos.write((registryId + "\n").getBytes()); // send registry name to Launch, to let it call RMI		
			
			
			// Launch.java will RMI daemonImpl.call(), which will waitRMI.release()
			// --if we waitRMI.acquire() here instead of above, in unlucky case the RMI will arrive
			// --and call waitRMI.release() before waitRMI.acquire(), and this daemon will be stuck forever
			System.out.println(registryName + " waiting for RMI");
			waitRMI.acquire();
			System.out.println(registryName + " RMI finished");
			
			// SEND MAPPED FILE TO CLIENT
			FileInputStream fis = new FileInputStream(filename + ".out");
			while (true) {
				bytesRead = fis.read(buffer, 0, buffer.length);
				if (bytesRead <= 0) break;
				cos.write(buffer, 0, bytesRead);;
			}
			
			cos.close();
			fis.close();
			clientSocket.close();	
			
			System.out.println(registryName + ": getting file '" + filename + "' success");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/****************/
	@Override
	public void run() {
		try {
			System.out.println(registryName + ": serving customer");
			clientSocket.setSoTimeout(60000*30); // wait max 30 minute
			
			String message;
			String[] args;
			String command, filename;								
					
		    InputStream cis = clientSocket.getInputStream();					    						
			message = "";
			while (true) {
				char ch = (char)cis.read();
				if (ch=='\n') break;
				else message = message + ch;
			}			
			args = message.trim().split("\\s+"); // split by whitespace
					
			command = args[0];
			filename = args[1];
						
			if (command.equals("put")) runPut(filename);
			else if (command.equals("get")) runGet(filename);
			else System.out.println(registryName + ": unknown command: '" + command + "'. Daemon exited");
		}
		catch (SocketTimeoutException e) {
			System.out.println(registryName + ": client timeout");
		}
		catch (Exception e) {
			System.out.println(registryName + ": unknown error. Please check");
			//e.printStackTrace();
		}
		
		try {
			registry.unbind(registryName);			
		}
		catch (Exception e) {
			System.out.println(registryName + " failed to unbind registry");
		}
	}
}


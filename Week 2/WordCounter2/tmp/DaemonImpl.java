

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

	int port;

	public DaemonImpl() throws RemoteException {
		super();
	}
	
	public DaemonImpl(int p) throws RemoteException {
		super();
		port = p;
		Naming.rebind("//localhost/daemon" + port);
	}

	@Override
	public void call(Map map, String blockin, String blockout, Callback cb) throws RemoteException {
		map.executeMap(blockin, blockout);
		cb.completed();
	}

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Please input port number");
			return;
		}

		try {
			int port = Integer.parseInt(args[0]);
			DaemonImpl daemon = new DaemonImpl(port);

			ServerSocket serverSocket = new ServerSocket(port);
			while (true) {
				Socket clientSocket = serverSocket.accept();

				//take filename.in from split

				clientSocket = serverSocket.accept();
				//send filename.out to launch
			}

		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}


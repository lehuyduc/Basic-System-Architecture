

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Daemon extends Remote {
	public void call(Map m, String blockin, String blockout, Callback cb) throws RemoteException;
}

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Daemon extends Remote {
    public void localExec(String s, Console c) throws RemoteException;
}

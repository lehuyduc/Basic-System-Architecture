import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ConsoleImpl extends UnicastRemoteObject implements Console {

    protected ConsoleImpl() throws RemoteException {
    }

    @Override
    public void println(String s) throws RemoteException {
        System.out.println(s);
    }
}

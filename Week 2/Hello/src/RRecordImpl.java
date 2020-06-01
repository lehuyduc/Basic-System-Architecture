import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RRecordImpl extends UnicastRemoteObject implements RRecord {
    SRecord record;

    public RRecordImpl(SRecord sr) throws java.rmi.RemoteException {
        record = sr;
    }

    @Override
    public String getName() throws RemoteException {
        return null;
    }

    @Override
    public String getEmail() throws RemoteException {
        return null;
    }
}

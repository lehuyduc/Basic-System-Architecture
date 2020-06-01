import java.rmi.*;

public interface Hello extends Remote {
	public void sayHello()
		throws RemoteException;
}
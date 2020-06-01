import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

public class HelloImpl extends UnicastRemoteObject implements Hello {
	String message;
 
	public HelloImpl(String msg) throws java.rmi.RemoteException {
		message = msg;
	}
	
	public void sayHello() throws java.rmi.RemoteException {
		System.out.println(message);
	}

	public static void main(String args[]) {
		try {
			Hello obj = new HelloImpl("hello Hanoi");
			Naming.rebind("//localhost/my_server", obj);
			System.out.println("HelloImpl " + " bound in registry");
		} catch (Exception e) {e.printStackTrace();}
	}
}
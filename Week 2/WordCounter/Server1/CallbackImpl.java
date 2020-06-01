

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Semaphore;

public class CallbackImpl extends UnicastRemoteObject implements Callback {
	   int nbnode;
	   Semaphore semaphore;
	   
	   public CallbackImpl(int n) throws RemoteException, InterruptedException {
		   nbnode = n;
		   semaphore = new Semaphore(n);
		   semaphore.acquire(n);
	   }
	   
	   public void completed() throws RemoteException {		  
		   System.out.println("Callback is completing");
		   semaphore.release(1); 
	   }
	   
	   public synchronized void waitforall() throws RemoteException {
		   System.out.println("Callback is waiting");
		   try {
			   semaphore.acquire(nbnode);
		   } catch (InterruptedException e) {
			   e.printStackTrace();
		   }
		   System.out.println("Callback is completed");
	   }
}

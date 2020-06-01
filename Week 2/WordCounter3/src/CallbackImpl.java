

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
		   semaphore.release(1); 
	   }
	   
	   public synchronized void waitforall() throws RemoteException {
		   try {
			   semaphore.acquire(nbnode);
		   } catch (InterruptedException e) {
			   e.printStackTrace();
		   }
	   }
}

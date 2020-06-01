import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;

import com.sun.prism.impl.Disposer.Record;

public class PadImpl extends UnicastRemoteObject implements Pad {
	
	private static final long serialVersionUID = 1;
	
	Hashtable<String, RRecord> table = new Hashtable<String, RRecord>();
	int numPad;
	Pad other = null;
	
	public PadImpl(int n) throws RemoteException {
		numPad = n;
	}
	
	public void add(SRecord sr) throws RemoteException {
		try {
			RRecord rr = new RRecordImpl(sr.getName(), sr.getEmail());
			table.put(sr.getName(), rr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public RRecord consult(String n, boolean forward) throws RemoteException {
		try {
			RRecord rr = table.get(n);
			if (rr==null && forward) {
			   other = (Pad)Naming.lookup("//localhost:4000/Pad"+((numPad%2)+1));
			   
			}
			return rr;
		} catch (Exception e) {
			System.out.println("PadImpl error: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String args[]) {
		try {
			Integer I = new Integer(args[0]);
			int nump = I.intValue();
			
			Naming.rebind("//localhost:4000/Pad"+nump, new PadImpl(nump));
			System.out.println("Pad"+nump+"bound in registry");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}



import java.rmi.RemoteException;

public class Slave extends Thread{
	Map map;
	String blockin;
	String blockout;
	Callback cb;
	
	public Slave(Map map, String blockin, String blockout, Callback cb) {
		this.map = map;
		this.blockin = blockin;
		this.blockout = blockout;
		this.cb = cb;
	}
	
	public void run() {
		try {
			map.executeMap(blockin, blockout);
			cb.completed();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}	

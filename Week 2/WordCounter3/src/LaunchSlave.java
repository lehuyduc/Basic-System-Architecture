
public class LaunchSlave implements Runnable {
	Daemon daemon;
	WordCount wc;
	String blockin, blockout;
	Callback cb;
	
	LaunchSlave(Daemon d, WordCount wordCount, String filein, String fileout, Callback callback) {
		daemon = d;
		wc = wordCount;
		blockin = filein;
		blockout = fileout;
		cb = callback;
	}

	@Override
	public void run() {
		try {
			daemon.call(wc, blockin, blockout, cb);
		} catch (Exception e) {
			System.out.println("Error in launch slave. Please check");
			//e.printStackTrace();
		}
	}
	
}

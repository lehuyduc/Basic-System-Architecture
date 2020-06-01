
import java.util.Vector;
import java.io.File;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class Split {
	public static final String hostip = "localhost";
	
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Split need filepath and port number of servers");
		}
		
		try {
			String filename = args[0];
			Vector<Integer> ports = new Vector<>();
			for (int i=1; i<args.length; i++) 
				ports.add(Integer.parseInt(args[i]));			
			int nbNodes = ports.size();
			
			File file = new File(filename);
			Scanner scanner = new Scanner(file);
			long totalBytesRead = 0;
			long fileSize = file.length();
			long newSize = fileSize / ports.size() + 1; 
			
			System.out.println("size of file is " + file.length());
			for (int i=0; i<ports.size(); i++) {
				Socket socket = new Socket(hostip, ports.elementAt(i));
				System.out.println("Connecting to port " + ports.elementAt(i));
				OutputStream daemonOs = socket.getOutputStream();
				long bytesRead = 0;
				
				while (scanner.hasNext()) {
					String word = scanner.next();
					bytesRead += word.length() + 1; // +1 to include space or \n
					word = word + "\n";
					daemonOs.write(word.getBytes(), 0, word.length());
					if (bytesRead > newSize) break;
				}
				socket.close();
				daemonOs.close();
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}

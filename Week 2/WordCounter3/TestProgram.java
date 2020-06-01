import java.io.File;
import java.io.IOException;
import java.util.Vector;

public class TestProgram {
	
	// -1: close needed ports. Use if "already binded" error occurs
	// 0: compile only	
	// 1: run only

	private static int mode = 0;
	private static Vector<Process> processes = new Vector<>();
		
	// program to test my project
	// all .java files and test1->5 in folder src
	// TestProgram.java and closePorts.sh is in the same folder as src
	
	// step 1: java TestProgram
	// step 2: in ./server1, java Server 8081. Server 2 and 3 use 8082 and 8083
	// step 3: java TestProgram 1
	public static void main(String[] args) {
		if (args.length == 1) mode = Integer.parseInt(args[0]);
		else if (args.length > 1) {
			System.out.println("Only 1 or 0 arg");
			return;
		}
		if (mode>1 || mode<-1) mode = 0;
		
		try {
			if (mode==-1) {
				closePorts();
				return;
			}
			
			if (mode==0) {
				execString("cp -a ./src/. ./client1/","",true);
				compileAll("./client1");
				
				execString("cp -a ./client1/. ./client2/","",true);
				execString("cp -a ./client1/. ./client3/","",true);
				execString("cp -a ./client1/. ./server1/","",true);
				execString("cp -a ./client1/. ./server2/","",true);
				execString("cp -a ./client1/. ./server3/","",true);

				System.out.println("Setup successful");
			}

			if (mode==1) {
				// basic upload/download
				execString("java Split test1 8081 8082 8083", "./client1", true);
				execString("java Split test2 8081 8082 8083", "./client2", true);
				execString("java Launch 3 8081 8082 8083 test1", "./client1", true);
				execString("java Split test3 8081 8082 8083", "./client3", true);
				execString("java Launch 3 8081 8082 8083 test3", "./client3", true);
				execString("java Launch 3 8081 8082 8083 test2", "./client2", true);
				
				// upload here download there
				execString("java Split test4 8081 8082", "./client1", true);
				execString("java Launch 2 8081 8082 test4", "./client2",true);
				execString("java Split test5 8083", "./client1", true);
				execString("java Launch 1 8083 test5", "./client3", true);		
				
				// edge case: see test6
				execString("java Split test6 8081 8082 8083", "./client1", true);
				execString("java Launch 3 8081 8082 8083 test6", "./client1", true);
			}
			
			for (Process proc : processes) {
				if (proc.isAlive()) proc.destroyForcibly();
			}			
		}
		catch (Exception e) {
			e.printStackTrace();
		}		
	}
    
	/**********************/
	static void execString(String command, String location, boolean wait) throws IOException, InterruptedException {
		if (location==null || location.equals("")) location = "./";
		
		Process proc = Runtime.getRuntime().exec(command, null, new File(location));		
		if (wait) proc.waitFor();	
		processes.add(proc);
	}
	
	static void compileAll(String folder) throws IOException, InterruptedException {
		execString("javac Split.java", folder, true);
		execString("javac Launch.java", folder, true);
		execString("javac LaunchLocal.java", folder, true);
		execString("javac Server.java", folder, true);
	}
	
	static void closePorts() throws IOException, InterruptedException {
		execString("sudo chmod +x closePorts.sh", "", true);
		execString("sudo ./closePorts.sh", "", true);
		System.out.println("Close ports successful");
	}

}



public class LaunchLocal {	
	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		
		try {
			String filename = args[0];			
			
			WordCount wc = new WordCount();
			wc.executeMap(filename, filename + ".reduce");
		}
		catch (Exception e) {
			e.printStackTrace();
		}	
		
		long endTime = System.currentTimeMillis();
		System.out.println("Total time = " + (endTime - startTime) + " ms");
		System.exit(0);
	}
}

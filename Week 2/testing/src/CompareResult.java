import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

public class CompareResult {
	
	public static void main(String[] args) {
		try {
			if (args.length < 2) {
				System.out.println("Need 2 args: filename1, filename2");
				return;
			}
			
			HashMap<String,Integer> hm1 = new HashMap<String,Integer>();
			HashMap<String,Integer> hm2 = new HashMap<String,Integer>();
			
			String file1 = args[0], file2 = args[1];
			Scanner scanner1 = new Scanner(new File(file1));
			Scanner scanner2 = new Scanner(new File(file2));
			
			while (scanner1.hasNext()) {
				String line1 = scanner1.nextLine();
				String line2 = scanner2.nextLine();
				
				String[] args1, args2;
				String word1, word2;
				int c1, c2;
				
				args1 = line1.split(" - ");
				args2 = line2.split(" - ");
				
				word1 = args1[0];
				word2 = args2[0];
				
				c1 = Integer.parseInt(args1[1]);
				c2 = Integer.parseInt(args2[1]);
				
				if (hm1.containsKey(word1)) hm1.put(word1, hm1.get(word1) + 1);
				else hm1.put(word1, 1);
				
				if (hm2.containsKey(word2)) hm2.put(word2, hm2.get(word2) + 1);
				else hm2.put(word2, 1);				
			}
			
			for (String key : hm1.keySet()) {
				if (hm1.get(key) != hm2.getOrDefault(key, -1)) {
					System.out.println(key);
					System.out.println("Wrong result");
					return;
				}
			}
			System.out.println("Correct");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}

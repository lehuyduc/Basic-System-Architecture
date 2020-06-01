import java.rmi.*;

public class HelloClient {
	public static void main(String args[]) {
		try {
				Hello obj = (Hello) Naming.lookup("//sd-127206.dedibox.fr/my_server");
				obj.sayHello();
		} catch (Exception exc) {exc.printStackTrace();}
	}
}

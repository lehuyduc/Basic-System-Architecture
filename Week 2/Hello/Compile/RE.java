import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class RE{

    public static void main(String[] args) {
        String command = args[0];
        try {
            Daemon d = (Daemon) Naming.lookup("//localhost/daemon");
            d.localExec(command, new ConsoleImpl());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

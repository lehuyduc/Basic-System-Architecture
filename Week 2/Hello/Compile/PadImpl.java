import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PadImpl extends UnicastRemoteObject implements Pad {
    int padnum;
    HashMap<String, RRecord> records;
    Pad other;

    protected PadImpl(int num) throws RemoteException {
        padnum = num;
        records = new HashMap<String, RRecord>();
    }

    @Override
    public void add(SRecord sr) throws RemoteException {
        records.put(sr.getName(), (RRecord)sr);
    }

    @Override
    public RRecord consult(String n, boolean forward) throws RemoteException {
        System.out.println("Pad " + padnum + " consulting");
        try {
            RRecord rr = records.getOrDefault(n, null);
            if (rr == null && forward) {
                if (other==null) {
                    String otherName = "//localhost/Pad" + (3-padnum);
                    other = (Pad) Naming.lookup(otherName);
                    System.out.println("Pad" + padnum + " forwarding");
                }
                if (other!=null) rr = other.consult(n, false);
            }

            if (rr==null) System.out.println("Pad" + padnum + ": user not found");
            return rr;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String args[]) {
        System.setProperty("java.rmi.server.hostname","127.0.0.1");
        try {
            Integer I = new Integer(args[0]);
            int padnum = I.intValue();

            Naming.rebind("//localhost/Pad"+padnum, new PadImpl(padnum));
            System.out.println("Pad"+padnum+" bound in registry");
        } catch (Exception e) {e.printStackTrace();}
    }
}

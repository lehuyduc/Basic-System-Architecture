
public class RRecordImpl implements RRecord {
	private String name;
	private String email;
	
	public RRecordImpl (String n, String e) {
		name = n;
		email = e;
	}
	
	public String getName() {
		return name;
	}
	
	public String getEmail() {
		return email;
	}
}

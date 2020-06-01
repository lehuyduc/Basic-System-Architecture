public class SRecordImpl implements SRecord, RRecord {
    private String name;
    private String email;

    public SRecordImpl(String name2, String email2) {
        name = name2;
        email = email2;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}

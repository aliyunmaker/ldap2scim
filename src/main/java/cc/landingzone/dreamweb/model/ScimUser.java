package cc.landingzone.dreamweb.model;

public class ScimUser implements Comparable<ScimUser> {

    private String id;
    private String externalId;
    private String userName;
    private String displayName;
    private String firstName;
    private String lastName;
    private String email;

    @Override
    public int compareTo(ScimUser o) {
        if (o == null || o.getId() == null) {
            return 1;
        }
        if (this.id == null) {
            return -1;
        }
        return id.compareTo(o.getId());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}

package ldap2scim.model;

public class ScimUserResourceName {

    private String familyName;
    private String givenName;

    public String getFamilyName() {
        return familyName;
    }

    public ScimUserResourceName(String familyName, String givenName) {
        super();
        this.familyName = familyName;
        this.givenName = givenName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

}

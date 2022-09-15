package ldap2scim.model;

import java.util.Objects;

/**
 * 简化SCIM中的userresource,将name,emails扁平化
 * 
 * @author charles
 *
 */
public class ScimUser implements Comparable<ScimUser> {

    private String id;
    private String externalId;
    private String userName;
    private String displayName;
    private String givenName;
    private String familyName;
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

    @Override
    public int hashCode() {
        return Objects.hash(displayName, email, externalId, familyName, givenName, userName);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ScimUser other = (ScimUser)obj;
        return Objects.equals(displayName, other.displayName) && Objects.equals(email, other.email)
            && Objects.equals(externalId, other.externalId) && Objects.equals(familyName, other.familyName)
            && Objects.equals(givenName, other.givenName) && Objects.equals(userName, other.userName);
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

    public String getFamilyName() {
        return familyName;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}

package ldap2scim.model;

import java.util.List;

public class UserResource {
    private String id;
    private String externalId;
    private String userName;
    private Boolean active;
    private String displayName;
    private List<UserResourceEmail> emails;
    private List<String> schemas;
    private UserResourceName name;

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

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<UserResourceEmail> getEmails() {
        return emails;
    }

    public void setEmails(List<UserResourceEmail> emails) {
        this.emails = emails;
    }

    public List<String> getSchemas() {
        return schemas;
    }

    public void setSchemas(List<String> schemas) {
        this.schemas = schemas;
    }

    public UserResourceName getName() {
        return name;
    }

    public void setName(UserResourceName name) {
        this.name = name;
    }

}

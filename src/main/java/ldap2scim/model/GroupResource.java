package ldap2scim.model;

import java.util.List;

public class GroupResource {
    private String id;
    private String externalId;
    private String displayName;
    private List<String> schemas;

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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setSchemas(List<String> schemas) {
        this.schemas = schemas;
    }

    public List<String> getSchemas() {
        return schemas;
    }

}

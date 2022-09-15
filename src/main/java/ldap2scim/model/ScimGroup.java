package ldap2scim.model;

import java.util.List;
import java.util.Objects;

public class ScimGroup {
    private String id;
    private String externalId;
    private String displayName;
    private List<String> schemas;

    @Override
    public int hashCode() {
        return Objects.hash(displayName, externalId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ScimGroup other = (ScimGroup)obj;
        return Objects.equals(displayName, other.displayName) && Objects.equals(externalId, other.externalId);
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

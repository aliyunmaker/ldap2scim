package ldap2scim.model;

public class LdapTask {

    private String uuid;
    private String searchBase;
    private String searchFilter;
    private String crontab;
    private boolean disable;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSearchBase() {
        return searchBase;
    }

    public void setSearchBase(String searchBase) {
        this.searchBase = searchBase;
    }

    public String getSearchFilter() {
        return searchFilter;
    }

    public void setSearchFilter(String searchFilter) {
        this.searchFilter = searchFilter;
    }

    public String getCrontab() {
        return crontab;
    }

    public void setCrontab(String crontab) {
        this.crontab = crontab;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

}

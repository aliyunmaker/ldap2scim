package ldap2scim.model;

public class TaskRecord {

    private String uuid;
    // private String searchBase;
    // private String searchFilter;
    // private String cronExpression;
    private String executeTime;
    private String result;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(String executeTime) {
        this.executeTime = executeTime;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

}

package ldap2scim.model;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

public class ScimUserResponse {

    @JSONField(name = "Resources")
    private List<ScimUserResource> resources;
    private int itemsPerPage;
    private int startIndex;
    private int totalResults;

    public List<ScimUserResource> getResources() {
        return resources;
    }

    public void setResources(List<ScimUserResource> resources) {
        this.resources = resources;
    }

    public int getItemsPerPage() {
        return itemsPerPage;
    }

    public void setItemsPerPage(int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

}

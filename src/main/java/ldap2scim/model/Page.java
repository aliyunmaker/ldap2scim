package ldap2scim.model;

import org.springframework.util.Assert;

/**
 * 类Page.java的实现描述：翻页相关
 *
 * @author charles 2015年9月23日 下午3:53:12
 */
public class Page {

    private Integer start;
    private Integer limit;
    private Integer page;

    private Integer total;

    public Page(Integer start, Integer limit) {
        super();
        Assert.notNull(start, "start can not be null!");
        Assert.notNull(limit, "limit can not be null!");
        this.start = start;
        this.limit = limit;
    }

    public Page(Integer start, Integer limit, Integer page) {
        super();
        this.start = start;
        this.limit = limit;
        this.page = page;
    }

    @Override
    public String toString() {
        return "Page [start=" + start + ", limit=" + limit + ", total=" + total + "]";
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPage() {
        return page;
    }

}

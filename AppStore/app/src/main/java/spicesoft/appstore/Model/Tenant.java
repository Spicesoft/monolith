package spicesoft.appstore.Model;

/**
 * Created by Vincent on 29/06/15.
 */
public class Tenant {

    private Tenant tenant = null;

    private String domain;
    private String name;

    public Tenant(){
    }

    public Tenant getInstance(){
        if (getTenant() == null) setTenant(new Tenant());
        return getTenant();
    }


    public String toString(){
        return "Name : " + getName() + "\n domain_url : " + getDomain();
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

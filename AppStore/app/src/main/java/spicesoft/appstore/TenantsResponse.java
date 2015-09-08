package spicesoft.appstore;

import java.util.ArrayList;

import spicesoft.appstore.Model.Tenant;

/**
 * Created by Vincent on 29/06/15.
 */
public interface TenantsResponse {

     void postGetTenant(ArrayList<Tenant> TenantList);

}

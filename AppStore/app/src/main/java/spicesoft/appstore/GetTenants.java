package spicesoft.appstore;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import spicesoft.appstore.Model.Tenant;

/**
 * Created by Vincent on 26/06/15.
 */
public class GetTenants extends AsyncTask<Void,Void,ArrayList<Tenant>> {

    private static final String WebService = "https://backoffice.cowork.io/api/v1/tenants/";
    private static final String token = "51d49610e250eecb128ba24f5a830e1998a9bc84";

    public TenantsResponse delegate = null;
    private Activity activity;

    public GetTenants(Activity a){
        activity = a;
    }


    @Override
    protected ArrayList<Tenant> doInBackground(Void... params) {

        ArrayList<Tenant> TenantList = new ArrayList<>();
        JSONArray response;
        ArrayList<JSONObject> responseList = new ArrayList<>();


        HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
        SSLContext context = null;
        try {
            context = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            context.init(null, new X509TrustManager[]{new NullX509TrustManager()}, new SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());


        HttpURLConnection urlConnection = null;


        try {

            URL url = new URL(WebService);

            urlConnection = (HttpURLConnection) url.openConnection();
            //urlConnection.setDoOutput(true);
            //urlConnection.setDoInput(true);
            //urlConnection.setUseCaches(false);
            urlConnection.setRequestMethod("GET");
            //urlConnection.setConnectTimeout(20000);
            urlConnection.addRequestProperty("Accept", "application/json");
            urlConnection.addRequestProperty("Authorization", "Token " + token);

            int responseCode = urlConnection.getResponseCode();

            if(responseCode == HttpStatus.SC_OK){
                String responseString = readStream(urlConnection.getInputStream());
                Log.d("Tenants", responseString);

                response = new JSONArray(responseString);
                Log.d("Tenants", "Response code:" + responseCode );

                for(int i = 0, count = response.length(); i< count; i++)
                {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        responseList.add(jsonObject);
                        Tenant tenant = new Tenant();
                        tenant.setName(jsonObject.getString("name"));
                        tenant.setDomain(jsonObject.getString("domain_url"));
                        TenantList.add(tenant);
                        Log.d("JSON", tenant.toString());
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }else{
                Log.v("Tenants", "Response code:" + responseCode );
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(urlConnection != null)
                urlConnection.disconnect();
        }

            return TenantList;

    }


    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }


    @Override
    protected void onPostExecute(ArrayList<Tenant> tenants) {
        super.onPostExecute(tenants);
        delegate.postGetTenant(tenants);

    }


    public class NullHostNameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String hostname, SSLSession session) {
            Log.i("RestUtilImpl", "Approving certificate for " + hostname);
            return true;
        }

    }

}

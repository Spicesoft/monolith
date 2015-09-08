package spicesoft.appstore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import spicesoft.appstore.KisokMode.KioskModeActivity;
import spicesoft.appstore.Model.Tenant;


public class LoginActivity extends KioskModeActivity implements TenantsResponse{

    public static final String PREFS_NAME = "spicesoft.appstore";
    public static final String TAG = "LoginActivity";

    private ArrayList<Tenant> TenantList;
    private String userTenant;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        enableFullKioskMode();
    }


    public void TenantButtonPressed(View view) {

        EditText editText = (EditText) findViewById(R.id.editText);
        userTenant = editText.getText().toString();
        userTenant = userTenant.toLowerCase();
        userTenant = userTenant.replace(" ", "");
        userTenant = userTenant.replace("\n", "");

        if(userTenant.isEmpty()){
            Toast.makeText(this, "Please enter a tenant", Toast.LENGTH_SHORT).show();
            return;
        }

        GetTenants getTenants = new GetTenants(this);
        getTenants.delegate = this;
        getTenants.execute();

    }


    @Override
    public void postGetTenant(ArrayList<Tenant> TenantList) {

        String tempTenantName = "";

        for (int i=0; i<TenantList.size(); i++){

            Log.d("postGetTenant", TenantList.get(i).toString());

            tempTenantName = TenantList.get(i).getName();
            tempTenantName = tempTenantName.toLowerCase();
            tempTenantName = tempTenantName.replace(" ", "");

            Log.d("postGetTenant", userTenant + " / "+ tempTenantName);
            if(tempTenantName.equals(userTenant)){
                Toast.makeText(this, "Tenant found", Toast.LENGTH_SHORT).show();
                EditText editText = (EditText) findViewById(R.id.editText);
                editText.setText(tempTenantName , TextView.BufferType.EDITABLE);
                storeTenant(TenantList.get(i));
                setConfigDone();
                startActivity(new Intent(this, MainActivity.class));
                break;
            }
        }
    }

    private void setConfigDone() {
        SharedPreferences settings;
        settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        settings.edit().putBoolean("config_done", true).apply();
    }


    public void storeTenant(Tenant t){
        SharedPreferences settings;
        settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        settings.edit().putString("tenant_name", t.getName()).apply();
        settings.edit().putString("tenant_domain_url", t.getDomain()).apply();
    }
}

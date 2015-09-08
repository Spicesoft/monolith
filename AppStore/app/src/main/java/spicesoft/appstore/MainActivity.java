package spicesoft.appstore;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import spicesoft.appstore.AsyncTasks.ApkDownloader;
import spicesoft.appstore.AsyncTasks.AsyncResponse;
import spicesoft.appstore.AsyncTasks.InstallDownloadedApp;
import spicesoft.appstore.AsyncTasks.getAppInfoFromServer;
import spicesoft.appstore.AsyncTasks.getAvailableAppsFromServer;
import spicesoft.appstore.KisokMode.KioskModeNfcActivity;
import spicesoft.appstore.Model.App;
import spicesoft.appstore.Model.ServerInfo;
import spicesoft.appstore.Model.Tenant;
import spicesoft.appstore.NFC.NfcResponse;



public class MainActivity extends KioskModeNfcActivity implements AsyncResponse, NfcResponse{

    private static final boolean DEBUG = true;
    private static final String TAG = "MainActivity";

    public static final String PREFS_NAME = "spicesoft.appstore";
    private String AppStoreUrl = "http://spicesoft.cowork.io:8000/logout/?next=/webapps/";

    private Tenant tenant = new Tenant();


    private WebView mWebView;
    private getAppInfoFromServer appinfo;

    static class PInfo {
        private String appname = "";
        private String pname = "";
        private String versionName = "";
        private int versionCode = 0;
    }


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updater_activity);

        initTasks();

        Bundle extras = getIntent().getExtras();
        try {
            if (extras != null) {
                String data = extras.getString("default_app"); // retrieve the data using keyName
                if (data.equals("clear")) {
                    Log.d(TAG, "Clear preferences");
                    SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    preferences.edit().putString("default_app", "").apply();

                }
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        enableFullKioskMode();
        mWebView = (WebView) findViewById(R.id.web_content);

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView  view, String  url)
            {
                //Only URL that contains cowork.app scheme can be opened by a third party app
                //Other URLs are openned in the webview.
                if (url.contains("cowork.app://")) {
                    //Load new URL Don't override URL Link
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                }else {
                    // Return true to override url loading (In this case do nothing).
                    view.loadUrl(url);
                    return false;
                }
            }
                                  });
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setUserAgentString("COWORK");

    }


    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_updater_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onResume() {
        super.onResume();

        onFirstRun();

        SharedPreferences settings;
        settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        tenant.setName(settings.getString("tenant_name", null));
        tenant.setDomain(settings.getString("tenant_domain_url", null));
        AppStoreUrl = tenant.getDomain() + "/logout/?next=/webapps/";

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if(!(netInfo != null && netInfo.isConnectedOrConnecting())) {
            Intent intent = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK);
            intent.putExtra("extra_prefs_show_button_bar", true);
            intent.putExtra("wifi_enable_next_on_connect", true);
            startActivity(intent);

        }else{
            mWebView.loadUrl(AppStoreUrl);
            runConfig();
            startDefaultApp();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mWebView.loadUrl(AppStoreUrl);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_reset_config:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.factory_reset_message)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                                builder.setMessage(R.string.reboot_message)
                                        .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                                                SharedPreferences.Editor editor = preferences.edit();
                                                editor.clear();
                                                editor.apply();
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("cowork.app://appstore")));
                                            }
                                        });
                                // Create the AlertDialog object
                                builder.create();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });

                Dialog d = builder.create();
                d.setCanceledOnTouchOutside(false);
                d.show();

                return true;

            case R.id.action_wifi_settings:
                Intent intent = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK);
                intent.putExtra("extra_prefs_show_button_bar", true);
                startActivityForResult(intent, 1);
                return true;


            case R.id.test:
                SharedPreferences settings;
                settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                settings.edit().putString("default_app", "cowork.app://monolith/aHR0cDovL2Nvd29yay5pby8=").apply();
                startActivity(new Intent(this, MainActivity.class));
                return true;


            case R.id.check_updates:
                initTasks();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void initTasks(){
        getAvailableAppsFromServer appsFromServer = new getAvailableAppsFromServer(this);
        appsFromServer.delegate = this;
        appsFromServer.execute(ServerInfo.BASE_URL + ServerInfo.APPS_LIST_FILE);

        appinfo = new getAppInfoFromServer(this);
        appinfo.delegate = this;
    }



    // Get Information about Only Specific application which is Install on Device.
    public String getInstallPackageVersionInfo(String appName)
    {
        String InstallVersion = "";
        ArrayList<PInfo> apps = getInstalledApps(false); // false = no system packages
        final int max = apps.size();
        for (int i=0; i<max; i++)
        {
            //Log.d(TAG, "Installed app : " + apps.get(i).appname);
            if(apps.get(i).appname.equals(appName))
            {
               // InstallVersion = "Install Version Code: "+ apps.get(i).versionCode+
               //         " Version Name: "+ apps.get(i).versionName;
                if(DEBUG)Log.d(TAG, InstallVersion);
                break;
            }
        }

        return InstallVersion;
    }


    private boolean isAppInstalled(String appName){

        ArrayList<PInfo> apps = getInstalledApps(false); // false = no system packages
        final int max = apps.size();
        for (int i=0; i<max; i++)
        {
            //Log.d(TAG, "Installed app : " + apps.get(i).appname);
            if (apps.get(i).appname.toLowerCase().equals(appName.toLowerCase()))
            {
                return true;
            }
        }
        return false;
    }


    private boolean isAppInstalled(String pkgName, String appName){

        ArrayList<PInfo> apps = getInstalledApps(false); // false = no system packages
        final int max = apps.size();
        for (int i=0; i<max; i++)
        {
            //Log.d(TAG, "Installed app : " + apps.get(i).pname + "  " + apps.get(i).appname);
            if(apps.get(i).pname.equals(pkgName))
            {
                return true;
            }
        }
        return false;
    }


    private Boolean isServerVersionNewer(String appName, int ServerVersionCode)
    {
        ArrayList<PInfo> apps = getInstalledApps(false); // false = no system packages
        final int max = apps.size();
        for (int i=0; i<max; i++)
        {
            if(apps.get(i).appname.equals(appName))
            {
                if (DEBUG)Log.d(TAG, "AppName = "+ apps.get(i).appname + " / Version = " + apps.get(i).versionName + " / PackageName = " + apps.get(i).pname);

                if(ServerVersionCode > apps.get(i).versionCode)
                {
                    return true;
                    //Not up-to-date => download the new version and install it !

                }else return false;
            }
        }
        return false;
    }


    private ArrayList<PInfo> getInstalledApps(boolean getSysPackages)
    {
        ArrayList<PInfo> res = new ArrayList<PInfo>();
        List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);

        for(int i=0;i<packs.size();i++)
        {
            PackageInfo p = packs.get(i);
            if ((!getSysPackages) && (p.versionName == null)) {
                continue ;
            }
            PInfo newInfo = new PInfo();
            newInfo.appname = p.applicationInfo.loadLabel(getPackageManager()).toString();
            newInfo.pname = p.packageName;
            newInfo.versionName = p.versionName;
            newInfo.versionCode = p.versionCode;
            res.add(newInfo);
        }
        return res;
    }


    @Override
    public void postInstallDownloadedAppResult(App app) {
        mWebView.reload();
    }

    @Override
    public void postApkDownloader(App app) {
        new InstallDownloadedApp(this, this).execute(app);
    }

    @Override
    public void postUninstallApp(App app) {

    }

    @Override
    public void postGetAvailableAppFromServerResult(List<String> appList) {
        appinfo.execute(appList);
        Log.d(TAG, "Available apps received");
    }

    @Override
    public void postGetAppInfoFromServerResult(List<App> app) {

        for (int i = 0; i < app.size(); i++) {

            if (isAppInstalled(app.get(i).pkgName , app.get(i).name)) {

                if (isServerVersionNewer(app.get(i).name, app.get(i).versionCode)) {
                    Log.d(TAG, "New Version available (" + app.get(i).versionCode + ") => Starting download");
                    //Download + install
                    new ApkDownloader(this, this).execute(app.get(i));

                } else {
                    // The app is up to date
                }
            } else {
                //App is not installed => download it and install it for the 1st time
                new ApkDownloader(this, this).execute(app.get(i));
            }
        }
    }

    @Override
    public void NfcIntentReceived(Intent i) {

    }

    /**
     * This method is executed on the very first run of the app.
     * It will be used to set the tablet configuration.
     */
    public void onFirstRun(){

        SharedPreferences settings;
        settings = getSharedPreferences(PREFS_NAME, MODE_WORLD_WRITEABLE);

        if (settings.getBoolean("first_run", true)) {
            if(DEBUG)Log.d(TAG, "First run");

            settings.edit().putBoolean("first_run", false).apply();
            settings.edit().putBoolean("config_done", false).apply();
            settings.edit().putString("default_app", null).apply();

            //Disable systemui
            String command = "pm disable com.android.systemui";
            Process proc = null;
            try {
                proc = Runtime.getRuntime().exec(new String[] { "su", "-c", command });
                proc.waitFor();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.reboot_message)
                    .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //Reboot Tablet
                            String command = "reboot";
                            Process proc = null;
                            try {
                                proc = Runtime.getRuntime().exec(new String[] { "su", "-c", command });
                                proc.waitFor();

                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            // Create the AlertDialog object
            builder.create();

        }else{
            if(DEBUG)Log.d(TAG, "Not first run");
        }
    }


    /**
     * Method that check if the Tenant is configured
     */
    public void runConfig(){

        if(DEBUG)Log.d(TAG, "Check Config");

        SharedPreferences settings;
        settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        if (settings.getBoolean("config_done", true)) {
            //the app is being launched for first time, do something
            if(DEBUG)Log.d(TAG, "config done");
            //startLockTask();
        }else{
            if(DEBUG)Log.d(TAG, "config not done");
            startActivity(new Intent(this, LoginActivity.class));
        }
    }


    public void startDefaultApp(){

        SharedPreferences settings;
        settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Log.d(TAG, "default app : " + settings.getString("default_app", ""));

        if(!Objects.equals(settings.getString("default_app", ""), "")) {

            String defaultApp = settings.getString("default_app", "");
            Log.d(TAG, "default app : " + defaultApp);
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(defaultApp)));
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

}
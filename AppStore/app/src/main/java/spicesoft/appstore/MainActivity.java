package spicesoft.appstore;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.webkit.WebView;
import java.util.ArrayList;
import java.util.List;
import spicesoft.appstore.AsyncTasks.ApkDownloader;
import spicesoft.appstore.AsyncTasks.AsyncResponse;
import spicesoft.appstore.AsyncTasks.GetVersionFromServer;
import spicesoft.appstore.AsyncTasks.InstallDownloadedApp;
import spicesoft.appstore.AsyncTasks.RebootDevice;
import spicesoft.appstore.AsyncTasks.SetDeviceOwner;
import spicesoft.appstore.AsyncTasks.getAppInfoFromServer;
import spicesoft.appstore.AsyncTasks.getAvailableAppsFromServer;
import spicesoft.appstore.KisokMode.KioskModeNfcActivity;
import spicesoft.appstore.Model.App;
import spicesoft.appstore.NFC.NfcResponse;
import spicesoft.appstore.Receiver.BasicDeviceAdminReceiver;


public class MainActivity extends KioskModeNfcActivity implements AsyncResponse, NfcResponse{

    private static final boolean DEBUG = true;
    private static final String TAG = "MainActivity";


    public static final String PREFS_NAME = "spicesoft.appstore";

    /**
     * Update server URL
     */
    private static final String baseUrl = "http://4ltrophyece.fr/tandoori/";

    private WebView mWebView;

    private App application = new App();

    /**
     * URL of the version file
     */
    private static final String BuildVersionPath= baseUrl;

    /**
     * Download directory on the device
     */
    private static final String downloadDirectory = "/Download/updates/";


    private static final String DefaultURL = "";

    private InstallDownloadedApp InstallApp;
    private ApkDownloader DlUpdate;
    private LaunchApp launcher = new LaunchApp();
    private getAppInfoFromServer appinfo;


    static class PInfo {
        private String appname = "";
        private String pname = "";
        private String versionName = "";
        private int versionCode = 0;
    }


    static class LaunchParam {
        public Activity a;
        public String pkgName = "";
        public String actName = "";
    }

    private LaunchParam launchParam = new LaunchParam();

    public SensorManager sensorManager;
    public Sensor proximitySensor;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updater_activity);

        enableFullKioskMode();
        mWebView = (WebView) findViewById(R.id.web_content);
        mWebView.getSettings().setUserAgentString("COWORK");
    }


    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if(!(netInfo != null && netInfo.isConnectedOrConnecting())) {
            Intent intent = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK);
            intent.putExtra("extra_prefs_show_button_bar", true);
            intent.putExtra("wifi_enable_next_on_connect", true);
            startActivityForResult(intent, 1);

        }else{
            //refreshAppStore();
            //mWebView.loadUrl(DefaultURL);
            mWebView.loadUrl("file:///android_asset/index.html");
        }

        onFirstRun();
        deviceOwnerConfig();
    }


    public void refreshAppStore(){

        getAvailableAppsFromServer appsFromServer = new getAvailableAppsFromServer(this);
        appsFromServer.delegate = this;
        appsFromServer.execute(baseUrl + "apps");

        appinfo = new getAppInfoFromServer(this);
        appinfo.delegate = this;

        GetVersionFromServer ServerVersion = new GetVersionFromServer();
        ServerVersion.delegate = this;

        UpdateDownloaderInstance.getInstance().setDlUpdate(new ApkDownloader(this));
        UpdateDownloaderInstance.getInstance().getDlUpdate().delegate = this;

        InstallApp = new InstallDownloadedApp();
        InstallApp.delegate = this;

        launchParam = new LaunchParam();
        launchParam.a = this;

    }


    // Get Information about Only Specific application which is Install on Device.
    public String getInstallPackageVersionInfo(String appName)
    {
        String InstallVersion = "";
        ArrayList<PInfo> apps = getInstalledApps(false); // false = no system packages
        final int max = apps.size();
        for (int i=0; i<max; i++)
        {
            Log.d(TAG, "Installed app : " + apps.get(i).appname);
            //apps.get(i).prettyPrint();
            if(apps.get(i).appname.equals(appName))
            {
                InstallVersion = "Install Version Code: "+ apps.get(i).versionCode+
                        " Version Name: "+ apps.get(i).versionName;
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
            Log.d(TAG, "Installed app : " + apps.get(i).appname);
            //apps.get(i).prettyPrint();
            if(apps.get(i).appname.equals(appName))
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
            Log.d(TAG, "Installed app : " + apps.get(i).pname + "  " + apps.get(i).appname);
            //apps.get(i).prettyPrint();
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


    public void launchApp(String packageName, String activityName){
        launchParam.pkgName = packageName;
        launchParam.actName = activityName;
        launcher.execute(launchParam);
    }


    public void uninstallApp(String packageName){

    }

    @Override
    public void postGetVersionFromServerResult(int ServerVersionCode, String ServerVersionName) {


        if (isAppInstalled(application.name)) {

            if (isServerVersionNewer(application.name, ServerVersionCode)) {
                Log.d(TAG, "New Version available (" + ServerVersionCode + ") => Starting download");
                //Download + install

            } else {
                // The app is up to date
                //launch app
            }
         }else {
            //App is not installed => download it and install it for the 1st time
            }
        }

    @Override
    public void postInstallDownloadedAppResult() {
        launchApp(application.pkgName, application.activityName);
    }


    @Override
    public void postDownloadUpdate() {
        InstallApp.execute(Environment.getExternalStorageDirectory() + downloadDirectory + application.apkName);
    }

    @Override
    public void postUninstallApp() {

    }

    @Override
    public void postGetAvailableAppFromServerResult(List<String> appList) {

        appinfo.execute(appList);
        Log.d(TAG, "Available apps received");

    }

    @Override
    public void postGetAppInfoFromServerResult(List<App> app) {

    }


    @Override
    public void NfcIntentReceived(Intent i) {

    }

    /**
     * This method is executed on the very first run of the app.
     * It will be used to set the tablet configurtion (e.g. center openning/closing hours, meeting room ID...)
     */
    public void onFirstRun(){

        SharedPreferences settings;
        settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        if (settings.getBoolean("first_run", true)) {
            if(DEBUG)Log.d(TAG, "First run");

            settings.edit().putBoolean("first_run", false).commit();
            settings.edit().putBoolean("config_done", false).commit();

        }else{
            if(DEBUG)Log.d(TAG, "Not first run");
        }
    }


    public void deviceOwnerConfig(){

        if(DEBUG)Log.d(TAG, "Check Config");

        SharedPreferences settings;
        settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        if (settings.getBoolean("config_done", true)) {
            //the app is being launched for first time, do something
            if(DEBUG)Log.d(TAG, "config done");

            startLockTask();


        }else{
            if(DEBUG)Log.d(TAG, "config not done");

            new SetDeviceOwner(this).execute();

        }
    }
}
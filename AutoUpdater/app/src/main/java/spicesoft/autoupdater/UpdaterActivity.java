package spicesoft.autoupdater;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class UpdaterActivity extends Activity implements AsyncResponse {


    private static final boolean DEBUG = true;
    private static final String TAG = "UpdaterActivity";

    private static final String baseUrl = "http://4ltrophyece.fr/tandoori/";
    private static final String ApkName = "monolith.apk";
    private static final String AppName = "Volaille futuriste" ;
    private static final String PackageName = "spicesoft.monolith";
    private static final String ActivityName = "MainActivity";
    private static final String BuildVersionPath= baseUrl + "version";
    private static final String downloadDirectory = "/Download/updates/";



    private int VersionCode;
    private String VersionName="";
    private String urlpath ;
    private String InstallAppPackageName;


    class PInfo {
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

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);


        urlpath = baseUrl + ApkName;

        //getInstallPackageVersionInfo(AppName);

        GetVersionFromServer ServerVersion = new GetVersionFromServer();
        ServerVersion.execute(BuildVersionPath);
        ServerVersion.delegate = this;
        //getPackages(AppName);

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
            //newInfo.icon = p.applicationInfo.loadIcon(getPackageManager());
            res.add(newInfo);
        }
        return res;
    }

    public void installDownloadedUpdate(String path){

        File file = new File(path);
        if(file.exists()){
            try {
                String command;
                command = "pm install -r " + path;
                Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", command });
                proc.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void launchApp(String packageName, String activityName){

        try {
            String command;
            command = "am start -n " + packageName + "/" + packageName + "." + activityName;
            Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", command });
            proc.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void uninstallApp(String packageName){

    }


    @Override
    public void postResult(int ServerVersionCode, String ServerVersionName) {
        TextView tv = (TextView) findViewById(R.id.textView);
        tv.setText(ServerVersionCode+"V"+ServerVersionName + " is available");

        if (isAppInstalled(AppName)) {

            if (isServerVersionNewer(AppName, ServerVersionCode)) {
                Log.d(TAG, "New Version available ("+ ServerVersionCode +") => Starting download");
                tv.setText("Starting update download");

                Button b = (Button)findViewById(R.id.installButton);
                b.setVisibility(View.VISIBLE);
                b.setEnabled(true);
                b.setText("Update Monolith");
                b.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (DEBUG) Log.d(TAG, "Downloading Monolith update");
                        //Download the update
                        new DownloadUpdate().execute(urlpath, ApkName, downloadDirectory);
                        installDownloadedUpdate(Environment.getExternalStorageDirectory() + downloadDirectory + ApkName);
                        launchApp(PackageName, ActivityName);
                    }
                });

                //Download the update
                new DownloadUpdate().execute(urlpath, ApkName, downloadDirectory);
            } else {
                // The app is up to date
                tv.setText("Monolith is up to date");
                Button b = (Button)findViewById(R.id.installButton);
                b.setVisibility(View.VISIBLE);
                b.setEnabled(true);
                b.setText("Launch Monolith");
                b.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //Launch Monolith
                        launchApp(PackageName, ActivityName);
                    }
                });
            }
         }else {
            //App is not installed => download it and install it for the 1st time
            tv.setText("Monolith is not installed yet");
            Button b = (Button)findViewById(R.id.installButton);
            b.setVisibility(View.VISIBLE);
            b.setEnabled(true);
            b.setText("Download & Install Monolith");
            b.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if(DEBUG)Log.d(TAG, "The app is up to date");
                    new DownloadUpdate().execute(urlpath, ApkName, downloadDirectory);
                    installDownloadedUpdate(Environment.getExternalStorageDirectory() + downloadDirectory + ApkName);
                    launchApp(PackageName, ActivityName);
                }
            });
            }
        }
}

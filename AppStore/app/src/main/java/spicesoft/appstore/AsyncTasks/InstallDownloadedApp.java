package spicesoft.appstore.AsyncTasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import spicesoft.appstore.Model.App;

/**
 * Async task that installs apk files using adb commands
 * Created by Vincent on 01/06/15.
 */
public class InstallDownloadedApp extends AsyncTask<App, Void, App> {

    private static final boolean DEBUG = true;
    private static final String TAG = "InstallDownloadedApp";

    public AsyncResponse delegate =null;
    public Activity activity;

    public ProgressDialog dialog;

    public InstallDownloadedApp(Activity a, AsyncResponse d){
        activity = a;
        delegate = d;
    }

    /**
     * Install apk file
     * @param params
     * @return
     */
    @Override
    protected App doInBackground(App... params) {

        App app = params[0];

        if(DEBUG) Log.d(TAG, "APK path is: " + App.downloadDir + app.apkName);
        File file = new File(App.downloadDir + app.apkName);
        if(file.exists()){
            try {
                if(DEBUG) Log.d(TAG, "Trying to install APK");
                String command;
                command = "pm install -r " + App.downloadDir + app.apkName;
                Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", command });
                proc.waitFor();
                if (DEBUG) Log.d(TAG, "Installation process: done");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return params[0];
    }

    @Override
    protected void onPostExecute(App app) {
        super.onPostExecute(app);
        delegate.postInstallDownloadedAppResult(app);
    }
}
package spicesoft.appstore.AsyncTasks;

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

    /**
     * Install apk file
     * @param params
     * @return
     */
    @Override
    protected App doInBackground(App... params) {

        String path = params[0].downloadDir;

        File file = new File(path);
        if(file.exists()){
            try {
                String command;
                command = "pm install -r " + path;
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
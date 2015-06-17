package spicesoft.appstore.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;

/**
 * Async task to uninstall an app using adb shell commands, with root access.
 * SuperSU needs to grant access.
 * Created by Vincent on 01/06/15.
 */
public class UninstallApp extends AsyncTask <String, Void, Void> {

    private static final boolean DEBUG = true;
    private static final String TAG = "AppUninstaller";

    public AsyncResponse delegate = null;

    /**
     * Async task that uses adb command to uninstall the app.
     * The app is uninstalled and the app's cache gets flushed.
     * In order to keep the data and cache after the package is removed, add the '-k'
     * flag to the uninstall command.
     * @param params is the package name of the app. (e.g. com.companyName.appName)
     * @return
     */

    @Override
    protected Void doInBackground(String... params) {

        File file = new File(params[0]);
        if(file.exists()){
            try {
                String command;
                command = "pm uninstall " + params[0];
                Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", command });
                proc.waitFor();
                if (DEBUG) Log.d(TAG, "Uninstall process: done");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Call the postUninstallApp method when the Async task is done.
     * @param aVoid
     */
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        delegate.postUninstallApp();
    }
}

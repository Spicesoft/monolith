package spicesoft.appstore.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;

/**
 * Async task that installs apk files using adb commands
 * Created by Vincent on 01/06/15.
 */
public class InstallDownloadedApp extends AsyncTask<String, Void, Void> {

    private static final boolean DEBUG = true;
    private static final String TAG = "InstallDownloadedApp";

    public AsyncResponse delegate =null;

    /**
     * Install apk file
     * @param params
     * @return
     */
    @Override
    protected Void doInBackground(String... params) {

        String path = params[0];

        File file = new File(path);
        if(file.exists()){
            try {
                String command;
                command = "pm install -r " + path;
                Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", command });
                proc.waitFor();
                if (DEBUG) Log.d(TAG, "Installation process: done");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        delegate.postInstallDownloadedAppResult();
    }
}

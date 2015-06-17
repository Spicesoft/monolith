package spicesoft.appstore.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

/**
 * ASync task that reboots the device using adb commands
 * Created by Vincent on 01/06/15.
 */
public class RebootDevice extends AsyncTask<Void, Void, Void> {

    private static final boolean DEBUG = true;
    private static final String TAG = "RebootDevice";

    @Override
    protected Void doInBackground(Void... params) {

        try {
            String command;
            command = "reboot";
            if (DEBUG) Log.d(TAG, "Reboot command started");
            Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", command });
            proc.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

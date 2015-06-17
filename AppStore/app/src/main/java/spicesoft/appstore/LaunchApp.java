package spicesoft.appstore;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Async task that start an activity using adb commands.
 * SuperSU have to grant access first.
 * Created by Vincent on 01/06/15.
 */
public class LaunchApp extends AsyncTask<MainActivity.LaunchParam, Void, Void> {

    private static final String TAG ="AppLauncher";
    private static final boolean DEBUG = true;

    /**
     *
     * @param params is a class containing the Activity, the package name and the activity name
     *               of the activity to start.
     *               The previous activity is killed, when the new one is started.
     * @return
     */
    @Override
    protected Void doInBackground(MainActivity.LaunchParam... params) {

        Activity a = params[0].a;
        String packageName = params[0].pkgName;
        String activityName = params[0].actName;

        try {
            String command;
            command = "am start -n " + packageName + "/" + packageName + "." + activityName;
            Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", command });
            proc.waitFor();
            if (DEBUG) Log.d(TAG, "App launch process: done");
            a.finish(); //Kill the activity

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

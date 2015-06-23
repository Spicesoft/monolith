package spicesoft.appstore.AsyncTasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import spicesoft.appstore.MainActivity;
import spicesoft.appstore.Model.App;

/**
 * Async task that start an activity using adb commands.
 * SuperSU have to grant access first.
 * Created by Vincent on 01/06/15.
 */
public class LaunchApp extends AsyncTask<App, Void, Void> {

    private static final String TAG ="AppLauncher";
    private static final boolean DEBUG = true;
    public Activity activity;

    public LaunchApp (Activity a){activity = a;}

    /**
     *
     * @param params is a class containing the Activity, the package name and the activity name
     *               of the activity to start.
     *               The previous activity is killed, when the new one is started.
     * @return
     */
    @Override
    protected Void doInBackground(App... params) {

        String packageName = params[0].pkgName;
        String activityName = params[0].activityName;

        try {
            String command;
            command = "am start -n " + packageName + "/" + packageName + "." + activityName;
            Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", command });
            proc.waitFor();
            if (DEBUG) Log.d(TAG, "App launch process: done");
            activity.finish(); //Kill the activity

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

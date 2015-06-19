package spicesoft.appstore.AsyncTasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import spicesoft.appstore.MainActivity;
import spicesoft.appstore.R;

/**
 * Created by Vincent on 19/06/15.
 */

public class SetDeviceOwner extends AsyncTask<Void, Void, Void>{

    private static final boolean DEBUG = true;
    private static final String TAG = "SetDeviceOwner";

    private static String deviceOwnerPath = "/data/system/";
    private static String getDeviceOwnerFileName = "device_owner.xml";
    private boolean error = false;
    private String errorCode = "";
    private Activity activity;


    public SetDeviceOwner(Activity a){
        activity = a;
    }

    @Override
    protected Void doInBackground(Void... params) {

        String command;
        command = "echo '" +
                "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\" ?>\n" +
                "<device-owner package=\"spicesoft.appstore\" name=\"Task Pinning\" />" + "'"
                + " > " + deviceOwnerPath + getDeviceOwnerFileName;
        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec(new String[] { "su", "-c", command });
            proc.waitFor();

        } catch (IOException e) {
            e.printStackTrace();
            error = true;
            errorCode = "Unable to write device_owner.xml";
            if(DEBUG) Log.d(TAG, "AsyncError");

        } catch (InterruptedException e) {
            e.printStackTrace();
            error = true;
            errorCode = "Device owner process interrupted";
            if(DEBUG) Log.d(TAG, "AsyncError");
        }


        command = "chown system:system " + deviceOwnerPath + getDeviceOwnerFileName;
        try {
            proc = Runtime.getRuntime().exec(new String[] { "su", "-c", command });
            proc.waitFor();

        } catch (IOException e) {
            e.printStackTrace();
            error = true;
            errorCode = "Unable to set device_owner.xml permission";
            if(DEBUG) Log.d(TAG, "AsyncError");

        } catch (InterruptedException e) {
            e.printStackTrace();
            error = true;
            errorCode = "Device owner chown process interrupted";
            if(DEBUG) Log.d(TAG, "AsyncError");

        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if(error){
            if(DEBUG) Log.d(TAG, "AsyncError");
        }else{
            SharedPreferences settings;
            settings = activity.getSharedPreferences(MainActivity.PREFS_NAME, activity.MODE_PRIVATE);
            settings.edit().putBoolean("config_done", true).commit();

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(R.string.reboot_message)

                    .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            new RebootDevice().execute();
                        }
                    });

            Dialog d = builder.create();
            d.setCanceledOnTouchOutside(false);
            d.show();

        }
    }
}

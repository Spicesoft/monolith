package spicesoft.appstore.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import spicesoft.appstore.MainActivity;

/**
 * Handle the RECEIVE_BOOT_COMPLETED intent and start the MainActivity after Android is done booting
 * Created by Vincent on 19/05/15.
 */

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        /**
         * This method receives the boot intent and launches the MainActivity
         */

        Intent i = new Intent(context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(i);

    }
}
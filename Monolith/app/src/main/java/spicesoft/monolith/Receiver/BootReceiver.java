package spicesoft.monolith.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import spicesoft.monolith.MainActivity;

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
        Intent mIntent = new Intent(context, MainActivity.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mIntent);
    }
}
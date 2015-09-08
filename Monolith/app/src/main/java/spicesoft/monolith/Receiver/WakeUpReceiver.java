package spicesoft.monolith.Receiver;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import spicesoft.monolith.KisokMode.WakeLockInstance;


/**
 * Created by Vincent on 01/06/15.
 */
public class WakeUpReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("WakeUpReceiver", "WakeUpAlarm received");

        try {
            WakeLockInstance.getInstance().getWl().acquire();
            if(WakeLockInstance.getInstance().getPwl().isHeld())
            WakeLockInstance.getInstance().getPwl().release();
            Log.d("WakeUpReceiver", "WakeLock acquired !");
        }
        catch(Exception e)
        {
            Log.d("WakeUpReceiver", "Cannot acquire wakeLock" + e.getMessage());
        }

    }
}

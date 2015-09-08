package spicesoft.monolith.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;
import spicesoft.monolith.KisokMode.KioskModeActivity;
import spicesoft.monolith.KisokMode.WakeLockInstance;


/**
 * Created by Vincent on 01/06/15.
 */
public class HibernateAlarmReceiver extends BroadcastReceiver
{
    private static final boolean DEBUG = true;

    @Override
    public void onReceive(Context context, Intent intent) {

        if(DEBUG) Toast.makeText(context, "Hibernate Alarm received", Toast.LENGTH_LONG).show();

        PowerManager.WakeLock wl = WakeLockInstance.getInstance().getWl();

        try{
            if(WakeLockInstance.getInstance().getWl().isHeld())
            wl.release();

            WakeLockInstance.getInstance().getPwl().acquire();

            if(DEBUG) Log.d("HibernateReceiver", "Wakelock released !");
        }
        catch (Exception e)
        {
            if(DEBUG) Log.d("HibernateAlarmReceiver", "WakeLock cannot be released \n" + e.getMessage());
        }
    }
}

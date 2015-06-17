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
    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "Hibernate Alarm received", Toast.LENGTH_LONG).show();

        KioskModeActivity.lockPowerButton(false);
        PowerManager.WakeLock wl = WakeLockInstance.getInstance().getWl();

        try{
            if(WakeLockInstance.getInstance().getWl().isHeld())
            wl.release();

            WakeLockInstance.getInstance().getPwl().acquire();

            Log.d("HibernateReceiver", "Wakelock released !");
        }
        catch (Exception e)
        {
            Log.d("HibernateAlarmReceiver", "WakeLock cannot be released \n" + e.getMessage());
        }
    }
}

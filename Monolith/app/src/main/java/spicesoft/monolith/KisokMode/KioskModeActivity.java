package spicesoft.monolith.KisokMode;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.WindowManager;


/**
 * Class defining methods to create a kiosk mode app (single application mode).
 * Created by Vincent on 20/05/15.
 */
public class KioskModeActivity extends Activity {

    public static final boolean DEBUG = true;
    public static final String TAG = "KioskModeActiviy";

    public static boolean enabled = false;

    protected static  String PREFS_NAME = "spicesoft.appstore";

    public PowerManager powerManager=null;
    public PowerManager.WakeLock wakeLock = null;



    /**
     * Set default kiosk device properties:
     *  - lock physical buttons (volume up/down, power)
     *  - disable the unlock screen (keyguard)
     *  - set the orientation to landscape according to the sensor
     *  - disable sound
     */
    public  void enableFullKioskMode(){

        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        /*
        PowerManager.WakeLock fullWakeLock = powerManager.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "Loneworker - FULL WAKE LOCK");
        PowerManager.WakeLock partialWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Loneworker - PARTIAL WAKE LOCK");
        WakeLockInstance.getInstance().setWl(fullWakeLock);
        WakeLockInstance.getInstance().setPwl(partialWakeLock);
        */
        //WakeLockInstance.getInstance().getWl().acquire();
        disableKeyguard();
        setOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
    }


    /**
     * Disable the unlock screen (keyguard)
     */
    public void disableKeyguard(){
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD); // Disable the lock screen
    }


    /**
     * Sets the orientation of the screen
     * @param requestedOrientation
     */
    public void setOrientation(int requestedOrientation ){

        this.setRequestedOrientation(requestedOrientation);
    }


    @Override
    protected void onPause() {
        super.onPause();
/*
        if (WakeLockInstance.getInstance().getWl().isHeld()){
            WakeLockInstance.getInstance().getWl().release();
            WakeLockInstance.getInstance().getPwl().acquire();
        }
*/

            //powerManager  = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            //PowerManager.WakeLock wakeLock = powerManager.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
            //wakeLock.acquire();

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

    }


    /**
     * This method is called when the BACK button is pressed but it does nothing
     * in order to disable this button.
     */
    @Override
    public void onBackPressed() {
        //Do nothing
    }

}
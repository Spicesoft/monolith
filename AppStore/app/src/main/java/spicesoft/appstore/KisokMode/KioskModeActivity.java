package spicesoft.appstore.KisokMode;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import spicesoft.appstore.Receiver.BasicDeviceAdminReceiver;


/**
 * Class defining methods to create a kiosk mode app (single application mode).
 * Created by Vincent on 20/05/15.
 */
public class KioskModeActivity extends Activity {

    public static final boolean DEBUG = true;
    public static final String TAG = "KioskModeActiviy";

    public static boolean enabled = false;


    private static boolean bLockPowerButton = false;
    private static boolean bEnableScreenPinning = false;
    private static boolean bLockVolumeButtons =false;

    public PowerManager powerManager=null;
    public PowerManager.WakeLock wakeLock = null;

    //List of buttons to disable: Volume UP/DOWN
    private static final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));


    /**
     * Set default kiosk device properties:
     *  - lock physical buttons (volume up/down, power)
     *  - disable the unlock screen (keyguard)
     *  - set the orientation to landscape according to the sensor
     *  - disable sound
     */
    public  void enableFullKioskMode(){

        lockPowerButton(false);
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock fullWakeLock = powerManager.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "Loneworker - FULL WAKE LOCK");
        PowerManager.WakeLock partialWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Loneworker - PARTIAL WAKE LOCK");
        WakeLockInstance.getInstance().setWl(fullWakeLock);
        WakeLockInstance.getInstance().setPwl(partialWakeLock);
        WakeLockInstance.getInstance().getWl().acquire();

        lockVolumeButtons(false);
        disableKeyguard();
        //disableSound();
        setOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        enabled = true;
    }


    public  void disableFullKioskMode(){
        lockVolumeButtons(false);
        lockPowerButton(false);
        setOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        enabled = false;
    }

    /**
     * Sets the locking status of the power button
     * @param b
     */
    public static void lockPowerButton(boolean b){
        bLockPowerButton=b;
    }


    /**
     * Sets the locking status of the volume buttons
     * @param b
     */
    public void lockVolumeButtons(boolean b){
        bLockVolumeButtons=b;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public  void enableScreenPinning(Activity activity, boolean b){
        if(b) activity.startLockTask(); else activity.stopLockTask();
    }


    /**
     * Disable the unlock screen (keyguard)
     */
    public void disableKeyguard(){
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD); // Disable the lock screen
        KeyguardManager keyguardManager = (KeyguardManager)getSystemService(Activity.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
        lock.disableKeyguard();
    }


    /**
     * Sets the orientation of the screen
     * @param requestedOrientation
     */
    public void setOrientation(int requestedOrientation ){

        this.setRequestedOrientation(requestedOrientation);
    }

    public void disableSound() {
        AudioManager aManager=(AudioManager)getSystemService(AUDIO_SERVICE);
        aManager.setRingerMode(aManager.RINGER_MODE_SILENT);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (WakeLockInstance.getInstance().getWl().isHeld()){
            WakeLockInstance.getInstance().getWl().release();
            WakeLockInstance.getInstance().getPwl().acquire();
        }

        if(bLockPowerButton) {
            if (WakeLockInstance.getInstance().getWl().isHeld()) {
                WakeLockInstance.getInstance().getWl().release();
                WakeLockInstance.getInstance().getWl().acquire();
            }
            else {
                WakeLockInstance.getInstance().getWl().acquire();
            }
        }
    }


    @Override
    protected void onPostCreate(Bundle bundle) {
        super.onPostCreate(bundle);

        try {
            getActionBar().hide();
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);


        provisionOwner();
    }

    /**
     * This method is called when the BACK button is pressed but it does nothing
     * in order to disable this button.
     */
    @Override
    public void onBackPressed() {

        //Do nothing
        //Activate the back button to unpin the screen for debug purposes only
        try {
            //stopLockTask();
        }
        catch (NullPointerException e)
        {
            Log.d(TAG, e.getMessage());
        }
    }

    public void setWorkingHours(Date start, Date stop){

        Date now = Calendar.getInstance().getTime();


    }

    /**
     * This method is overridden in order to handle the volume buttons locking
     * @param event
     * @return
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (blockedKeys.contains(event.getKeyCode())) {
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }


    /**
     * This method is used in order to define the app as administrator device
     * Should be used only if the screenPinning is used => Android 5.0 + (API 22 +)
     */
    protected void provisionOwner() {
        DevicePolicyManager manager =
                (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName componentName = BasicDeviceAdminReceiver.getComponentName(this);

        if(!manager.isAdminActive(componentName)) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
            startActivityForResult(intent, 0);
            return;
        }

        if (manager.isDeviceOwnerApp(getPackageName())) {
            manager.setLockTaskPackages(componentName, new String[]{getPackageName()});
            startLockTask();
        }
    }


    /**
     * This method is overridden in order to disable long press on the power button (shutdown).
     * When the KisokModeActivity loses focus (when the shutdown dialog appears), it automatically get the focus back.
     * When this activity is openned, the device cannot be shutted down with a long press on the power button.
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);
        if(bLockPowerButton) {
            if (!hasFocus) {
                // Close every kind of system dialog
                Log.d(TAG, "Power button long push");
                Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                sendBroadcast(closeDialog);
            }
        }
    }
}
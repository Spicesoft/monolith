package spicesoft.monolith.KisokMode;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import spicesoft.monolith.Receiver.BasicDeviceAdminReceiver;

/**
 * Created by Vincent on 20/05/15.
 */
public class KioskModeActivity extends Activity{

    private final String TAG="KioskModeActiviy";

    public static boolean enabled = false;

    private static boolean bLockPowerButton = false;
    private static boolean bEnableScreenPinning = false;
    private static boolean bLockVolumeButtons =false;

    //List of buttons to disable: Volume UP/DOWN
    private static final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));


    public  void enableFullKioskMode(){
        lockPowerButton(true);
        lockVolumeButtons(true);
        disableKeyguard();
        disableSound();
        setOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        enabled = true;
    }

    public  void disableFullKioskMode(){

        lockVolumeButtons(false);
        lockPowerButton(false);
        setOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        enabled = false;
    }

    public void lockPowerButton(boolean b){
        bLockPowerButton=b;
    }

    public void lockVolumeButtons(boolean b){
        bLockVolumeButtons=b;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public  void enableScreenPinning(Activity activity, boolean b){

        if(b) activity.startLockTask(); else activity.stopLockTask();
    }


    public void disableKeyguard(){
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD); // Disable the lock screen
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    }


    public void setOrientation(int requestedOrientation ){

        this.setRequestedOrientation(requestedOrientation);
    }

    public void disableSound(){
        AudioManager aManager=(AudioManager)getSystemService(AUDIO_SERVICE);
        aManager.setRingerMode(aManager.RINGER_MODE_SILENT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(bLockPowerButton) {
            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
            wakeLock.acquire();
        }
    }


    @Override
    protected void onPostCreate(Bundle bundle) {
        super.onPostCreate(bundle);


        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        //provisionOwner();
    }

    @Override
    public void onBackPressed() {
        /**
         * This method is called when the BACK button is pressed but it does nothing
         * in order to disable this button.
         */
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

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (blockedKeys.contains(event.getKeyCode())) {
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void provisionOwner() {
        DevicePolicyManager manager =
                (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName componentName = BasicDeviceAdminReceiver.getComponentName(this);


        if(!manager.isAdminActive(componentName)) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
            startActivityForResult(intent, 0);
            return;
        }

        if (manager.isDeviceOwnerApp(getPackageName()))
            manager.setLockTaskPackages(componentName, new String [] {getPackageName()});
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        /**
         * This method is called when this activity loses focus
         * If it loses focus, the system dialog is closed. This way, the device
         * cannot be shutted down by a long press on the power button
         * @param hasFocus
         */
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
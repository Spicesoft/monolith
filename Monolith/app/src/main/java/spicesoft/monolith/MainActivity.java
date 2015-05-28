package spicesoft.monolith;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.WebView;

import spicesoft.monolith.KisokMode.KioskModeNfcActivity;
import spicesoft.monolith.utils.AndroidUtils;
import spicesoft.monolith.utils.WebViewTool;
import spicesoft.tandoori.R;


/**
 * Main activity
 *
 * @author Vincent Dudek
 */
public class MainActivity extends KioskModeNfcActivity {

    private boolean DEBUG = true;
    public static final String TAG = "MainActivity";
    public static final String UPDATER_SERVICE = "spicesoft.autoupdater";
    public static final long DISCONNECT_TIMEOUT = 10000; // 10sec
    public static final String PREFS_NAME = "spicesoft.monolith";

    public SharedPreferences settings;

    private Activity activity;

    private JsHandler mJsHandler;

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;

        enableFullKioskMode();
        AndroidUtils.launchWifiSetup(this);

        //onFirstRun();

        mWebView = (WebView) findViewById(R.id.web_content);
        mJsHandler = new JsHandler(this, mWebView);
        WebViewTool.init(mWebView, mJsHandler);


        if(!AndroidUtils.isWifiConnected(this)){
            AndroidUtils.launchWifiSetup(this);
        }
    }


    /**
     * This Method is called when the user touches the screen.
     * It resets the DisconnectTimer in order to detect user's inactivity.
     */
    @Override
    public void onUserInteraction(){
        resetDisconnectTimer();
    }


    @Override
    public void onResume() {
        super.onResume();

        if (!AndroidUtils.isWifiConnected(this)){
            AndroidUtils.launchWifiSetup(this);
        }
    }


    /**
     * This method is executed on the very first run of the app.
     * It will be used to set the tablet configurtion (e.g. center openning/closing hours, meeting room ID...)
     */
    public void onFirstRun(){
        settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        setOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);


        if (settings.getBoolean("first_run", true)) {
            //the app is being launched for first time, do something
            if(DEBUG)Log.d(TAG, "First run");


            AndroidUtils.launchWifiSetup(this);

            settings.edit().putBoolean("first_run", false).commit();
        }else{
            if(DEBUG)Log.d(TAG, "Not first run");
        }
    }


    private static Handler disconnectHandler = new Handler(){
        public void handleMessage(Message msg) {
        }
    };

    private Runnable disconnectCallback = new Runnable() {
        @Override
        public void run() {
            // Perform any required operation on disconnect
            if(DEBUG)Log.d("INACTIVITY HANDLER", "User activity timeout");

            if (!AndroidUtils.isWifiConnected(activity)){
                AndroidUtils.launchWifiSetup(activity);
            }

            if(DEBUG)  Log.d(TAG, "Refreshing WebView");
        }
    };

    public void resetDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }

    public void stopDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
    }

}
package spicesoft.monolith;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import spicesoft.monolith.KisokMode.KioskModeNfcActivity;
import spicesoft.monolith.KisokMode.WakeLockInstance;
import spicesoft.monolith.NFC.HexDump;
import spicesoft.monolith.NFC.NfcResponse;
import spicesoft.monolith.Receiver.HibernateAlarmReceiver;
import spicesoft.monolith.Receiver.UpdateAlarmReceiver;
import spicesoft.monolith.Receiver.WakeUpReceiver;
import spicesoft.monolith.utils.WebViewTool;


/**
 * Main activity
 * This activity is started right after Android's done booting.
 * It displays a wifiSetup activity if the tablet isn't connected to a wifi network.
 * Otherwise it display a fullscreen WebView
 * @author Vincent Dudek
 */
public class MainActivity extends KioskModeNfcActivity implements NfcResponse{

    public static boolean DEBUG = true;
    public static final String TAG = "MainActivity";
    public static final String DefaultURL = "http://14cb3f28.ngrok.com";

    public static final String UPDATER_SERVICE = "spicesoft.autoupdater";
    public static final long DISCONNECT_TIMEOUT = 10000; // 10 * 1000ms before user_inactivity timeout
    public static final String PREFS_NAME = "spicesoft.monolith";

    public static final int UPDATE_HOUR = 11;
    public static final int UPDATE_MINUTE = 45;
    public static final int MAX_JITTER = 5; //Spread the update on N minutes


    public SharedPreferences settings;

    private Activity activity;

    private JsHandler mJsHandler;

    private WebView mWebView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;
        this.delegate = this;

        enableFullKioskMode();

        mWebView = (WebView) findViewById(R.id.web_content);
        mJsHandler = new JsHandler(this, mWebView);
        WebViewTool.init(mWebView, mJsHandler);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if(!(netInfo != null && netInfo.isConnectedOrConnecting())) {
            Intent intent = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK);
            intent.putExtra("extra_prefs_show_button_bar", true);
            intent.putExtra("wifi_enable_next_on_connect", true);
            startActivityForResult(intent, 1);
        }



        try {
            Uri data = getIntent().getData();
            String scheme = data.getScheme(); // "cowork.app"
            String host = data.getHost(); // "monolith"
            List<String> params = data.getPathSegments();
            String base64JSON = params.get(0); // Base64 encoded JSON
            Log.d(TAG, "PARAMS => " + base64JSON);
            byte[] decodedJSON = Base64.decode(base64JSON, Base64.DEFAULT);
            String JSON = new String(decodedJSON, "UTF-8");
            Log.d(TAG, "DECODED => " + JSON );
            JSONObject jObject = new JSONObject(JSON);
            String URL = jObject.getString("URL"); //get JSON value for "URL" key
            mWebView.loadUrl(URL);
        }
        catch (Exception e){
            mWebView.loadUrl("http://www.google.com");
            e.printStackTrace();
        }


        /*
        Get the working hours of the center for the day, then call setWorkingHours methods from KioskModeActivity class.
         */
        //getWorkingHoursFromJs();
        //setWorkingHours();
        //setUpdateAlarm();

        Calendar WakeUpTime = Calendar.getInstance();
        WakeUpTime.setTimeZone(TimeZone.getDefault());
        //WakeUpTime.set(Calendar.HOUR_OF_DAY, 15);
        //WakeUpTime.set(Calendar.MINUTE, UPDATE_MINUTE);

        setWakeUpAlarm(WakeUpTime);

        Calendar HibernateTime = Calendar.getInstance();
        HibernateTime.setTimeZone(TimeZone.getDefault());
        //HibernateTime.set(Calendar.HOUR_OF_DAY, 15);
        //HibernateTime.set(Calendar.MINUTE, 21);

        setHibernateAlarm(HibernateTime);


        Calendar updateTime = Calendar.getInstance();
        updateTime.setTimeZone(TimeZone.getDefault());
        updateTime.set(Calendar.HOUR_OF_DAY, UPDATE_HOUR);
        updateTime.set(Calendar.MINUTE, UPDATE_MINUTE);

        int jitterDelay = new Random().nextInt(MAX_JITTER); // Generate a random delay in [ 0 ; MAX_JITTER ]

        //setUpdateAlarm(updateTime, jitterDelay);

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
/*
        if (!AndroidUtils.isWifiConnected(this)){
            AndroidUtils.launchWifiSetup(this);
        }
        */

        //mWebView.loadUrl(DefaultURL);
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


            //AndroidUtils.launchWifiSetup(this);

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

            /*
            if (!AndroidUtils.isWifiConnected(activity)){
                AndroidUtils.launchWifiSetup(activity);
            }
            */

        }
    };

    public void resetDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }


    public void stopDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
    }


    /**
     * This methods sets an alarmManager in order to trigger the update process (AutoUpdater.apk)
     * at a specific time. A jitter can be added in order to spread the update triggering
     * on a given range of time.
     * @param UpdateTime Calendar object defining the specific hour and minutes for the update.
     * @param jitter is the number of minutes (int) used to delay the update process.
     */
    public void setUpdateAlarm(Calendar UpdateTime, int jitter){
        //TODO : Add random jitter to the update time

        long jitterMillis = jitter * 60 * 1000;

        Toast.makeText(this, "Update alarm time set to : " + UpdateTime.getTime().toString(), Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, UpdateAlarmReceiver.class);

        PendingIntent pIntent = PendingIntent.getBroadcast(this,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get the AlarmManager service
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, UpdateTime.getTimeInMillis() + jitterMillis + 120 * 1000, pIntent);
    }


    /**
     * This method sets an alarm in that will wake the device at a specific time.
     * @param WakeUpTime
     */
    public void setWakeUpAlarm(Calendar WakeUpTime){

        Intent intent = new Intent(this, WakeUpReceiver.class);

        PendingIntent pIntent = PendingIntent.getBroadcast(this,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, WakeUpTime.getTimeInMillis() + 60 * 1000, pIntent );
    }


    /**
     * This method sets an alarm in that will the device in sleep mode at a specific time.
     * @param HibernateTime
     */
    public void setHibernateAlarm(Calendar HibernateTime){

        Intent intent = new Intent(this, HibernateAlarmReceiver.class);

        PendingIntent pIntent = PendingIntent.getBroadcast(this,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, HibernateTime.getTimeInMillis() + 25 * 1000, pIntent);

    }

    @Override
    public void NfcIntentReceived(Intent intent) {

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        /*
        if(HexDump.dumpHexString(tag.getId()).equals("1A044481"))
        mWebView.loadUrl("http://14cb3f28.ngrok.com/webapps/concierge/");

        if(HexDump.dumpHexString(tag.getId()).equals("9A0BA447")) {
        */

            Intent sIntent = new Intent("android.intent.action.MAIN");
            sIntent.setComponent(ComponentName.unflattenFromString("spicesoft.appstore" + "/" + "spicesoft.appstore" + "." + "MainActivity"));
        sIntent.addCategory("android.intent.category.LAUNCHER");
        sIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(sIntent);

       // }

        Toast.makeText(this, "NFC TAG discovered !", Toast.LENGTH_LONG).show();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case 1:
                mWebView.reload();
                Toast.makeText(this, "WebView reloaded after Wifi config", Toast.LENGTH_LONG).show();
                break;

            default:
                break;
        }
    }
}
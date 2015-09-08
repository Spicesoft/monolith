package spicesoft.monolith;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Instrumentation;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.widget.Toast;

import java.net.URLDecoder;
import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

import spicesoft.monolith.KisokMode.KioskModeNfcActivity;
import spicesoft.monolith.NFC.HexDump;
import spicesoft.monolith.NFC.NfcResponse;
import spicesoft.monolith.Receiver.HibernateAlarmReceiver;
import spicesoft.monolith.Receiver.UpdateAlarmReceiver;
import spicesoft.monolith.Receiver.WakeUpReceiver;
import spicesoft.monolith.utils.WebViewTool;


/**
 * This activity is started right after Android's done booting.
 * It displays a wifiSetup activity if the tablet isn't connected to a wifi network.
 * Otherwise it display a fullscreen WebView
 * @author Vincent Dudek
 */
public class MainActivity extends KioskModeNfcActivity implements NfcResponse{

    public static boolean DEBUG = true;
    public static final String TAG = "MainActivity";

    public static final String UPDATER_SERVICE = "spicesoft.appstore";

    public static final long DISCONNECT_TIMEOUT = 10000; // 10 * 1000ms before user_inactivity timeout

    public static final int UPDATE_HOUR = 0;
    public static final int UPDATE_MINUTE = 30;
    public static final int MAX_JITTER = 10; //Spread the update on N minutes

    private static final String ERROR_URL = "file:///android_asset/error.html";
    private static final String TEST_URL = "http://spicesoft.cowork.io:8000/login/?next=/webapps/meeting_room/";

    private Activity activity;

    private JsHandler mJsHandler;

    private WebView mWebView;

    private String nfcStatus = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;
        this.delegate = this;

        enableFullKioskMode();

        mWebView = (WebView) findViewById(R.id.web_content);
        mJsHandler = new JsHandler(this, mWebView);
        new WebViewTool(this).init(mWebView, mJsHandler);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if(!(netInfo != null && netInfo.isConnectedOrConnecting())) {
            //Launch wifi setup wizard
            Intent intent = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK);
            intent.putExtra("extra_prefs_show_button_bar", true);
            intent.putExtra("wifi_enable_next_on_connect", true);
            startActivityForResult(intent, 1);
        }

        try {
            Uri data = getIntent().getData();
            if(DEBUG) Log.d(TAG, "URI data = " + data.toString());
            String scheme = data.getScheme(); // "cowork.app"
            String host = data.getHost(); // "monolith"
            if(DEBUG) Log.d(TAG, "Host = " + host);

            String query = data.getQuery();

            UrlQuerySanitizer sanitizer = new UrlQuerySanitizer();
            sanitizer.setAllowUnregisteredParamaters(true);
            sanitizer.parseUrl(getIntent().getData().toString());
            if(DEBUG) Log.d(TAG, "query = " + query);
            String URL = sanitizer.getValue("url");
            if(DEBUG) Log.d(TAG, "URL = " + URL);

            String decodedUrl = URLDecoder.decode(URL, "UTF-8");
            mWebView.loadUrl(decodedUrl);

            Context mContext = getApplicationContext().createPackageContext(
                    "spicesoft.appstore",
                    Context.MODE_PRIVATE);
            SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, MODE_WORLD_WRITEABLE);

            if(DEBUG) Log.d(TAG, "default_app(monolith) : " + settings.getString("default_app", ""));

            settings.edit().putString("default_app", data.toString()).apply();

            if(DEBUG) Log.d(TAG, "default_app(monolith) : " + settings.getString("default_app", ""));

        }
        catch (Exception e){ // error while parsing Intent's parameters
            mWebView.loadUrl(ERROR_URL);
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

        //setWakeUpAlarm(WakeUpTime);

        Calendar HibernateTime = Calendar.getInstance();
        HibernateTime.setTimeZone(TimeZone.getDefault());
        //HibernateTime.set(Calendar.HOUR_OF_DAY, 15);
        //HibernateTime.set(Calendar.MINUTE, 21);

        //setHibernateAlarm(HibernateTime);

        Calendar updateTime = Calendar.getInstance();
        updateTime.setTimeZone(TimeZone.getDefault());
        updateTime.set(Calendar.HOUR_OF_DAY, UPDATE_HOUR);
        updateTime.set(Calendar.MINUTE, UPDATE_MINUTE);

        int jitterDelay = new Random().nextInt(MAX_JITTER); // Generate a random delay in [ 0 ; MAX_JITTER ]

        setUpdateAlarm(updateTime, jitterDelay);

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

        if(DEBUG) Toast.makeText(this, "NFC TAG discovered ! " + HexDump.dumpHexString(tag.getId()), Toast.LENGTH_SHORT).show();

        //Calling JS method
        //params: TagID
        //mJsHandler.nfcAuthenticate(HexDump.dumpHexString(tag.getId()));

        final String tagID = HexDump.dumpHexString(tag.getId());


        final UrlQuerySanitizer sanitizer = new UrlQuerySanitizer();
        sanitizer.setAllowUnregisteredParamaters(true);
        sanitizer.parseUrl(mWebView.getUrl());

        if(DEBUG) Log.d(TAG, "url = " + mWebView.getUrl());

        if (mWebView.getUrl().contains("nfc=true")) {
            if (sanitizer.getValue("nfc") != null) nfcStatus = sanitizer.getValue("nfc");

            if (nfcStatus.equals("true")) {  //type tagID + tab + tagID + enter
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(DEBUG)Log.d("Sanitizer nfc = ", nfcStatus);
                        nfcStatus = "true";
                        Instrumentation inst = new Instrumentation();
                        for (int i = 0; i < tagID.length(); i++) {
                            inst.sendKeyDownUpSync(KeyEvent.keyCodeFromString("KEYCODE_" + tagID.charAt(i)));
                        }
                    }

                }).start();

            }
        } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        nfcStatus = "auth";
                        Instrumentation inst = new Instrumentation();
                        for (int i = 0; i < tagID.length(); i++) {
                            inst.sendKeyDownUpSync(KeyEvent.keyCodeFromString("KEYCODE_" + tagID.charAt(i)));
                        }
                        inst.sendKeyDownUpSync(KeyEvent.KEYCODE_TAB);

                        for (int i = 0; i < tagID.length(); i++) {
                            inst.sendKeyDownUpSync(KeyEvent.keyCodeFromString("KEYCODE_" + tagID.charAt(i)));
                        }
                        inst.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
                    }

                }).start();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                mWebView.reload();
                if(DEBUG) Toast.makeText(this, "WebView reloaded after Wifi config", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

    /**
     * For debugging purpose only, should be empty.
     */
    @Override
    public void onBackPressed() {
        mWebView.reload();
    }

}
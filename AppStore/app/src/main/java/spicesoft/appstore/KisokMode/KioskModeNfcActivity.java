package spicesoft.appstore.KisokMode;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import spicesoft.appstore.NFC.HexDump;
import spicesoft.appstore.NFC.NFCTool;
import spicesoft.appstore.NFC.NfcResponse;

/**
 * Created by Vincent on 28/05/15.
 */
public class KioskModeNfcActivity extends KioskModeActivity {

    private boolean DEBUG = true;
    public static final String TAG = "KioskModeNfcActivity";


    protected NfcAdapter mNfcAdapter;

    public NfcResponse delegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        NFCTool.checkNfcState(mNfcAdapter, this);

        if(getIntent() != null) handleIntent(getIntent());
    }

    @Override
    public void onPause() {
        super.onPause();
        mNfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        mNfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        /**
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called. For more information have a look
         * at the documentation.
         *
         * In our case this method gets called, when the user attaches a Tag to the device.
         */
        handleIntent(intent);
    }


    protected void handleIntent(Intent intent) {
        /**
         * This method gets called when a tag is discovered. Reads the ID of the new Tag.
         * @param intent
         */

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        try {

            /*
                    NFC Tag Discovered !
             */
            delegate.NfcIntentReceived(intent);

            if (DEBUG) Log.d(TAG, "Size : " + tag.getId().length +
                    "Tag ID : " + HexDump.dumpHexString(tag.getId()) +
                    "\n");

            if (HexDump.dumpHexString(tag.getId()).equals("9A0BA447")) {
                Toast.makeText(this, "Vincent", Toast.LENGTH_SHORT).show();
            }else if(HexDump.dumpHexString(tag.getId()).equals("1A044481")){

               // Toast.makeText(this, "Loading = " + "http://192.168.1.206:8000/webapps/concierge/", Toast.LENGTH_LONG ).show();

               /* Intent intentw = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK);
                intentw.putExtra("extra_prefs_show_button_bar", true);
                intentw.putExtra("wifi_enable_next_on_connect", true);
                startActivityForResult(intent, 1);
                */

            }
            else {
                Toast.makeText(this, "Mifare demo card", Toast.LENGTH_SHORT).show();
            }
        } catch (NullPointerException e) {
            Log.d(TAG, "Unable to read the Tag ID : NULL pointer exception");
        }
    }
}
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

            //Call delegate for NFC intent
            delegate.NfcIntentReceived(intent);

            if (DEBUG) Log.d(TAG, "Size : " + tag.getId().length +
                    "Tag ID : " + HexDump.dumpHexString(tag.getId()) +
                    "\n");

        } catch (NullPointerException e) {
            Log.d(TAG, "Unable to read the Tag ID : NULL pointer exception");
        }
    }
}
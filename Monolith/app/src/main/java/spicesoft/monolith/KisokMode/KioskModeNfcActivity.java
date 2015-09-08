package spicesoft.monolith.KisokMode;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import spicesoft.monolith.NFC.HexDump;
import spicesoft.monolith.NFC.NFCTool;
import spicesoft.monolith.NFC.NfcResponse;

/**
 * Created by Vincent on 28/05/15.
 */
public class KioskModeNfcActivity extends KioskModeActivity {

    private static final String ADMIN_CARD_ID = "1A044481";
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
            if(HexDump.dumpHexString(tag.getId()).equals(ADMIN_CARD_ID)){ //If the 'admin' card is detected
                Intent sintent = new Intent();
                sintent.setClassName("spicesoft.appstore", "spicesoft.appstore.MainActivity");
                sintent.putExtra("default_app", "clear");
                startActivity(sintent);
                finish();
            }

            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
                NdefMessage[] messages = null;
                Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                if (rawMsgs != null) {
                    messages = new NdefMessage[rawMsgs.length];
                    for (int i = 0; i < rawMsgs.length; i++) {
                        messages[i] = (NdefMessage) rawMsgs[i];
                    }
                }
                assert messages != null;
                if(messages[0] != null) {
                    String result="";
                    byte[] payload = messages[0].getRecords()[0].getPayload();
                    // this assumes that we get back an SOH followed by host/code
                    for (int b = 1; b<payload.length; b++) { // skip SOH
                        result += (char) payload[b];
                    }
                    if(DEBUG) Log.d(TAG, "Tag content = " + result
                    );
                }
            }

            delegate.NfcIntentReceived(intent);

            if (DEBUG) Log.d(TAG,
                    "Size ID : " + tag.getId().length +
                    "Tag ID : " + HexDump.dumpHexString(tag.getId()) +
                    "\n"+
                    "String = " + tag.toString());


        } catch (NullPointerException e) {
            Log.d(TAG, "Unable to read the Tag ID : NULL pointer exception");
        }
    }
}
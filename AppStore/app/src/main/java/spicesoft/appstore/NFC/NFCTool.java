package spicesoft.appstore.NFC;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.nfc.NfcAdapter;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Vincent on 27/05/15.
 */
public class NFCTool {

    private static String TAG = "NFCTool";

    public static void checkNfcState(NfcAdapter mNfcAdapter, Activity a){
        if (mNfcAdapter == null) {         //If the mNfcAdapter is null, it means NFC is not handled by the device.
            Log.d(TAG, "NFC not supported");
            Toast.makeText(a, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();

            new AlertDialog.Builder(a)
                    .setTitle("Produit incompatible")
                    .setMessage("Certaines fonctionnalités de l'application ne seront pas disponibles")
                    .setNeutralButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

            return;
        }
        if (!mNfcAdapter.isEnabled()) {
           // Toast.makeText(a, "NFC is disabled.", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(a, "NFC is enabled.", Toast.LENGTH_SHORT).show();
        }
    }
}

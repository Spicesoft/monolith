package spicesoft.appstore.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

/**
 * This class is used as a interface between Android-Java and JS
 * Created by Vincent on 27/05/15.
 */
public class JsHandler {
    Activity activity;
    String TAG = "JsHandler";
    WebView webView;


    public JsHandler(Activity _contxt, WebView _webView) {
        activity = _contxt;
        webView = _webView;
    }

    /**
     * This function handles call from JS
     */
    @JavascriptInterface
    public void jsFnCall(String jsString) {
        showDialog(jsString);
    }


    /**
     * This function handles call from Android-Java
     */
    public void javaFnCall(String jsString) {

        final String webUrl = "javascript:diplayJavaMsg('"+jsString+"')";
        // Add this to avoid android.view.windowmanager$badtokenexception unable to add window
        if(!activity.isFinishing())
            // loadurl on UI main thread
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl(webUrl);
                    Log.d(TAG, "URL Loaded");
                }
            });
    }

    /**
     * function shows Android-Native Alert Dialog
     */
    public void showDialog(String msg){

        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle("AlertDialog");
        alertDialog.setMessage(msg);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"Annuler", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

}
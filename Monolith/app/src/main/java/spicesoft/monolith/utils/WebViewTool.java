package spicesoft.monolith.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import spicesoft.monolith.JsHandler;

/**
 * Class used for WebView initialization.
 * Created by Vincent on 21/05/15.
 */
public class WebViewTool {

    private static final String TAG ="WebViewTool";

    private static String userAgent = "COWORK";
    private static String JsHandlerName = "Android"; //Class name used in JS

    private static String ERROR_URL = "file:///android_asset/error.html";

    public static Activity activity;

    public WebViewTool(Activity a){
        activity = a;
    }

    /**
     * This method initialize the WebView with the right settings.
     * @param mWebView  is an instantiated WebView
     * @param mJsHandler is the object that manages Java/JS interactions
     */
    public void init(WebView mWebView, JsHandler mJsHandler){

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        //mWebView.addJavascriptInterface(mJsHandler, JsHandlerName);
        mWebView.getSettings().setUserAgentString(userAgent);

        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onPermissionRequest(final PermissionRequest request) {
                Log.d(TAG, "onPermissionRequest");
                request.grant(request.getResources());
            }
        });

        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //Only URL that contains cowork.app scheme can be opened by a third party app
                //Other URLs are openned in the webview.
                if (url.contains("cowork.app://")) {
                    //Load new URL Don't override URL Link
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                } else {
                    // Return true to override url loading (In this case do nothing).
                    view.loadUrl(url);
                    return false;
                }
            }


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedSslError(WebView view,
                                           SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);

            }
        });


        mWebView.setWebChromeClient(new WebChromeClient() {
            public boolean onConsoleMessage(ConsoleMessage cm) {
                Log.d("Monolith", cm.message() + " -- From line "
                        + cm.lineNumber() + " of "
                        + cm.sourceId());
                return true;
            }
        });

        mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

            // load the page file that is kept in assets folder
            mWebView.loadUrl(ERROR_URL);
        }

}

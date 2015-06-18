package spicesoft.appstore.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.util.Log;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Class used for WebView initialization.
 * Created by Vincent on 21/05/15.
 */
public class WebViewTool {

    private static final String TAG ="WebViewTool";

    private static String userAgent = "TandooriAgent";
    private static String JsHandlerName = "Android"; //Class name used in JS

    private static String DefaultUrl = "file:///android_asset/index.html";

    public static Activity activity;


    /**
     * This method configure the WebView in order to work properly with the JsHandler.
     * @param mWebView  is an instantiated WebView
     * @param mJsHandler is the object that manages Java/JS interactions
     */
    public static void init(WebView mWebView, JsHandler mJsHandler){

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.addJavascriptInterface(mJsHandler, JsHandlerName);
        mWebView.getSettings().setUserAgentString(mWebView.getSettings().getUserAgentString() + " " + userAgent);

        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onPermissionRequest(final PermissionRequest request) {
                Log.d(TAG, "onPermissionRequest");
                request.grant(request.getResources());
            }
        });

        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);

        mWebView.setWebViewClient(new WebViewClient() {

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


        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

            // load the page file that is kept in assets folder
            mWebView.loadUrl(DefaultUrl);
        }


    }

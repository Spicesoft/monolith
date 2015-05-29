package spicesoft.monolith.utils;

import android.graphics.Bitmap;
import android.net.http.SslError;
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

    private static String userAgent = "TandooriAgent";
    private static String JsHandlerName = "Android"; //Class name used in JS

    private static String DefaultUrl = "file:///android_asset/index.html";


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
        mWebView.getSettings().setUserAgentString(mWebView.getSettings().getUserAgentString() + userAgent);
        mWebView.setWebChromeClient(new WebChromeClient());
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

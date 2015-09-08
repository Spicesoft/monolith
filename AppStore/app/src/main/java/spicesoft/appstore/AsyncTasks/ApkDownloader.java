package spicesoft.appstore.AsyncTasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import spicesoft.appstore.Model.App;

/**
 * Created by Vincent on 29/05/15.
 */
public class ApkDownloader extends AsyncTask<App, Void, App> {

    private static final String TAG = "ApkDownloader" ;
    public AsyncResponse delegate = null;
    public Activity activity = null;

    public ProgressDialog dialog;

    public ApkDownloader(Activity a, AsyncResponse d){
        activity = a;
        delegate = d;
    }

    @Override
    protected App doInBackground(App... params) {

        App app = params[0];

        String urlpath = app.downloadURL;
        String ApkName = app.apkName;
        String downloadDirectory = app.downloadDir;

        Log.d(TAG, "trying to download APK from : " + app.downloadURL + " To : " + app.downloadDir );
        try{
            URL url = new URL(urlpath); //File URL

            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect(); // Connection Complete


            File file = new File(downloadDirectory); // PATH = /mnt/sdcard/download/
            if (!file.exists()) {
                file.mkdirs();
            }

            File outputFile = new File(file, ApkName);
            FileOutputStream fos = new FileOutputStream(outputFile);

            InputStream is = c.getInputStream(); // Get from Server and Catch In Input Stream Object.

            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len1); // Write In FileOutputStream.
            }
            fos.close();
            is.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return app;
    }

    @Override
    protected void onPostExecute(App app) {
        super.onPostExecute(app);

        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        delegate.postApkDownloader(app);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        dialog = ProgressDialog.show(activity, "",
                "Loading. Please wait...", true);
    }

}

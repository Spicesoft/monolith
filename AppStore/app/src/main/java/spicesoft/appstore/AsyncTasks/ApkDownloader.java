package spicesoft.appstore.AsyncTasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Vincent on 29/05/15.
 */
public class ApkDownloader extends AsyncTask<String, Void, Void> {

    public AsyncResponse delegate = null;
    public Activity activity = null;

    public ProgressDialog dialog;

    public ApkDownloader(Activity a){
        activity = a;
    }

    public ApkDownloader(){}


    private static ApkDownloader instance = null;


    public static ApkDownloader getInstance(){
        if (instance == null) instance = new ApkDownloader();
        return  instance;
    }

    public void setActivity(Activity a){
        activity = a;
    }

    public Activity getActivity(){
        return activity;
    }

    public void setDelegate(AsyncResponse d){
        delegate = d;
    }


    @Override
    protected Void doInBackground(String... params) {

        String urlpath = params[0];
        String ApkName = params[1];
        String downloadDirectory = params[2];

        try{
            URL url = new URL(urlpath); //File URL

            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect(); // Connection Complete


            String PATH = Environment.getExternalStorageDirectory() + downloadDirectory;
            File file = new File(PATH); // PATH = /mnt/sdcard/download/
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

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        delegate.postDownloadUpdate();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = ProgressDialog.show(activity, "",
                "Loading. Please wait...", true);
    }

}

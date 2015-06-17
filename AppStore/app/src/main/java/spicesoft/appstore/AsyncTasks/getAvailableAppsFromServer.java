package spicesoft.appstore.AsyncTasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent on 03/06/15.
 */
public class getAvailableAppsFromServer extends AsyncTask<String, Void, List<String>> {

    private int appsNumber = 0;
    private  List<String> appList = null;

    public AsyncResponse delegate = null;

    public ProgressDialog dialog;
    public Activity activity;

    public getAvailableAppsFromServer(Activity a){
        activity = a;
    }

    @Override
    protected List<String> doInBackground(String... params) {

        List<String> appsName = new ArrayList<>();
        URL u;
        try {
            u = new URL(params[0]);

            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();

            InputStream in = c.getInputStream();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];  //that stops the reading after 1024 chars..
            //in.read(buffer); //  Read from Buffer.
            //baos.write(buffer); // Write Into Buffer.

            int len1 = 0;
            while ((len1 = in.read(buffer)) != -1) {
                baos.write(buffer, 0, len1); // Write Into ByteArrayOutputStream Buffer.
            }

            String temp = "";
            String s = baos.toString();// baos.toString(); contain Version Code = 2; \n Version name = 2.1;


            String[] AppsNameArray = s.split("\n");
            appsNumber = AppsNameArray.length;

            for (int i = 0; i< appsNumber ; i++){
                Log.d("GetAppsFromServer", "Apps available: " + AppsNameArray[i]);
            }

            baos.close();

            appList = new ArrayList<>();

            for (int i = 0; i< AppsNameArray.length ; i++){
                appList.add(AppsNameArray[i]);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return appList;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = ProgressDialog.show(activity, "",
                "Loading. Please wait...", true);
    }

    @Override
    protected void onPostExecute(List<String> appsNameList) {
        super.onPostExecute(appsNameList);

        if (dialog.isShowing()) {
            try {
                dialog.dismiss();
            }catch(IllegalArgumentException e){
                e.printStackTrace();
            }
        }

        delegate.postGetAvailableAppFromServerResult(appsNameList);
    }
}

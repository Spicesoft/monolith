package spicesoft.appstore.AsyncTasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import spicesoft.appstore.Model.App;
import spicesoft.appstore.Model.ServerInfo;

/**
 * Created by Vincent on 04/06/15.
 */
public class getAppInfoFromServer extends AsyncTask<List<String>, Void, List<App>> {


    public AsyncResponse delegate = null;
    public ProgressDialog dialog;
    public Activity activity;

    public getAppInfoFromServer (Activity a){
        activity = a;
    }


    @Override
    protected final List<App> doInBackground(List<String>... params) {

        URL url;
        List<App> appList = new ArrayList<>();

        for (int j=0; j < params[0].size(); j++) {

            App app = new App();

            try {
                url = new URL(ServerInfo.BASE_URL + params[0].get(j) + ServerInfo.APP_VERSION_FILE);

                HttpURLConnection c = (HttpURLConnection) url.openConnection();

                c.setRequestMethod("GET");
                c.setDoOutput(true);
                c.connect();

                InputStream in = c.getInputStream();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];

                int len1 = 0;
                while ((len1 = in.read(buffer)) != -1) {
                    baos.write(buffer, 0, len1); // Write Into ByteArrayOutputStream Buffer.
                }

                String temp = "";
                String s = baos.toString();// baos.toString(); contain Version Code = 2; \n Version name = 2.1;

                int len = s.length();
                int i =0;
                for (int k = 0; k < len; k++) {
                    i = s.indexOf("=") + 1;
                    while (s.charAt(i) == ' ') // Skip Spaces
                    {
                        i++;
                    }
                    while (s.charAt(i) != ';' && ((s.charAt(i) >= 'a' && s.charAt(i) <= 'z') || (s.charAt(i) >= 'A' && s.charAt(i) <= 'Z') || (s.charAt(i) >= '0' && s.charAt(i) <= '9') || s.charAt(i) == '.') ) {  // && ((s.charAt(i) >= 'a' && s.charAt(i) <= 'z') || (s.charAt(i) >= 'A' && s.charAt(i) <= 'Z') || (s.charAt(i) >= '0' && s.charAt(i) <= '9') || s.charAt(i) == '.')
                        temp = temp.concat(Character.toString(s.charAt(i)));
                        i++;
                    }

                    s = s.substring(i); // Move to Next to Process.!
                    temp = temp + " "; // Separate w.r.t Space Version Code and Version Name.
                }



                String[] fields = temp.split(" ");// Make Array for Version Code and Version Name.

                app.versionCode = Integer.parseInt(fields[0]);// .ToString() Return String Value.
                app.versionName = fields[1];
                app.apkName = fields[2];
                app.pkgName = fields[3];
                app.activityName = fields[4];
                app.downloadURL = ServerInfo.BASE_URL + params[0].get(j) + "/" + app.apkName;

                baos.close();



                appList.add(app);

            } catch (MalformedURLException | ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
    protected void onPostExecute(List<App> app) {

        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        super.onPostExecute(app);
        delegate.postGetAppInfoFromServerResult(app);
    }
}

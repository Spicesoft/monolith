package spicesoft.autoupdater;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Vincent on 28/05/15.
 */

public class GetVersionFromServer extends AsyncTask<String,Void,String[]> {

    private static final String TAG = "UpdateDownloader";
    private final boolean DEBUG = true;

    public AsyncResponse delegate=null;

    public int VersionCode;
    public String VersionName = "";
    private String[] results;

    private Context context;
    public void setContext(Context contextf) {
        context = contextf;
    }


    @Override
    protected String[] doInBackground(String... params) {

        URL u;
        try {
            u = new URL(params[0]);

            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();

            InputStream in = c.getInputStream();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024]; //that stops the reading after 1024 chars..
            //in.read(buffer); //  Read from Buffer.
            //baos.write(buffer); // Write Into Buffer.

            int len1 = 0;
            while ((len1 = in.read(buffer)) != -1) {
                baos.write(buffer, 0, len1); // Write Into ByteArrayOutputStream Buffer.
            }

            String temp = "";
            String s = baos.toString();// baos.toString(); contain Version Code = 2; \n Version name = 2.1;

            for (int i = 0; i < s.length(); i++) {
                i = s.indexOf("=") + 1;
                while (s.charAt(i) == ' ') // Skip Spaces
                {
                    i++; // Move to Next.
                }
                while (s.charAt(i) != ';' && (s.charAt(i) >= '0' && s.charAt(i) <= '9' || s.charAt(i) == '.')) {
                    temp = temp.concat(Character.toString(s.charAt(i)));
                    i++;
                }
                //
                s = s.substring(i); // Move to Next to Process.!
                temp = temp + " "; // Separate w.r.t Space Version Code and Version Name.
            }
            String[] fields = temp.split(" ");// Make Array for Version Code and Version Name.

            VersionCode = Integer.parseInt(fields[0]);// .ToString() Return String Value.
            VersionName = fields[1];

            results = fields;

            Log.d(TAG, "ServerVersion = " + VersionCode + " Name = " + VersionName);

            baos.close();
        } catch (MalformedURLException e) {
            Toast.makeText(context, "Error." + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error." + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return results;
    }

    protected void onPostExecute(String[] fields) {

        try {
            VersionCode = Integer.parseInt(fields[0]);// .ToString() Return String Value.
            VersionName = fields[1];
            delegate.postResult(VersionCode, VersionName);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}


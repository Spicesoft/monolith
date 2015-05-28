package spicesoft.autoupdater;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Vincent on 20/05/15.
 */

public class UpdateDownloader extends AsyncTask<String,Void,Void> {

    private Context context;
    public void setContext(Context contextf){
        context = contextf;
    }

    @Override
    protected Void doInBackground(String... arg0) {

        try {
            URL url = new URL(arg0[0]);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();
            Log.d("Downloader", "Starting download");
            String PATH = "/sdcard/Download/";
            File file = new File(PATH);
            file.mkdirs();
            File outputFile = new File(file, "update.apk");
            if(outputFile.exists()){
                outputFile.delete();
                Log.d("Downloader", "Deleting existing file");
            }
            FileOutputStream fos = new FileOutputStream(outputFile);

            InputStream is = c.getInputStream();

            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len1);
            }
            fos.close();
            is.close();
            Log.d("Downloader", "Download finished");

            /*

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File("/sdcard/Download/update.apk")), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
            context.startActivity(intent);

            */

            try {
                String command;
                command = "pm install -r " + "/sdcard/Download/update.apk";
                Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", command });

                Log.d("Command line install","START");
                proc.waitFor();
                Log.d("Command line install", "DONE");
                command = "am start -n spicesoft.tandoori/spicesoft.tandoori.MainActivity";
                proc = Runtime.getRuntime().exec(new String[] { "su", "-c", command });
                proc.waitFor();
                Log.d("Command line install","EXEC");

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            Log.e("UpdateAPP", "Update error! " + e.getMessage());
        }
        return null;
    }

}
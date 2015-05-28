package spicesoft.autoupdater;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Vincent on 20/05/15.
 */

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Recurring alarm; requesting download service.");
        // start the download
        //Intent downloader = new Intent(context, UpdateDownloader.class);

        Toast.makeText(context, "ALARM !!!", Toast.LENGTH_LONG).show();

        /*
        downloader.setData(Uri
                .parse("http://feeds.feedburner.com/MobileTuts?format=xml"));
        context.startService(downloader);
        */

        UpdateDownloader ud = new UpdateDownloader();
        ud.setContext(context);
        ud.execute("http://4ltrophyece.fr/tandoori/app-debug.apk");

        Log.d("UPDATE", "UpdateDownloader launched");


    }
}
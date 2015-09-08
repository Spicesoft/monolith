package spicesoft.monolith.Receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * This class defines the alarm receiver.
 * An alarm is set in the mainActivity, when this alarm is triggered, it is intercepted
 * by this BroadCastReceiver.
 * When alarm is received, the AutoUpdater app is started in order to poll the server
 * for new versions of Mnolith
 * Created by Vincent on 29/05/15.
 */
public class UpdateAlarmReceiver extends BroadcastReceiver {

    private static final boolean DEBUG = true;

    public static final String UPDATER_PACKAGE_NAME = "spicesoft.appstore";
    public static final String UPDATER_ACTIVITY_NAME = "MainActivity";


    @Override
    public void onReceive(Context context, Intent intent) {

        Intent sIntent = new Intent("android.intent.action.MAIN");

        sIntent.setComponent(ComponentName.unflattenFromString(UPDATER_PACKAGE_NAME + "/" + UPDATER_PACKAGE_NAME +"."+ UPDATER_ACTIVITY_NAME));
        sIntent.addCategory("android.intent.category.LAUNCHER");

        sIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if(DEBUG) Toast.makeText(context, "Alarm received !", Toast.LENGTH_SHORT).show();

        context.startActivity(sIntent);
    }
}

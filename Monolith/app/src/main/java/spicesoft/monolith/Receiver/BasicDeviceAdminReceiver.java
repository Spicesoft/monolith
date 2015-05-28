package spicesoft.monolith.Receiver;

/**
 *
 * Created by Vincent on 19/05/15.
 */
import android.app.admin.DeviceAdminReceiver;
import android.content.ComponentName;
import android.content.Context;


public class BasicDeviceAdminReceiver extends DeviceAdminReceiver {

    public static ComponentName getComponentName(Context context) {
        return new ComponentName(context.getApplicationContext(), BasicDeviceAdminReceiver.class);
    }
}
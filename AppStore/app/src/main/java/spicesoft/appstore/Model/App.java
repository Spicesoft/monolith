package spicesoft.appstore.Model;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.Environment;
import android.widget.ImageView;

/**
 * Created by Vincent on 03/06/15.
 */
public class App {

    public String name;
    public String description;
    public int versionCode;
    public String versionName;

    public String downloadURL;
    public static String downloadDir = Environment.getExternalStorageDirectory() + "/Download/updates/";
    public String apkName;
    public String pkgName;
    public String activityName;


    public String toString(){
        return "Name : " + name + "\n" +
                "Description : " + description + "\n" +
                "Apk : " + apkName + "\n" +
                "Package name : " + pkgName + "\n" +
                "Activity name : " + activityName ;

    }
}

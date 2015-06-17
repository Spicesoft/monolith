package spicesoft.monolith.KisokMode;

import android.os.PowerManager;

/**
 * Created by Vincent on 01/06/15.
 */
public class WakeLockInstance {

    private static WakeLockInstance wli = null;

    private PowerManager.WakeLock wl = null;
    private PowerManager.WakeLock pwl = null;

    public WakeLockInstance(){

    }

    public static WakeLockInstance getInstance(){
        if(wli == null) wli = new WakeLockInstance();
        return wli;
    }


    public PowerManager.WakeLock getWl(){
        return wl;
    }

    public void setWl(PowerManager.WakeLock obj){
        wl = obj;
    }

    public PowerManager.WakeLock getPwl(){
        return pwl;
    }

    public void setPwl(PowerManager.WakeLock obj){
        pwl = obj;
    }
}

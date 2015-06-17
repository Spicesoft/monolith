package spicesoft.appstore;

import spicesoft.appstore.AsyncTasks.ApkDownloader;

/**
 * Created by Vincent on 11/06/15.
 */
public class UpdateDownloaderInstance {


    private static UpdateDownloaderInstance instance;
    private ApkDownloader DlUpdate = null;


    public static UpdateDownloaderInstance getInstance(){
        if (instance == null) instance = new UpdateDownloaderInstance();
        return  instance;
    }

    public void setDlUpdate(ApkDownloader d){
        DlUpdate = d;
    }

    public ApkDownloader getDlUpdate(){
        return DlUpdate;
    }

}

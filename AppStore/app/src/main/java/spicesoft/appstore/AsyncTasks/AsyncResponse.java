package spicesoft.appstore.AsyncTasks;

import java.util.List;

import spicesoft.appstore.Model.App;

/**
 * This interface defines the callback functions for Async tasks.
 * Created by Vincent on 29/05/15.
 */
public interface AsyncResponse {

     /**
      * Method called when the update is installed.
      */
     void postInstallDownloadedAppResult(App app);

     /**
      * This method gets called when the update download is done.
      */
     void postApkDownloader(App app);

     /**
      * This method gets called when the app is uninstalled.
      */
     void postUninstallApp(App app);

     void postGetAvailableAppFromServerResult(List<String> appList);

     void postGetAppInfoFromServerResult(List<App> app);
}

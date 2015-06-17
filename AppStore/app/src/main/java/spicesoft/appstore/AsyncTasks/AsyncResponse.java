package spicesoft.appstore.AsyncTasks;

import java.util.List;

import spicesoft.appstore.Model.App;

/**
 * This interface defines the callback functions for Async tasks.
 * Created by Vincent on 29/05/15.
 */
public interface AsyncResponse {
     /**
      * Method called when the version available on the update server is checked.
      * @param versionCode is the version code read from the server version file.
      * @param versionName is the version name read from the server version file.
      */
     void postGetVersionFromServerResult(int versionCode, String versionName);

     /**
      * Method called when the update is installed.
      */
     void postInstallDownloadedAppResult();

     /**
      * This method gets called when the update download is done.
      */
     void postDownloadUpdate();

     /**
      * This method gets called when the app is uninstalled.
      */
     void postUninstallApp();

     void postGetAvailableAppFromServerResult(List<String> appList);

     void postGetAppInfoFromServerResult(List<App> app);
}

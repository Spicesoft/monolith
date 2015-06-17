/*
 * Copyright (C) 2013 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package spicesoft.appstore.util;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import java.security.MessageDigest;
import java.util.regex.Pattern;

/**
 * This class gives connectivity tools
 */
public class AndroidUtils {

    public static final String TAG = "AndroidUtils";
    // Leave this off for release
    public static final boolean DEBUG = true;

    public static final String ACTION_SETUP_WIFI = "com.android.net.wifi.SETUP_WIFI_NETWORK";

    public static final String EXTRA_FIRST_RUN = "firstRun";
    public static final String EXTRA_ALLOW_SKIP = "allowSkip";
    public static final String EXTRA_SHOW_SKIP = "extra_prefs_show_button_bar";
    public static final String EXTRA_AUTO_FINISH = "wifi_auto_finish_on_connect";
    public static final String EXTRA_SHOW_BUTTON_BAR = "extra_prefs_show_button_bar";
    public static final String EXTRA_PREF_BACK_TEXT = "extra_prefs_set_back_text";
    public static final String EXTRA_ONLY_ACCESS_POINTS = "only_access_points";

    public static final String MINIMUM_APP_VERSION = "minimum_app_version";

    public static final int REQUEST_CODE_SETUP_WIFI = 0;

    public static final int NOTIFICATION_ID_PASSWORD_RESET = 666;
    public static final int NOTIFICATION_ID_INCOMPATIBLE_VERSION = 667;



    public static final Pattern EMAIL_ADDRESS
            = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[A-Za-z]{2,6}" +
                    ")+"
    );

    private static final String KEY_UDID = "udid";


    private AndroidUtils(){}


    public static void tryEnablingWifi(Context context) {
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
    }

    private static Intent getWifiSetupIntent(Context context) {
        Intent intent = new Intent(ACTION_SETUP_WIFI);
        intent.putExtra(EXTRA_FIRST_RUN, true);
        intent.putExtra(EXTRA_ALLOW_SKIP, true);
        intent.putExtra(EXTRA_SHOW_BUTTON_BAR, true);
        intent.putExtra(EXTRA_ONLY_ACCESS_POINTS, true);
        intent.putExtra(EXTRA_SHOW_SKIP, true);
        intent.putExtra(EXTRA_AUTO_FINISH, true);
        //intent.putExtra(EXTRA_PREF_BACK_TEXT, context.getString(R.string.skip));
        return intent;
    }

    public static void launchWifiSetup(Activity context) {
        AndroidUtils.tryEnablingWifi(context);
        Intent intent = getWifiSetupIntent(context);
        context.startActivityForResult(intent, REQUEST_CODE_SETUP_WIFI);
    }

    public static void launchWifiSetup(Fragment fragment) {
        final Context context = fragment.getActivity();
        AndroidUtils.tryEnablingWifi(context);
        Intent intent = getWifiSetupIntent(context);
        fragment.startActivityForResult(intent, REQUEST_CODE_SETUP_WIFI);
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi != null && mWifi.isConnected();
    }


    public static boolean hasTelephony(Context context) {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
    }

    public static boolean isGSMPhone(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int phoneType = telephonyManager.getPhoneType();
        return phoneType == TelephonyManager.PHONE_TYPE_GSM;
    }

    public static boolean isSimMissing(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telephonyManager.getSimState();
        return simState == TelephonyManager.SIM_STATE_ABSENT || simState == TelephonyManager.SIM_STATE_UNKNOWN;
    }


    public static byte[] digestBytes(String algorithm, byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] digest = md.digest(bytes);
            return digest;
        } catch (Exception e) {
            return null;
        }
    }

    public static int getApplicationVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
}

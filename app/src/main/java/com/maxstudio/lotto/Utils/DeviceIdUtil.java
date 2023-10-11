package com.maxstudio.lotto.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;

public class DeviceIdUtil {

    @SuppressLint("HardwareIds")
    public static String getDeviceId(Context context)
    {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

}

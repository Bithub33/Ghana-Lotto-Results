package com.maxstudio.lotto.Utils;

import android.net.ConnectivityManager;
import android.net.Network;

import androidx.annotation.NonNull;

public class NetworkCallback extends ConnectivityManager.NetworkCallback {

    @Override
    public void onAvailable(@NonNull Network network) {
        super.onAvailable(network);

    }

    @Override
    public void onLost(@NonNull Network network) {
        super.onLost(network);

    }


}

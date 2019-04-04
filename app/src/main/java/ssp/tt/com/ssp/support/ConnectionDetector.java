package ssp.tt.com.ssp.support;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

public class ConnectionDetector {


    public static boolean isNetworkConnection = false;
    private Context _context;

    /**
     * @param context
     */
    public ConnectionDetector(Context context) {
        this._context = context;
    }


    /**
     * @return boolean
     */
    public boolean isConnectingToInternet() {
        final int version = Build.VERSION.SDK_INT;
        if (version >= Build.VERSION_CODES.M) {
            final ConnectivityManager connectivityManager = (ConnectivityManager) _context.
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            final Network network = connectivityManager.getActiveNetwork();
            final NetworkCapabilities capabilities = connectivityManager
                    .getNetworkCapabilities(network);
            return capabilities != null
                    && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
        } else {
            return isNetworkConnection;
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
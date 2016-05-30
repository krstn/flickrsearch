package demo.kristine.flickrsearch.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by kristine on 5/30/16.
 */
public class ConnectivityUtil {

  public static boolean isNetworkConnected(Context context) {
    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (connectivityManager != null) {
      NetworkInfo network = connectivityManager.getActiveNetworkInfo();
      if (network != null) return network.isConnected();
    }
    return false;
  }
}

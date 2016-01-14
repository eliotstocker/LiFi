package tv.piratemedia.lifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

import java.util.List;

import tv.piratemedia.lifi.DataModel.Rule;
import tv.piratemedia.lifi.DataModel.RulesSQLiteHelper;
import tv.piratemedia.lightcontroler.api.LightControllerAPI;
import tv.piratemedia.lightcontroler.api.LightControllerException;
import tv.piratemedia.lightcontroler.api.LightZone;

/**
 * Created by eliot on 14/01/2016.
 */
public class WifiReceiver extends BroadcastReceiver {
    private LightControllerAPI api;
    @Override
    public void onReceive(Context context, Intent intent) {
        RulesSQLiteHelper rsh = new RulesSQLiteHelper(context);
        Handler h = new Handler();
        try {
            api = new LightControllerAPI(context);
        } catch (LightControllerException e) {
            Log.d("Receiver", "LightController API Could not start, Not installed or old");
            return;
        }

        if(!api.hasPermission()) {
            Log.d("Receiver", "Permission not granted for API");
        }

        if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            final SharedPreferences sp = context.getSharedPreferences("prefs", context.MODE_PRIVATE);
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (info != null && info.isConnected()) {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                final String ssid = wifiInfo.getSSID().substring(1, wifiInfo.getSSID().length() -1);
                if(!ssid.equals("unknown ssid") && !ssid.equals(sp.getString("lastNetwork", "unknown"))) {
                    sp.edit().putString("lastNetwork", ssid).apply();
                    Log.d("Receiver", "Connected to: " + ssid);

                    List<Rule> rules = rsh.getRules();
                    for(final Rule r : rules) {
                        if(r.getSSID().equals(ssid)) {
                            Runnable run = new Runnable() {
                                @Override
                                public void run() {
                                    Log.d("Receiver", "Rule found matching network: "+ssid);
                                    LightZone lz = api.getZone(r.getZone(), r.getType());
                                    if(r.getPower()) {
                                        api.lightsOn(lz);
                                        Log.d("Receiver", "Turn Lights on in "+lz.Name);
                                    } else {
                                        api.lightsOff(lz);
                                        Log.d("Receiver", "Turn Lights off in "+lz.Name);
                                    }
                                }
                            };
                            h.postDelayed(run, 100);
                        }
                    }
                }
            } else {
                Log.d("Receiver", "Disconnected From: "+sp.getString("lastNetwork","unknown"));
                sp.edit().remove("lastNetwork").apply();
            }
        }
    }
}

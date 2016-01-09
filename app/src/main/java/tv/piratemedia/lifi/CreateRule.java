package tv.piratemedia.lifi;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tv.piratemedia.lifi.DataModel.Rule;
import tv.piratemedia.lifi.DataModel.RulesSQLiteHelper;
import tv.piratemedia.lightcontroler.api.LightControllerAPI;
import tv.piratemedia.lightcontroler.api.LightControllerException;
import tv.piratemedia.lightcontroler.api.LightZone;

/**
 * Created by eliotstocker on 09/01/16.
 */
public class CreateRule extends AppCompatActivity {
    private RulesSQLiteHelper db;
    private Spinner network;
    private List<WifiConfiguration> networks;
    private LightControllerAPI lcAPI;
    private LightZone Zone;
    private TextView ZoneName;
    private Switch Power;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.rule_create);

        network = (Spinner) findViewById(R.id.networkSelector);
        ZoneName = (TextView) findViewById(R.id.zone);
        Power = (Switch) findViewById(R.id.power);

        try {
            lcAPI = new LightControllerAPI(this);
        } catch (LightControllerException e) {
            finish();
        }

        setupWifiSpinner();
    }

    public void save(View v) {
        if(network.getSelectedItemPosition() < 1) {
            //no network selected
        }
        if(Zone == null) {
            //no zone selected
        }

        WifiConfiguration wc = networks.get(network.getSelectedItemPosition() -1);
        String ssid = wc.SSID.substring(1, wc.SSID.length() -1);
        Rule rule = new Rule(Zone.ID, Zone.isColor() ? "color" : "white", Power.isChecked(), ssid);

        db = new RulesSQLiteHelper(this);
        db.addRule(rule);
        finish();
    }

    public void close(View v) {
        finish();
    }

    public void selectZone(View v) {
        lcAPI.pickZone();
    }

    private void setupWifiSpinner() {
        WifiManager wm = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        networks = wm.getConfiguredNetworks();

        List<String> list = new ArrayList<String>();
        list.add(0, "Select Wifi Network");
        for(WifiConfiguration wc : networks) {
            list.add(wc.SSID.substring(1, wc.SSID.length() -1));
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        network.setAdapter(dataAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == LightControllerAPI.PickRequestCode) {
            if(resultCode == RESULT_OK) {
                Zone = (LightZone)data.getSerializableExtra("LightZone");
                ZoneName.setText(Character.toUpperCase(Zone.Type.charAt(0)) + Zone.Type.substring(1) + ": " + Zone.Name);
            } else if(resultCode == RESULT_CANCELED) {
                //picker was closed without picking
            }
        }
    }
}

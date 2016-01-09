package tv.piratemedia.lifi;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import tv.piratemedia.lifi.DataModel.Rule;
import tv.piratemedia.lifi.DataModel.RulesSQLiteHelper;
import tv.piratemedia.lightcontroler.api.LightControllerAPI;
import tv.piratemedia.lightcontroler.api.LightControllerException;
import tv.piratemedia.lightcontroler.api.LightZone;
import tv.piratemedia.lightcontroler.api.OnPermissionChanged;

public class Prefs extends AppCompatActivity {
    private RulesSQLiteHelper db;
    private RecyclerView rvl;
    private TextView prompt;
    private LinearLayout perms;
    private Context context;

    private RuleAdapter rules;
    private LightControllerAPI lcAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefs);

        context = this;
        db = new RulesSQLiteHelper(this);

        ActivityCompat.requestPermissions(this, new String[] {"tv.piratemedia.lightcontroler.api"}, 0);

        //setup api and show prompt to install if needed;
        try {
            lcAPI = new LightControllerAPI(this);
        } catch (LightControllerException e) {
            Intent i = new Intent(this, Install.class);
            if(e.getCode() == LightControllerException.TYPE_APPLICATION_MISSING) {
                i.putExtra("installed", false);
            } else if(e.getCode() == LightControllerException.TYPE_APPLICATION_OLD) {
                i.putExtra("installed", true);
            }
            startActivity(i);
            finish();
            return;
        }

        perms = (LinearLayout) findViewById(R.id.perms);

        //check for permission and show dialog until granted
        if(!lcAPI.hasPermission()) {
            lcAPI.RequiestPermission();
            perms.setVisibility(View.VISIBLE);
            lcAPI.setOnPermissionChanged(new OnPermissionChanged() {
                @Override
                public void onChange() {
                    //still need to check if your app has permission as this is called on any app permission change
                    if (lcAPI.hasPermission()) {
                        perms.setVisibility(View.GONE);
                    } else {
                        perms.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        rvl = (RecyclerView) findViewById(R.id.rv);
        prompt = (TextView) findViewById(R.id.prompt);

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateRules();
    }

    private void updateRules() {
        List<Rule> list = db.getRules();

        if(list.size() > 0) {
            prompt.setVisibility(View.GONE);
            rvl.setVisibility(View.VISIBLE);
        } else {
            prompt.setVisibility(View.VISIBLE);
            rvl.setVisibility(View.GONE);
        }

        LinearLayoutManager llm = new LinearLayoutManager(context);
        rvl.setLayoutManager(llm);

        rules = new RuleAdapter(list, lcAPI, db);
        rvl.setAdapter(rules);
    }

    public void requestPermission(View v) {
        lcAPI.RequiestPermission();
    }

    public void createRule(View v) {
        Intent i = new Intent(this, CreateRule.class);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == LightControllerAPI.PickRequestCode) {
            if(resultCode == RESULT_OK) {
                LightZone Zone = (LightZone)data.getSerializableExtra("LightZone");
                rules.updateCurrentZone(Zone.ID, Zone.isColor() ? "color" : "white");
            } else if(resultCode == RESULT_CANCELED) {
                //picker was closed without picking
            }
        }
    }
}

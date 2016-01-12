package tv.piratemedia.lifi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

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
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefs);

        context = this;

        String Activity = getIntent().getComponent().getClassName().replace(getPackageName()+".", "");
        final SharedPreferences sp = this.getSharedPreferences("prefs", MODE_PRIVATE);
        if(!Activity.equals("MainActivity") && !sp.getBoolean("asked_hide", false) && utils.iconEnabled(this)) {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                    .title("Hide Launcher Icon?")
                    .content("Would you like to hide the LiFi Launcher icon?, you will still be able to access the app from within LightController and you may re enable the icon at any time in the context menu")
                    .positiveText("Hide it")
                    .negativeText("Keep it")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            menu.findItem(R.id.ShowLauncherIcon).setChecked(false);
                            utils.hideIcon(context);
                        }
                    });
            builder.build().show();
            sp.edit().putBoolean("asked_hide", true).apply();
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.prefs_menu, menu);
        menu.findItem(R.id.ShowLauncherIcon).setChecked(utils.iconEnabled(this));
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.ShowLauncherIcon:
                if(item.isChecked()) {
                    utils.hideIcon(this);
                    item.setChecked(false);
                } else {
                    utils.showIcon(this);
                    item.setChecked(true);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

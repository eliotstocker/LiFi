package tv.piratemedia.lifi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import tv.piratemedia.lightcontroler.api.LightControllerAPI;

/**
 * Created by eliotstocker on 09/01/16.
 */
public class Install extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_install);

        if(getIntent().getBooleanExtra("installed", false)) {
            findViewById(R.id.not_installed).setVisibility(View.GONE);
            findViewById(R.id.not_uptodate).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.not_installed).setVisibility(View.VISIBLE);
            findViewById(R.id.not_uptodate).setVisibility(View.GONE);
        }

    }

    public void gotoPlaystore(View v) {
        LightControllerAPI.getApplicationFromPlayStore(this);
        finish();
    }

    public void close(View v) {
        finish();
    }
}

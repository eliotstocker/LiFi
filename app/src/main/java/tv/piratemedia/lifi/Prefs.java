package tv.piratemedia.lifi;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import tv.piratemedia.lifi.DataModel.Rule;
import tv.piratemedia.lifi.DataModel.RulesSQLiteHelper;

public class Prefs extends AppCompatActivity {
    private RulesSQLiteHelper db;
    private RecyclerView rvl;
    private TextView prompt;
    private Context context;

    private RuleAdapter rules;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefs);

        context = this;
        db = new RulesSQLiteHelper(this);
        rvl = (RecyclerView) findViewById(R.id.rv);
        prompt = (TextView) findViewById(R.id.prompt);

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

        rules = new RuleAdapter(list);
        rvl.setAdapter(rules);
    }

    public void createRule(View v) {

    }
}

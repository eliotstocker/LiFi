package tv.piratemedia.lifi;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import tv.piratemedia.lifi.DataModel.Rule;
import tv.piratemedia.lifi.DataModel.RulesSQLiteHelper;
import tv.piratemedia.lightcontroler.api.LightControllerAPI;
import tv.piratemedia.lightcontroler.api.LightZone;

/**
 * Created by eliotstocker on 09/01/16.
 */
public class RuleAdapter extends RecyclerView.Adapter<RuleAdapter.RuleViewHolder> {
    private List<Rule> rules;
    private LightControllerAPI lcapi;
    private RulesSQLiteHelper db;

    private Rule CurrentZone;
    private RuleViewHolder CurrentHolder;

    public RuleAdapter(List<Rule> rules, LightControllerAPI lcapi, RulesSQLiteHelper db) {
        this.rules = rules;
        this.lcapi = lcapi;
        this.db = db;
    }

    @Override
    public RuleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rule_card, parent, false);
        RuleViewHolder pvh = new RuleViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final RuleViewHolder holder, final int position) {
        final Rule r = rules.get(position);
        holder.enabled.setChecked(r.getEnabled());
        LightZone lz = lcapi.getZone(r.getZone(), r.getType());
        holder.zone.setText(Character.toUpperCase(lz.Type.charAt(0)) + lz.Type.substring(1) + ": " + lz.Name);
        holder.ssid.setText(r.getSSID());
        holder.power.setChecked(r.getPower());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteRule(r.getID());
                rules.remove(position);
                notifyDataSetChanged();
            }
        });
        holder.enabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                r.setEnabled(isChecked);
                db.updateRule(r);
            }
        });
        holder.power.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                r.setPower(isChecked);
                db.updateRule(r);
            }
        });
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrentHolder = holder;
                CurrentZone = r;
                lcapi.pickZone();
            }
        });
        holder.zone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrentHolder = holder;
                CurrentZone = r;
                lcapi.pickZone();
            }
        });
        if(r.getStart() > -1) {
            holder.start.setText(""+r.getStart());
            holder.end.setText(""+r.getEnd());
        } else {
            holder.start.setText("");
            holder.end.setText("");
        }
    }

    public void updateCurrentZone(int zone, String type) {
        CurrentZone.setZone(zone);
        CurrentZone.setType(type);

        db.updateRule(CurrentZone);

        LightZone lz = lcapi.getZone(zone, type);
        CurrentHolder.zone.setText(Character.toUpperCase(lz.Type.charAt(0)) + lz.Type.substring(1) + ": " + lz.Name);

        CurrentZone = null;
        CurrentHolder = null;
    }

    @Override
    public int getItemCount() {
        return rules.size();
    }

    public static class RuleViewHolder extends RecyclerView.ViewHolder {
        private Switch enabled;
        private Switch power;
        private TextView ssid;
        private TextView zone;
        private EditText start;
        private EditText end;
        private ImageButton delete;
        private ImageButton edit;
        RuleViewHolder(View itemView) {
            super(itemView);
            enabled = (Switch) itemView.findViewById(R.id.enable);
            power = (Switch) itemView.findViewById(R.id.power);
            ssid = (TextView) itemView.findViewById(R.id.ssid);
            zone = (TextView) itemView.findViewById(R.id.zone);
            start = (EditText) itemView.findViewById(R.id.startTime);
            end = (EditText) itemView.findViewById(R.id.endTime);
            delete = (ImageButton) itemView.findViewById(R.id.delete);
            edit = (ImageButton) itemView.findViewById(R.id.edit);
        }
    }
}

package tv.piratemedia.lifi;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import tv.piratemedia.lifi.DataModel.Rule;

/**
 * Created by eliotstocker on 09/01/16.
 */
public class RuleAdapter extends RecyclerView.Adapter<RuleAdapter.RuleViewHolder> {
    private List<Rule> rules;

    public RuleAdapter(List<Rule> rules) {
        this.rules = rules;
    }

    @Override
    public RuleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rule_card, parent, false);
        RuleViewHolder pvh = new RuleViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(RuleViewHolder holder, int position) {
        Rule r = rules.get(position);
        holder.enabled.setChecked(r.getEnabled());
        holder.zone.setText(r.getZone()); //TODO: get Zone Name;
        holder.ssid.setText(r.getSSID());
        holder.power.setChecked(r.getPower());
        if(r.getStart() > -1) {
            holder.start.setText(r.getStart());
            holder.end.setText(r.getEnd());
        } else {
            holder.start.setText("");
            holder.end.setText("");
        }
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
        RuleViewHolder(View itemView) {
            super(itemView);
            enabled = (Switch) itemView.findViewById(R.id.enable);
            power = (Switch) itemView.findViewById(R.id.power);
            ssid = (TextView) itemView.findViewById(R.id.ssid);
            zone = (TextView) itemView.findViewById(R.id.zone);
            start = (EditText) itemView.findViewById(R.id.startTime);
            end = (EditText) itemView.findViewById(R.id.endTime);
        }
    }
}

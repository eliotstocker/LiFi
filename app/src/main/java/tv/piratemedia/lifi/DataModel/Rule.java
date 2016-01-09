package tv.piratemedia.lifi.DataModel;

/**
 * Created by eliotstocker on 09/01/16.
 */
public class Rule {
    private int id = -1;
    private int zoneID;
    private boolean power;
    private int startTime = -1;
    private int endTime = -1;
    private boolean enabled = true;
    private String type;
    private String SSID;

    public Rule(){}

    public Rule(int zoneID, String type, boolean power, String SSID) {
        super();
        this.zoneID = zoneID;
        this.type = type;
        this.power = power;
        this.SSID = SSID;
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getID() {
        return this.id;
    }

    public String getSSID() {
        return this.SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setZone(int zone) {
        this.zoneID = zone;
    }

    public int getZone() {
        return this.zoneID;
    }

    public void setPower(boolean power) {
        this.power = power;
    }

    public boolean getPower() {
        return this.power;
    }

    public void setTimes(int start, int end) {
        this.startTime = start;
        this.endTime = end;
    }

    public int getStart() {
        return this.startTime;
    }

    public int getEnd() {
        return this.endTime;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    @Override
    public String toString() {
        return "Rule [Zone=" + zoneID + ", Type=" + type + ", Power=" + (power ? "on" : "off") + ", StartTime=" + startTime
                + ", EndTime=" + endTime + ", Enabled=" + enabled + ", SSID=" + SSID + "]";
    }
}

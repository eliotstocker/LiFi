package tv.piratemedia.lifi.DataModel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class RulesSQLiteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "lightcontroller";

    private static final String TABLE_RULES = "lc_rules";

    private static final String KEY_ID = "id";
    private static final String KEY_ZONE = "zone";
    private static final String KEY_POWER = "power";
    private static final String KEY_START = "start";
    private static final String KEY_END = "end";
    private static final String KEY_ENABLED = "enabled";
    private static final String KEY_SSID = "ssid";

    private static final String[] COLUMNS = {KEY_ID,KEY_ZONE,KEY_POWER,KEY_START,KEY_END};

    public RulesSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_RULES + " ( " +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_ZONE + " INTEGER, " +
                KEY_POWER + " BOOLEAN, " +
                KEY_START + " INTEGER, " +
                KEY_END + " INTEGER, " +
                KEY_ENABLED + " BOOLEAN, " +
                KEY_SSID + " STRING)";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_RULES);
        this.onCreate(db);
    }

    public void addRule(Rule rule){
        Log.d("addRule", rule.toString());
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ZONE, rule.getZone());
        values.put(KEY_POWER, rule.getPower());
        values.put(KEY_START, rule.getStart());
        values.put(KEY_END, rule.getEnd());
        values.put(KEY_ENABLED, rule.getEnabled());

        db.insert(TABLE_RULES, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        db.close();
    }

    public Rule getRule(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor =
                db.query(TABLE_RULES, // a. table
                        COLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        if (cursor != null)
            cursor.moveToFirst();

        Rule rule = new Rule();
        rule.setID(cursor.getInt(0));
        rule.setZone(cursor.getInt(1));
        rule.setPower(cursor.getInt(2) > 0);
        rule.setTimes(cursor.getInt(3), cursor.getInt(4));
        rule.setEnabled(cursor.getInt(5) > 0);

        Log.d("getRule(" + id + ")", rule.toString());

        return rule;
    }

    public List<Rule> getRules() {
        List<Rule> books = new LinkedList<Rule>();

        String query = "SELECT  * FROM " + TABLE_RULES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Rule rule = null;
        if (cursor.moveToFirst()) {
            do {
                rule = new Rule();
                rule.setID(cursor.getInt(0));
                rule.setZone(cursor.getInt(1));
                rule.setPower(cursor.getInt(2) > 0);
                rule.setTimes(cursor.getInt(3), cursor.getInt(4));
                rule.setEnabled(cursor.getInt(5) > 0);

                // Add book to books
                books.add(rule);
            } while (cursor.moveToNext());
        }

        Log.d("getRules()", books.toString());

        return books;
    }

    public int updateRule(Rule rule) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ZONE, rule.getZone());
        values.put(KEY_POWER, rule.getPower());
        values.put(KEY_START, rule.getStart());
        values.put(KEY_END, rule.getEnd());
        values.put(KEY_ENABLED, rule.getEnabled());

        int i = db.update(TABLE_RULES, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(rule.getID()) }); //selection args

        db.close();

        return i;
    }

    public void deleteRule(Rule rule) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_RULES,
                KEY_ID+" = ?",
                new String[] { String.valueOf(rule.getID()) });

        db.close();

        Log.d("deleteRule", rule.toString());

    }
}

package hhh.yyy.servtest2;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.net.TrafficStats;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.os.Build;
import android.preference.PreferenceManager;

import cronService.AlarmReceiver;

public class MainActivity extends ActionBarActivity implements OnSharedPreferenceChangeListener {

    private MainActivity context;
    final public static int CHECKTIMER = 5000;//5 sec
    public static SharedPreferences sharedpreferences;

    final public static String SPLANG = "baterSaver";
    final public static String SIZETOOFF = "SizeToOff";
    final public static String ISACTIVE = "isActive";
    final public static String OPTIONPAIRS = "optionPairs";
    public static String optionPairs;// = "key1=value1;key2=value2;key3=value3";

    boolean CheckboxPreference;
    String ListPreference;
    String editTextPreference;
    boolean SwitchPreference;
    String secondEditTextPreference;
    String customPref;

    public static String getStraffStart() {
        return String.valueOf(TrafficStats.getTotalRxBytes() / 1024L);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }

        //Get Preferenece localisation
        MainActivity.sharedpreferences = getBaseContext().getSharedPreferences(SPLANG, Context.MODE_PRIVATE);

        String stats = sharedpreferences.getString(SPLANG, "");
        String sizeToOff = sharedpreferences.getString(SIZETOOFF, "");
        String isActive = sharedpreferences.getString(ISACTIVE, "");
        MainActivity.optionPairs = MainActivity.sharedpreferences.getString(OPTIONPAIRS, "");

        Log.d("asd", "stats=" + stats);
        Log.d("asd", "sizeToOff=" + sizeToOff);

        if (stats.isEmpty()) {
            Log.d("asd", "Saving data to Files");
            SharedPreferences.Editor editor = sharedpreferences.edit();

            editor.putString(MainActivity.SPLANG, this.getStraffStart());
            editor.putString(MainActivity.SIZETOOFF, "50");//kb
            editor.putString(MainActivity.ISACTIVE, "true");
            editor.commit();
        }

        Log.d("asd1", "stats saved=" + stats);
        Log.d("asd1", "sizeToOff saved=" + sizeToOff);


        //CronService
        this.context = this;
        Intent alarm = new Intent(this.context, AlarmReceiver.class);
        boolean alarmRunning = (PendingIntent.getBroadcast(this.context, 0, alarm, PendingIntent.FLAG_NO_CREATE) != null);
        Log.d("asd", "Start Activity");
        if (alarmRunning == false) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, 0, alarm, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), CHECKTIMER, pendingIntent);
        } else {
            Log.d("asd", "Alarm is olready running!!!");
        }

    }

    public String getOptionValue(String keyOption){

        if(MainActivity.optionPairs != null) {
            for (String kvPair : optionPairs.split(";")) {
                String[] kv = kvPair.split("=");
                String key = kv[0];
                String value = kv[1];
                if (key.equals(keyOption)) {
                    return value;
                }
            }
        }
        return null;
    }
    private void getPrefs() {
        // Get the xml/preferences.xml preferences


        SharedPreferences prefs = PreferenceManager
                .getSharedPreferences(SPLANG, Context.MODE_PRIVATE);
                //.getDefaultSharedPreferences(getBaseContext());
        CheckboxPreference = prefs.getBoolean("checkboxPref", Boolean.parseBoolean(getOptionValue("displayOn")));


        //ListPreference = prefs.getString("listPref", "nr1");
        editTextPreference = prefs.getString("SizeToOff", (getOptionValue("SizeToOff")));

        SwitchPreference = prefs.getBoolean("mobileInet", Boolean.parseBoolean(getOptionValue("mobileInet")));
        SwitchPreference = prefs.getBoolean("bluetooth", Boolean.parseBoolean(getOptionValue("bluetooth")));
        SwitchPreference = prefs.getBoolean("wifi", Boolean.parseBoolean(getOptionValue("wifi")));
        SwitchPreference = prefs.getBoolean("bootOn", Boolean.parseBoolean(getOptionValue("bootOn")));

        // Get the custom preference
        SharedPreferences mySharedPreferences = getSharedPreferences(
                "myCustomSharedPrefs", Activity.MODE_PRIVATE);
        customPref = mySharedPreferences.getString("myCusomPref", "");
    }


    public void onResume() {
        super.onResume();
        Log.v("RESUME", "!!ONRESUME!!!");
    }

    public void showSettings() {
        Intent i = new Intent(this, Prefs.class);
        startActivity(i);
    }

    public void onclick(View v) {

        Log.v(this.getClass().getName(), "onClick: Starting service!");
        startService(new Intent(this, ServiceExample.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            showSettings();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container,
                    false);
            return rootView;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

        Log.v(this.getClass().getName(), "Pref=" + prefs.getAll() + "; key=" + key);

        switch (key) {
            case "bootOn":
                prefs.getBoolean("bootOn", true);
                break;
            case "INTERVAL":
                String inter = prefs.getString("INTERVAL", "30");

                ServiceExample.INTERVAL = Integer.parseInt(inter) * 1000;
                //Log.v(this.getClass().getName(), "interv="+prefs.getInt("INTERVAL", 30)+"; key="+key);

                break;
            case "SizeToOff":
                String sizeoff = prefs.getString("SizeToOff", "30");

                ServiceExample.SizeToOff = Integer.parseInt(sizeoff);
                //Log.v(this.getClass().getName(), "SizeToOff="+prefs.getInt("SizeToOff", 30)+"; key="+key);
                break;

            default:
                break;
        }

        //ReStartServices();

    }

}

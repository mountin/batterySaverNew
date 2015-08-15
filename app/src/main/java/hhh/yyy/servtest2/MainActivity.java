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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.os.Build;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.zip.Inflater;

import cronService.AlarmReceiver;


public class MainActivity extends ActionBarActivity  {

    private MainActivity context;
    final public static int CHECKTIMER = 120000;//2.2 min
    //final public static int CHECKTIMER = 15000;//2.2 min
    public static SharedPreferences sharedpreferences;

    final public static String SPLANG = "baterSaver";
    final public static String SIZETOOFF = "SizeToOff";
    final public static String ISACTIVE = "isActive";
    final public static String OPTIONPAIRS = "optionPairs";
    public static String optionPairs;
    public static SharedPreferences.Editor editor;

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

//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.container, new PlaceholderFragment()).commit();
//        }


        //Get Preferenece localisation
        MainActivity.sharedpreferences = getBaseContext().getSharedPreferences(SPLANG, Context.MODE_PRIVATE);

        String stats = sharedpreferences.getString(SPLANG, "");
        String sizeToOff = sharedpreferences.getString(SIZETOOFF, "");
        //String isActive = sharedpreferences.getString(ISACTIVE, "");
        Boolean isActive = Boolean.valueOf(sharedpreferences.getString(ISACTIVE, ""));
        MainActivity.optionPairs = MainActivity.sharedpreferences.getString(OPTIONPAIRS, "");
        editor = sharedpreferences.edit();

        Log.d("asd", "stats=" + stats);
        Log.d("asd", "sizeToOff=" + sizeToOff);

//        if (stats.isEmpty()) {
//            Log.d("asd", "Saving data to Files");
//            editor.putString(MainActivity.SPLANG, this.getStraffStart());
//            editor.putString(MainActivity.SIZETOOFF, "50");//kb
//        }


        Switch mySwitch = (Switch) findViewById(R.id.switch1);

        mySwitch.setChecked(isActive);

        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
             public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("Switch State=", "" + isChecked);
                MainActivity.editor.putString(MainActivity.ISACTIVE, String.valueOf(isChecked));
                MainActivity.editor.commit();
                if (isChecked) {
                    Toast.makeText(getApplicationContext(), getString(R.string.ActivationSerice), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.DisActivationSerice), Toast.LENGTH_LONG).show();
                }
            }

        });
//        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                Log.d("asdd", "switch Checheckd!");
//            }
//        });

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

        SharedPreferences prefs = PreferenceManager
                //.getSharedPreferences(SPLANG, Context.MODE_PRIVATE);
                .getDefaultSharedPreferences(getBaseContext());
        CheckboxPreference = prefs.getBoolean("checkboxPref", Boolean.parseBoolean(getOptionValue("displayOn")));


        //ListPreference = prefs.getString("listPref", "nr1");
        editTextPreference = prefs.getString("SizeToOff", (getOptionValue("SizeToOff")));

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
        Intent i = new Intent(this, PrefActivity.class);
        startActivity(i);
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//
//
//        //getMenuInflater().inflate(R.menu.main, menu);
//        //return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            showSettings();
        }
//        Intent i = new Intent(this, PrefActivity.class);
//        startActivity(i);
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
            View rootView = inflater.inflate(R.layout.activity_main, container,
                    false);
            return rootView;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        //MenuItem mi = menu.add(0, 1, 0, "Setting");
        //mi.setIntent(new Intent(this, PrefActivity.class));

        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

}

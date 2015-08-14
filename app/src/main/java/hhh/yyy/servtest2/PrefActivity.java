package hhh.yyy.servtest2;

import android.content.Context;
import android.location.GpsStatus;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.util.Log;
import android.widget.Toast;

import cronService.BackgroundService;

public class PrefActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    private static String optionPairs;
    public SharedPreferences sharedpreferences;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get Preferenece localisation
        sharedpreferences = getBaseContext().getSharedPreferences(MainActivity.SPLANG, Context.MODE_PRIVATE);

        String stats = sharedpreferences.getString(MainActivity.SPLANG, "");
        String isActive = sharedpreferences.getString(MainActivity.ISACTIVE, "");
        String optionPairsFromFile = sharedpreferences.getString(MainActivity.OPTIONPAIRS, "");
        optionPairs = sharedpreferences.getString(MainActivity.OPTIONPAIRS, "");

        if(optionPairs == ""){
            optionPairs = BackgroundService.baseOptionPairs;
            Log.d("asdd", "optionPairs is= '' .so.... Set Base Config!");
        }

        Log.d("asdd", "stats SAVED optionPairs:" + optionPairs);

        Log.d("asd", "stats from Service=" + isActive);
        Log.d("asd", "stats from bluetooth=" + getOptionValue("bluetooth"));
        Log.d("asd", "stats from mobileInet=" + getOptionValue("mobileInet"));



        PreferenceScreen rootScreen = getPreferenceManager().createPreferenceScreen(this);
        setPreferenceScreen(rootScreen);

        SwitchPreference swch1 = new SwitchPreference(this);
        swch1.setKey("bootOn");
        swch1.setSummaryOn("Start Service after reboot? Yes..(Recomended)");
        swch1.setSummaryOff("Start Service after reboot? No..(Not Recomended");
        swch1.setSwitchTextOff("No");
        swch1.setSwitchTextOn("Yes");
        swch1.setTitle("Service after reboot");
        swch1.setChecked(Boolean.parseBoolean(isActive));
        rootScreen.addPreference(swch1);

        SwitchPreference swch2 = new SwitchPreference(this);
        swch2.setKey("wifi");
        swch2.setTitle("Use wifi");
        swch2.setSummaryOn("Do service use Wifi? Yes..");
        swch2.setSummaryOff("Do service use Wifi? No..");
        swch2.setSwitchTextOff("No");
        swch2.setSwitchTextOn("Yes");
        swch2.setChecked(Boolean.parseBoolean(getOptionValue("wifi")));
        rootScreen.addPreference(swch2);

        SwitchPreference swch3 = new SwitchPreference(this);
        swch3.setKey("bluetooth");
        swch3.setTitle("Use Bluetooth");
        swch3.setSummaryOn("Do service use Bluetooth? Yes..");
        swch3.setSummaryOff("Do service use Bluetooth? No..");
        swch3.setSwitchTextOff("No");
        swch3.setSwitchTextOn("Yes");
        swch3.setChecked(Boolean.parseBoolean(getOptionValue("bluetooth")));
        rootScreen.addPreference(swch3);


        SwitchPreference swch4 = new SwitchPreference(this);
        swch4.setKey("mobileInet");
        swch4.setTitle("Use 2G/3G/4g internet");
        swch4.setSummaryOn("Do service use Mobile Internet? Yes..");
        swch4.setSummaryOff("Do service use Mobile Internet? No..");
        swch4.setSwitchTextOff("No");
        swch4.setSwitchTextOn("Yes");
        swch4.setChecked(Boolean.parseBoolean(getOptionValue("mobileInet")));
        rootScreen.addPreference(swch4);

//        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(this);
//        screen.setKey("screen");
//        screen.setTitle("Screen");
//        screen.setSummary("Description of screen");


//        CheckBoxPreference chb2 = new CheckBoxPreference(this);
//        chb2.setKey("displayOn");
//        chb2.setTitle("Skip service when display On 2");
//        chb2.setSummary("This preference can be true or false");
//        chb2.setDefaultValue(Boolean.parseBoolean(getOptionValue("displayOn")));
//        rootScreen.addPreference(chb2);


//        PreferenceCategory categ2 = new PreferenceCategory(this);
//        categ2.setKey("categ2");
//        categ2.setTitle("Advanced users only options");
//        categ2.setSummary("Advanced users only options");
//        rootScreen.addPreference(categ2);
//        screen.addPreference(categ2);

        EditTextPreference chb5 = new EditTextPreference(this);
        chb5.setKey("SizeToOff");
        chb5.setTitle("(Advanced User ONLY!) Traffic checking amount");
        chb5.setSummary("Set personal traffic of checking (per min)");
        chb5.setDialogTitle("Please set kilobytes from 30 to 1024 (carefully it cause of different behaviour of service):");
        chb5.setDefaultValue(getOptionValue("SizeToOff"));

        rootScreen.addPreference(chb5);

        //rootScreen.addPreference(screen);


        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        preferences.registerOnSharedPreferenceChangeListener(this);

        SharedPreferences.OnSharedPreferenceChangeListener prefListener =
                new SharedPreferences.OnSharedPreferenceChangeListener() {
                    public void onSharedPreferenceChanged(SharedPreferences prefs,
                                                          String key) {
                    }
                };
        preferences.registerOnSharedPreferenceChangeListener(prefListener);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {


        Log.d("asdd", "Pref=" + prefs.getAll() + "; key=" + key);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        boolean valid = true;

        switch (key) {
            case "bootOn":
                boolean boot = prefs.getBoolean("bootOn", true);
                editor.putString(MainActivity.ISACTIVE, String.valueOf(boot));
                break;
            case "SizeToOff":
                String sizeoff = prefs.getString("SizeToOff", "");
                Log.d("asdd", "sizeoff="+sizeoff);
                //Integer.parseInt(sizeoff);

                if(!sizeoff.matches("[0-9+]{1,5}")){
                    Toast.makeText(this, "Information Not Saved! Please set correct number", Toast.LENGTH_LONG).show();
                    Log.d("asdd", "WRONG!!");
                    //return false;
                    valid = false;
                    this.finish();
                }
                break;

            default:
                break;
        }

        String newData = prefs.getAll().toString().replaceAll(",",";").replace("{", "").replace(" ","").replace("}", "");
        if(valid){
            editor.putString(MainActivity.OPTIONPAIRS, newData);
            Log.d("asdd", newData);
            editor.commit();
            Toast.makeText(this, "Information are saved!", Toast.LENGTH_SHORT).show();
        }

    }

    public String getOptionValue(String keyOption){
        if(PrefActivity.optionPairs != null) {
            String[] kvPairs = PrefActivity.optionPairs.split(";");
            for (String kvPair : kvPairs) {
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

}
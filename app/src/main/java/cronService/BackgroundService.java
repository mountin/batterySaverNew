package cronService;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import hhh.yyy.servtest2.MainActivity;
import hhh.yyy.servtest2.MyApp;
import hhh.yyy.servtest2.ServiceExample;


public class BackgroundService extends Service {

    private boolean isRunning;
    private Context context;
    private Thread backgroundThread;
    public static long SizeToOff = 25;//kb
    static public long straffStart; //all traff from start app
    public static TrafficStats stats = new TrafficStats();

    public static String[] optionPairs = "key1=value1;key2=value2;key3=value3".split(";");

    static public void setStraffStart() {
        straffStart = (long) (stats.getTotalRxBytes() / 1024);
        Log.d("sample", "WE SETT straffStart is " + straffStart);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d("asd", "onCreate()!!!");
        this.context = this;
        this.isRunning = false;
        this.backgroundThread = new Thread(myTask);
    }

    public static long getCurrentTraff() {

        return (stats.getTotalRxBytes() / 1024);
    }

    public void setOffAll() {
        Log.d("asd", "setOffAll()");
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        try {
            Log.d("asd", "getting activeNetwork");
            BluetoothAdapter bluemm = BluetoothAdapter.getDefaultAdapter();

            WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            Log.d("asd", " is WIFI: " + wifi);

            if (wifi.isWifiEnabled()) {
                Log.d("asd", "setting WIFI OFF...");
                wifi.setWifiEnabled(false);
            } else {
                Log.d("asd", "WIFI is DIsabled!!!");
            }
            if (bluemm.isEnabled()) {
                bluemm.disable();
            }
            if (activeNetwork != null) {
                updateAPN(this, false);
            } else {
                Log.d("asd", "Network is NOT connected! ");
            }

        }  catch (Exception e) {
            Log.d("asd", e.toString());
            // TODO: handle exception
        }
    }

//    public void SetUnActiveNetwork(NetworkInfo activeNetwork) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
//
//        boolean isMobile = activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
//        //if(isMobile)
//        {
//            //updateAPN(this, false);
//        }
//
//    }

    public static void updateAPN(Context paramContext, boolean enable) {
        try {
            Log.d("asd", "Mobile GOING TO BE OFF.....");
            ConnectivityManager connectivityManager = (ConnectivityManager) paramContext.getSystemService("connectivity");
            Method setMobileDataEnabledMethod = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(false);
            setMobileDataEnabledMethod.invoke(connectivityManager, enable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setMobileDataEnabled(Context context, boolean enabled) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final Class conmanClass = Class.forName(conman.getClass().getName());
        final Field connectivityManagerField = conmanClass.getDeclaredField("mService");
        connectivityManagerField.setAccessible(true);
        final Object connectivityManager = connectivityManagerField.get(conman);
        final Class connectivityManagerClass = Class.forName(connectivityManager.getClass().getName());
        final Method setMobileDataEnabledMethod = connectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
        setMobileDataEnabledMethod.setAccessible(true);

        setMobileDataEnabledMethod.invoke(connectivityManager, enabled);
    }

    public void checkTraff() {

        String mesg = "";
        if ((getCurrentTraff() - straffStart) < SizeToOff) {
            setOffAll();
            setStraffStart();
            mesg = "So...Data turned OFF... straffStart = " + straffStart;
        } else {
            mesg = "So...Data Continue Work...";
        }

        Log.d("asd1", "was - " + straffStart + "- all traf " + getCurrentTraff() + "  . Now Traffic is " + (getCurrentTraff() - straffStart) + "Kb. / last 15 sec. " + mesg);
        setStraffStart();
    }

    private Runnable myTask = new Runnable() {

        public void run() {
            Log.d("asd", "This is new Runnable() work!!!");

            //Get Preferenece localisation
            SharedPreferences sharedpreferences = getBaseContext().getSharedPreferences(MainActivity.SPLANG, Context.MODE_PRIVATE);
            String stats = sharedpreferences.getString(MainActivity.SPLANG, "");
            String sizeToOff = sharedpreferences.getString(MainActivity.SIZETOOFF, "");
            String isActive = sharedpreferences.getString(MainActivity.ISACTIVE, "");
            String optionPairsFromFile = sharedpreferences.getString(MainActivity.OPTIONPAIRS, "");

            //BackgroundService.optionPairs = optionPairsFromFile.split(";");

            if(optionPairsFromFile != null) {
                for (String kvPair : optionPairs) {
                    String[] kv = kvPair.split("=");
                    String key = kv[0];
                    String value = kv[1];

                    if (key.equals("bluetooth")) {

                    }
                    if (key.equals("wifi")) {

                    }
                    if (key.equals("mobile")) {

                    }
                    if (key.equals("displayOn")) {

                    }
                }
            }

            Log.d("asd", "stats from Service=" + stats);
            Log.d("asd", "sizeToOff from Service=" + sizeToOff);

            System.out.println("Run servuce!!!");
            checkTraff();

            stopSelf();
        }

//        public void setStraffStart() {
//            straffStart = (long) (stats.getTotalRxBytes() / 1024);
//            Log.d("sample", "WE SETT straffStart is " + straffStart);
//        }
    };

    @Override
    public void onDestroy() {
        this.isRunning = false;
        Log.d("asd", "This is new onDestroy() Stopped service!!!");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("asd", "onStartCommand!!!");
        if (!this.isRunning) {
            this.isRunning = true;
            this.backgroundThread.start();
        }
        return START_STICKY;
    }

}
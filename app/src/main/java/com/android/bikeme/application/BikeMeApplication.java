package com.android.bikeme.application;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.multidex.MultiDexApplication;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.widget.ImageView;
import com.android.bikeme.R;
import com.mapbox.mapboxsdk.Mapbox;
import com.squareup.picasso.Picasso;
import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Date;

/**
 * Created by Daniel on 22/04/2017.
 */
public class BikeMeApplication extends MultiDexApplication {

    private static final String BIKE_ME_PREFERENCES_KEY = "BikeMePreferences";
    public static final String DATE_DIFFERENCE = "dateDifference";
    public static final String MAP_OFFLINE_DOWNLOADED = "MapOfflineDownloaded";

    private static BikeMeApplication bikeMeApplication;

    public static synchronized BikeMeApplication getInstance()
    {
        return bikeMeApplication;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        Mapbox.getInstance(getApplicationContext(), getString(R.string.access_token_map_box));
        bikeMeApplication = this;
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean isOnlineWifi()
    {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI);
    }

    public boolean isOnline()
    {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public String getStringDistance(double distanceInMeter)
    {
        if(distanceInMeter >= 1000)
        {
            return  ((double)Math.round((distanceInMeter/1000)*10)/10.0)+" Km";
        }
        else
        {
            return  Math.round(distanceInMeter)+" m";
        }
    }

    public String encodeBitmapToString(Bitmap bitmap)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return  Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
    }

    public Bitmap decodeStringToBitmap(String imageEncode)
    {
        byte[] encodeByte = Base64.decode(imageEncode, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // used for search routes for date created
    public String getDateString(Date date)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(date);
    }

    //used for save ratings, guest and user date  and hour updated
    public String getDateTimeString(Date date)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.getDefault());
        return dateFormat.format(date);
    }

    public Date getDateTime(String date)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Date dateReturn = null;

        try {
            dateReturn = dateFormat.parse(date);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }

        return dateReturn;
    }

    public String getLongDate(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)-2;
        if(dayOfWeek == -1)
        {
            dayOfWeek=6;
        }
        String longDate = getResources().getStringArray(R.array.days_of_week)[dayOfWeek]+", ";
        longDate += calendar.get(Calendar.DAY_OF_MONTH) + " de ";
        longDate += getResources().getStringArray(R.array.months_of_year)[calendar.get(Calendar.MONTH)] + " ";
        longDate += calendar.get(Calendar.YEAR);
        return  longDate;
    }

    public String getHourAmPm(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String hour;
        hour =  (calendar.get(Calendar.HOUR) == 0 ? "12" : calendar.get(Calendar.HOUR))+":";
        hour += (calendar.get(Calendar.MINUTE) < 10 ? "0" : "") +calendar.get(Calendar.MINUTE)+" ";
        hour += (calendar.get(Calendar.AM_PM) == 0 ? "AM" : "PM");
        return hour;
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void loadImage(Uri uri, ImageView imageView, Drawable defaultImage)
    {
        Picasso.with(this).load(uri).placeholder(defaultImage).error(defaultImage).into(imageView);
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public SharedPreferences getBikeMeSharedPreferences()
    {
        return getSharedPreferences(BIKE_ME_PREFERENCES_KEY,Context.MODE_PRIVATE);
    }

    public void savePrefDifferenceLocalServerDate(String serverDateString)
    {
        Date serverDate = getDateTime(serverDateString);
        Date currentDate = new Date();
        SharedPreferences prefs = getBikeMeSharedPreferences();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(DATE_DIFFERENCE, serverDate.getTime()-currentDate.getTime());
        editor.apply();
    }

    public Date getCurrentDate()
    {
        Date currentDate = new Date();
        SharedPreferences prefs = getSharedPreferences(BIKE_ME_PREFERENCES_KEY,Context.MODE_PRIVATE);
        currentDate.setTime(currentDate.getTime()+prefs.getLong(DATE_DIFFERENCE,0));
        return currentDate;
    }

    public void savePrefMapOfflineDownloaded()
    {
        SharedPreferences prefs = getBikeMeSharedPreferences();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(MAP_OFFLINE_DOWNLOADED, true);
        editor.apply();
    }

    public boolean isMapOfflineDownloaded()
    {
       return  getBikeMeSharedPreferences().getBoolean(BikeMeApplication.MAP_OFFLINE_DOWNLOADED,false);
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean isMyServiceRunning(Class<?> serviceClass)
    {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {

            if (serviceClass.getName().equals(service.service.getClassName()))
            {
                return true;
            }
        }
        return false;
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public Drawable getDrawable(String name)
    {
        String uri = "@drawable/"+name;
        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
        return ContextCompat.getDrawable(this, imageResource);
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public ProgressDialog getProgressDialog(Context context)
    {
        ProgressDialog mProgressDialog = new ProgressDialog(context,R.style.DialogThemeLoading);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);

        return mProgressDialog;
    }

    public void hideProgressDialog(ProgressDialog mProgressDialog)
    {
        if (mProgressDialog != null && mProgressDialog.isShowing())
        {
            mProgressDialog.dismiss();
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void registerReceiverFromManifest(Class<? extends BroadcastReceiver> broadcastClass)
    {
        ComponentName component = new ComponentName(getApplicationContext(), broadcastClass);
        getPackageManager()
                .setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);

    }

    public void unregisterReceiverFromManifest(Class<? extends BroadcastReceiver> broadcastClass)
    {
        ComponentName component = new ComponentName(getApplicationContext(), broadcastClass);
        getPackageManager().setComponentEnabledSetting(component,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);

    }
}

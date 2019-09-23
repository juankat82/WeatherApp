package com.juan.weatherapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.juan.weatherapp.com.juan.controller.DataFlowController;
import com.juan.weatherapp.com.juan.controller.MyWeatherAsyncTask;
import com.juan.weatherapp.com.juan.controller.SingletonClass;
import com.juan.weatherapp.com.juan.database.WeatherOpenHelper;
import com.juan.weatherapp.com.juan.model.Weather;
import java.util.concurrent.ExecutionException;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/*
 *   Weather app in an app that shows the weather in the current location
 *   @author juan
 *   @version 1.0
 *   @since 02-04-2019
 *  MainActivity is the main activity of the app.
 */
public class MainActivity extends AppCompatActivity {

    private static final String OPEN_WEATHERMAP_API_KEY="";
    private static final int GPS_REQUEST_CODE=33;
    private static final int PERMISSIONS_REQUEST_CODE=55;
    private SQLiteDatabase mWeatherDatabase;
    private Weather mWeather;
    private SharedPreferences mSharedPreferences;
    private FloatingActionButton mFloatingActionButton;
    private TextView mConditionTextView;
    private TextView mTemperatureTextView;
    private TextView mWindSpeedTextView;
    private TextView mWindDirectionTextView;
    private ImageView mConditionImageView;
    private ProgressBar mProgressCircle;
    private TextView mLoadingTextView;
    private TextView mMessageTextView;
    private LocationManager mLocationManager;
    private Intent gpsIntent;
    private boolean isGpsEnabled=false;
    private boolean choseOffMode=false;
    private String[] permissions={ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private SingletonClass mSingletonClass;
    private DataFlowController mDataFlowController;


    /////////////////////////////////   MAIN APP's LOGIC AND METHOD OVERRIDING   ///////////////////////////////////////////
    /**
     * This method returns an instance of MainActivity class
     *
     * @return MainActivity This returns an instance of the MainActivity class
     */
    public MainActivity getMe()
    {
        return MainActivity.this;
    }

    /**
     * This method is part of the lifecycle of the app where we initialize the objects and configure the app's elements
     * @param savedInstanceState is a copy of savedInstanceState used to restore any previous state of the app
     * @return nothing
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mWeatherDatabase=new WeatherOpenHelper(MainActivity.this).getWritableDatabase();
        mSharedPreferences=getApplicationContext().getSharedPreferences("com.juan.weatherapp",Context.MODE_PRIVATE);
        mFloatingActionButton=findViewById(R.id.floatingActionButton);
        mConditionTextView=findViewById(R.id.condition_textview);
        mTemperatureTextView=findViewById(R.id.temperature_textview);
        mWindSpeedTextView=findViewById(R.id.wind_speed_textview);
        mWindDirectionTextView=findViewById(R.id.wind_direction_textview);
        mConditionImageView=findViewById(R.id.condition_image_view);
        mProgressCircle=findViewById(R.id.progress_circle);
        mLoadingTextView=findViewById(R.id.loading_text_view);
        mMessageTextView=findViewById(R.id.message_text_view);
        mLocationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mSingletonClass=SingletonClass.getSingletClass(this);
        mDataFlowController=new DataFlowController(mWeatherDatabase,mSharedPreferences);

        //checks google service is available
         if (!mSingletonClass.isGooglePlayAvailable(this))
         {
             mMessageTextView.setVisibility(View.VISIBLE);//turn visibility OFF when hitting the RELOAD button
             mMessageTextView.setText(R.string.play_services_unavailable);
         }

        //implementing FloatingActionButton
          mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  //If gps isnt active, request to turn it on then get location and perform weather request, otherwise, just request it
                  isGpsEnabled=mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                  mMessageTextView.setVisibility(View.GONE);

                    if (!isGpsEnabled)
                    {
                        if (choseOffMode)
                        {
                            mMessageTextView.setText(R.string.you_need_gps);
                            mMessageTextView.setVisibility(View.VISIBLE);
                            setOffLineSystem(mWeather=mDataFlowController.getWeatherFromSharedPreferences());
                        }
                        else//we turned off gps while app was on
                        {
                            mMessageTextView.setVisibility(View.GONE);
                            enableGps();
                            getLocation();
                        }
                    }
                    else
                    {
                        mMessageTextView.setVisibility(View.GONE);
                        getLocation();
                    }
              }
          });
    }

    /**
     * Executed right after onCreate() as part of the app's lifecycle.
     * Permission request and check the state of the Gps (On/Off)
     *
     */
    @Override
    public void onStart()
    {
        super.onStart();
        int permission= ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION);
        isGpsEnabled=mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (permission== PackageManager.PERMISSION_GRANTED)
        {
            if (!isGpsEnabled)
                enableGps();
            else
            {
                choseOffMode=false;//indicate THE USER CHOSE TO HAVE THE GPS ON
                getLocation();
            }
        }
        else
        {
            ActivityCompat.requestPermissions(MainActivity.this,permissions,PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Requesting permissions will lead your here. Actions are taken depending on permissions granted.
     *
     * @param requestCode int This identifies an action by an ID.
     * @param permissions String[] This returns a list of permissions requested.
     * @param grantResults int[] This returns a list of granted rights, one per permission requested.
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if (requestCode==PERMISSIONS_REQUEST_CODE)
        {
            if (grantResults[0]!=PackageManager.PERMISSION_GRANTED)
            {
                mMessageTextView.setVisibility(View.VISIBLE);//turn visibility OFF when hitting the RELOAD button
                mMessageTextView.setText(R.string.permissions_not_granted);
            }
            else
            {
                if (mMessageTextView.getVisibility()==View.VISIBLE)
                    mMessageTextView.setVisibility(View.GONE);

                if (isGpsEnabled)
                    getLocation();
                else
                    enableGps();
            }
        }
    }

    /**
     *  This is used to perform actions based on an Intent.
     *
     * @param requestCode requestCode This identifies an user attempting to execute an action (firing the GPS in this case)
     * @param resultCode resultCode This contains the answer to either we accept waking up the GPS or we don't.
     * @param data Intent This hold an attempt to manually make the user activate the gps.
     *
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode==GPS_REQUEST_CODE)
        {
            if (resultCode== Activity.RESULT_OK)
            {
                isGpsEnabled=true;
            }
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////   VIEW MANAGING //////////////////////////////////////////////////////////////////////////////
    /**
     * This method sets the TextViews and ImageView with the weather update.
     * Only handles the set-up in off-line mode, using the SharedPreferences
     * or the integrated (but disabled, with chance of getting enabled) database.
     *
     * @param weather Weather This is a snapshot of the weather that will be presented on UI
     *
     */
    public void setOffLineSystem(Weather weather)
    {
        float differenceOfDays;
        SingletonClass singletonClass=SingletonClass.getSingletClass(this);
        //if the data stores in sharedpreferences or database is older than a day or theres no data, just say theres no data available
        if (weather==null) //if simply theres no data...
        {
            mConditionTextView.setText(R.string.no_data_text);
            mTemperatureTextView.setText(R.string.no_data_text);
            mWindSpeedTextView.setText(R.string.no_data_text);
            mWindDirectionTextView.setText(R.string.no_data_text);
            mConditionImageView.setImageResource(R.drawable.not_vailable);
        }
        else
        {
            differenceOfDays=singletonClass.getDifferenceOfDays(weather.getmTimeStamp());
            if (differenceOfDays>=1.0)//Amount of days
            {
                mConditionTextView.setText(R.string.no_data_text);
                mTemperatureTextView.setText(R.string.no_data_text);
                mWindSpeedTextView.setText(R.string.no_data_text);
                mWindDirectionTextView.setText(R.string.no_data_text);
                mConditionImageView.setImageResource(R.drawable.not_vailable);
            }
            else
            {
                mMessageTextView.setVisibility(View.VISIBLE);
                mMessageTextView.setText(R.string.you_need_gps);
                mConditionTextView.setText(weather.getmWeatherCondition());
                mTemperatureTextView.setText(String.format("%s",weather.getTemperature())+"°C");
                mWindSpeedTextView.setText(String.format("%s",Math.round(weather.getWindSpeed()))+" Mph");
                mWindDirectionTextView.setText(weather.getWindDirection());
                mConditionImageView.setImageResource(singletonClass.getImage(weather.getIconName()));
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////  GPS MANAGING //////////////////////////////////////////////////////////////////////////////////
    /**
     * This method asks the user to enable the Gps for online mode or to use the offline mode instead.
     *
     *
     */
    public void enableGps()
    {
        if(!isGpsEnabled)
        {
            new AlertDialog.Builder(MainActivity.this).setTitle(R.string.start_gps_question_title).setMessage(R.string.get_gps_enabled_string)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            gpsIntent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(gpsIntent, GPS_REQUEST_CODE);
                            isGpsEnabled=true;
                            choseOffMode=false;
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new AlertDialog.Builder(MainActivity.this).setTitle(R.string.try_offline_mode_string).setMessage(R.string.are_you_sure_string)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            choseOffMode=true;

                                            //set using sharedpreferences
                                            setOffLineSystem(mWeather=mDataFlowController.getWeatherFromSharedPreferences());

                                            //set using database
                                            //setOffLineSystem(mWeather=mDataFlowController.readFromDatabase(null,null));
                                            mMessageTextView.setVisibility(View.VISIBLE);
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            enableGps();
                                            mMessageTextView.setVisibility(View.GONE);
                                            choseOffMode=false;
                                        }
                                    }).create().show();
                        }

                    }).create().show();
        }
    }
    /**
     * This method gets the location, parse a JSON answer and store it for using with offline mode (if required)
     *
     */
    public void getLocation()
    {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(MainActivity.this,ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED)
        {
            mFusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);

            locationCallback=new LocationCallback(){
                @Override
                public void onLocationResult(LocationResult locationResult)
                {
                    Location location=locationResult.getLocations().get(locationResult.getLocations().size()-1);//gets last location
                    float latitude=(float)location.getLatitude();
                    float longitude=(float)location.getLongitude();

                    String stringToSend="https://api.openweathermap.org/data/2.5/weather?lat="+latitude+"&lon="+longitude+"&cnt=10&APPID="+OPEN_WEATHERMAP_API_KEY;

                    try {
                        choseOffMode=false;
                        mWeather=new MyWeatherAsyncTask(MainActivity.this,MainActivity.this,mProgressCircle,mLoadingTextView).execute(stringToSend).get();

                        mConditionTextView.setText(mWeather.getmWeatherCondition());
                        Log.i("MainActivity","WeatherCondition: "+mWeather.getmWeatherCondition());
                        mTemperatureTextView.setText(String.valueOf(Math.round(mWeather.getTemperature()))+"℃");
                        mWindSpeedTextView.setText(String.valueOf(Math.round(mWeather.getWindSpeed()))+" Mph");
                        mWindDirectionTextView.setText(mWeather.getWindDirection());
                        mConditionImageView.setImageResource(mSingletonClass.getImage(mWeather.getIconName()));

                        //write to sharedpreferences
                        mDataFlowController.writeSharedPreferences(mWeather);

                        //write to database
                        //mDataFlowController.writeToDatabase(mWeather);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            locationRequest=new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setNumUpdates(1).setInterval(2000);
            mFusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,null);
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}

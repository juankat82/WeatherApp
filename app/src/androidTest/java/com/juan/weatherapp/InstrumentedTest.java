package com.juan.weatherapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.juan.weatherapp.com.juan.controller.DataFlowController;
import com.juan.weatherapp.com.juan.controller.MyWeatherAsyncTask;
import com.juan.weatherapp.com.juan.controller.SingletonClass;
import com.juan.weatherapp.com.juan.database.WeatherOpenHelper;
import com.juan.weatherapp.com.juan.model.Weather;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class InstrumentedTest {

    private Weather weather=new Weather();
    private Context context;
    private SQLiteDatabase mWeatherDatabase;
    private SharedPreferences mSharedPreferences;
    private DataFlowController mDataFlowController;

    //initializes weather so we can use it with our tests
    @Before
    public void fillWeather()
    {
        weather=new Weather();
        weather.setmTimeStamp(new Date().getTime());
        weather.setmLocation("London");
        weather.setmLatitude(51.509865f);
        weather.setmLongitude(-0.118092f);
        weather.setWindDirection("South South-East");
        weather.setWindSpeed(12f);
        weather.setTemperature(20f);
        weather.setmWeatherCondition("Sunny");
        weather.setIconName("i01d");

        context=InstrumentationRegistry.getTargetContext();
        mWeatherDatabase=new WeatherOpenHelper(context).getWritableDatabase();
        mSharedPreferences=context.getSharedPreferences("com.juan.weatherapp",Context.MODE_PRIVATE);
        mDataFlowController=new DataFlowController(mWeatherDatabase,mSharedPreferences);
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.juan.weatherapp", appContext.getPackageName());

    }


    //tests the httpconnection that fetches the json code for the asynctask to parse it
    @Test
    public void test_httpConnection() throws IOException
    {
        assertNotNull(new MyWeatherAsyncTask().getUrlString("https://api.openweathermap.org/data/2.5/weather?lat=51.4284364&lon=-0.1905934&APPID=e41df8dbfc20e774eaa0bf2bbd632e71"));
    }

    //tests the asynctask code
    @Test
    public void test_asynctask() throws ExecutionException,InterruptedException
    {
        Context appContext=InstrumentationRegistry.getTargetContext();
        ProgressBar pB=new ProgressBar(appContext);
        TextView tV=new TextView(appContext);
        String url="https://api.openweathermap.org/data/2.5/weather?lat=51.4284364&lon=-0.1905934&APPID=e41df8dbfc20e774eaa0bf2bbd632e71";
        Weather weather=new MyWeatherAsyncTask(appContext,new MainActivity(),pB,tV).execute(url).get();
        assertNotNull(weather);
    }

    //Tests google play API is available
    @Test
    public void isGooglePlayAvailable_Test()
    {

        boolean answer=false;
        GoogleApiAvailability availability=GoogleApiAvailability.getInstance();
        int result=availability.isGooglePlayServicesAvailable(context);
        answer = result == ConnectionResult.SUCCESS;
        assertTrue("Google play API is installed",answer);
    }

    //Tests reading from database
    @Test
    public void database_read_test()
    {
        mDataFlowController.readFromDatabase(null,null);
    }
    //Tests writting to database
    @Test
    public void database_write_test()
    {
        mDataFlowController.writeToDatabase(weather);
    }
    //Tests reading from sharedPreferences
    @Test
    public void sharedpreferences_read_test()
    {
        mDataFlowController.getWeatherFromSharedPreferences();
    }
    //Tests writting to sharedPreferences
    @Test
    public void sharedpreferences_write_test()
    {
        mDataFlowController.writeSharedPreferences(weather);
    }
////////////////////////////////////////////   THIS WONT WORK AS THESE METHODS AREN'T DESIGNED TO BE TESTED BUT ON RUNTIME  ////////////
//    //tests MainActivity's getLocation() method
//    @Test
//    public void location_test()
//    {
//        new MainActivity().getLocation();
//    }
//
//    //tests MainActivity's enableGps() method
//    @Test
//    public void test_enable_gps()
//    {
//        new MainActivity().enableGps();
//    }
//
//    //tests deployment of Weather fetched by the asynctask on screen
//    @Test
//    public void test_deployment()
//    {
//        //It works when attempting to deploy data (acceses the method) but as it lacks the references to the initialized TextViews
//        //It throws nullpointerexception on those
//        Context appContext=InstrumentationRegistry.getTargetContext();
//        SingletonClass singletonClass=SingletonClass.getSingletClass(appContext);
//
//        Weather weather=new Weather();
//        weather.setmTimeStamp(new Date().getTime());
//        weather.setmLocation("London");
//        weather.setmLatitude(51.509865f);
//        weather.setmLongitude(-0.118092f);
//        weather.setWindDirection("South South-East");
//        weather.setWindSpeed(12f);
//        weather.setTemperature(20f);
//        weather.setmWeatherCondition("Sunny");
//        weather.setIconName("i01d");
//
//        MainActivity mA=new MainActivity().getMe();
//        mA.setOffLineSystem(weather);
//    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}

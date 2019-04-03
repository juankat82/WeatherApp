package com.juan.weatherapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.juan.weatherapp.com.juan.controller.MyWeatherAsyncTask;
import com.juan.weatherapp.com.juan.controller.SingletonClass;
import com.juan.weatherapp.com.juan.model.Weather;

import org.junit.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class BaseUnitTests {

    //tests the kelvin to celsius converter
    @Test
    public void kelvinToCelsius_Converter_test()
    {
        int num=(int)new SingletonClass().kelvinToCelsius(300);
        assertEquals("It matches",26,num);
    }

    //tests wind direction
    @Test
    public void wind_direction_test()
    {
        String direction=new SingletonClass().getWindDirection(85);
        assertEquals("It matches","East",direction);
    }

    //tests the difference of days method (returns the difference of days between NOW and a given timestamp
    @Test
    public void differenceOfDays_Test()
    {
        assertNotNull(new SingletonClass().getDifferenceOfDays(1554125400));
    }

    //tests the method getImage (required to get an image with the weather condition), just against ONE SINGLE image
    @Test
    public void getImage_test()
    {
        assertNotSame(R.drawable.i01d,new SingletonClass().getImage("i01d"));
    }
}
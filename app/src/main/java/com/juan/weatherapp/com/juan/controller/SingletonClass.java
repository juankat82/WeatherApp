package com.juan.weatherapp.com.juan.controller;

import android.content.Context;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.juan.weatherapp.R;
import java.util.Date;

/**
 * This class implements several methods for convenience and better code organization.
 */
public class SingletonClass {

    private Context mContext;

    /**This method is the default constructor, only used for instantiation and running tests.
     */
    public SingletonClass(){}   //THIS CONSTRUCTOR EXISTS JUST TO MAKE TESTS EASIER

    /**This method is an overloaded constructor, only used for instantiation and running tests.
    * @param context Context initializes an instance. Only for internal use. It's private.
     */
    private SingletonClass(Context context)
    {
        mContext=context;
    }

    /**This static method is used to return a singleton of the class.
     *
     * @param context Context This will initialize an instance of this class given a context.
     * @return SingletonClass Returns an instance of this class.
     */
    public static SingletonClass getSingletClass(Context context)
    {
        return new SingletonClass(context);
    }

    /**This method is used to check Google play services availability
     *
     * @param context Context Initializing an instance of google play services, requires this context
     * @return boolean True if services are available, false if unavailable.
     */
    public boolean isGooglePlayAvailable(Context context)
    {
        GoogleApiAvailability availability=GoogleApiAvailability.getInstance();
        int result=availability.isGooglePlayServicesAvailable(context);
        if (result!= ConnectionResult.SUCCESS)
        {
             if(!availability.isUserResolvableError(result))
                 return false;
            return false;
        }
        else {
            return true;
        }
    }

    /**This method returns the difference of days between a timestamp and the current timestamp
     *
     * @param oldTimeStamp long This is the timestamp we want to compare with the current timestamp
     * @return float This is the amount of days between oldTimestamp and current timestamp
     */
    public float getDifferenceOfDays(long oldTimeStamp) {

        long newTimeStamp=new Date().getTime();
        long millis=24*60*60*1000;

        return (float) ((newTimeStamp/millis)-(oldTimeStamp/millis));
    }

    /**This method returns the id of a local resource in int type, given its very name in String.
     *
     * @param icon String is the name in String type
     * @return int This is the real int-type ID of the String-type resource.
     */
    public int getImage(String icon)
    {
        switch(icon)
        {
            case "i01d":
                return R.drawable.i01d;
            case "i01n":
                return R.drawable.i01n;
            case "i02d":
                return R.drawable.i02d;
            case "i02n":
                return R.drawable.i02n;
            case "i03d":
                return R.drawable.i03d;
            case "i03n":
                return R.drawable.i03n;
            case "i04d":
                return R.drawable.i04d;
            case "i04n":
                return R.drawable.i04n;
            case "i09d":
                return R.drawable.i09d;
            case "i09n":
                return R.drawable.i09n;
            case "i10d":
                return R.drawable.i10d;
            case "i10n":
                return R.drawable.i10n;
            case "i11d":
                return R.drawable.i11d;
            case "i11n":
                return R.drawable.i11n;
            case "i13d":
                return R.drawable.i13d;
            case "i13n":
                return R.drawable.i13n;
            case "i50d":
                return R.drawable.i50d;
            case "i50n":
                return R.drawable.i50n;
            default:
                return R.drawable.not_vailable;
        }
    }

    /**This method converts kelvin degrees to celsius.
     *
     * @param kelvin double This is kelvin degrees to convert.
     * @return double Kelvin degrees converted into Celsius.
     */
    public double kelvinToCelsius(double kelvin)
    {
        return kelvin-273.15;
    }

    /**This method returns the direction of the wind given a number of degrees.
     *
     * @param degrees This is the degrees to transform into a direction.
     * @return String This is the direction of the wind (N, S, W, E).
     */
    public String getWindDirection(int degrees)
    {
        String direction="";
        if ((degrees>=338 && degrees<=360) || (degrees>=0 && degrees<=22))
            direction="North";
        else
        {
            if(degrees>=23 && degrees<=67)
                direction="North East";
            else
            {
                if (degrees>=68 && degrees<=112)
                    direction="East";
                else
                {
                    if (degrees>=113 && degrees<=157)
                        direction="South East";
                    else
                    {
                        if (degrees>=158 && degrees<=202)
                            direction="South";
                        else
                        {
                            if (degrees>=203 && degrees<=247)
                                direction="South West";
                            else
                            {
                                if (degrees>=248 && degrees<=292)
                                    direction="West";
                                else
                                {
                                    if (degrees>=292 && degrees<=337)
                                        direction="North West";
                                    else
                                        direction="Invalid wind direction";
                                }
                            }
                        }
                    }
                }
            }
        }
        return direction;
    }
}

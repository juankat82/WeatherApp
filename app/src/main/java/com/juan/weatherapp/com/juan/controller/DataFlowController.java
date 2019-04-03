package com.juan.weatherapp.com.juan.controller;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.juan.weatherapp.com.juan.database.WeatherColumns;
import com.juan.weatherapp.com.juan.database.WeatherWrapper;
import com.juan.weatherapp.com.juan.model.Weather;


/**
 * This class is in charge of controlling the handling of local storage of the weather instances
 * for later usage.
 * It uses a local SQLite database and sharedpreferences.
 */
public class DataFlowController {

    private SQLiteDatabase mWeatherDatabase;
    private SharedPreferences mSharedPreferences;

    /**
     * This method is the constructor.
     *
     * @param weatherDataBase SQLiteDatabase Holds a copy of the database for storing weather snapshot for offline mode
     * @param sharedPreferences SharedPreferences Holds a copy of the sharedPreferences
     */
    public DataFlowController(SQLiteDatabase weatherDataBase, SharedPreferences sharedPreferences)
    {
        mWeatherDatabase=weatherDataBase;
        mSharedPreferences=sharedPreferences;
    }

    /**
     * This method is used to write weather snapshot to database
     * @param weather Weather is a snapshot of the weather
     */
    public void writeToDatabase(Weather weather)
    {
        if (weather!=null)
        {
            mWeatherDatabase.execSQL("DELETE FROM "+ WeatherColumns.TABLE);
            ContentValues cv=new ContentValues();
            cv.put("LOCATION",weather.getmLocation());
            cv.put("LATITUDE",weather.getmLatitude());
            cv.put("LONGITUDE",weather.getmLongitude());
            cv.put("TIMESTAMP",weather.getmTimeStamp());
            cv.put("WEATHER_CONDITION",weather.getmWeatherCondition());
            cv.put("ICON_NAME",weather.getIconName());
            cv.put("TEMPERATURE",weather.getTemperature());
            cv.put("WIND_SPEED",weather.getWindSpeed());
            cv.put("WIND_DIRECTION",weather.getWindDirection());
            mWeatherDatabase.insert(WeatherColumns.TABLE,null,cv);

        }

    }

    /**
     * This method is used for reading from Database and create a weather snapshot to present on the UI.
     *
     * @param whereClause String This is the WHERE clause for a database query (e.g.: "Select from mytable WHERE whereClause ="whereArgs") .
     * @param whereArgs String[] used as arguments for a WHERE for a database query (e.g.: "Select from mytable WHERE whereClause ="whereArgs").
     * @return Weather This returns a weather instance obtained from the database
     */
    public Weather readFromDatabase(String whereClause, String[] whereArgs)
    {
        Cursor cursor=mWeatherDatabase.query(WeatherColumns.TABLE,null,whereClause,whereArgs,null,null,null);
        WeatherWrapper wrapper=new WeatherWrapper(cursor);
        return wrapper.getWeatherData(cursor);
    }

    /**
     * This method is used to obtain a Weather snapshot stored in sharedpreferences
     * @return Weather This returns a weather instance obtained from the database
     */
    public Weather getWeatherFromSharedPreferences()
    {
        if (mSharedPreferences!=null) {
            String location = mSharedPreferences.getString("LOCATION", "");
            float latitude = mSharedPreferences.getFloat("LATITUDE", 0);
            float longitude = mSharedPreferences.getFloat("LONGITUDE", 0);
            long timeStamp = mSharedPreferences.getLong("TIMESTAMP", 0);
            String weatherCondition = mSharedPreferences.getString("WEATHER_CONDITION", "");
            String iconName = mSharedPreferences.getString("ICON_NAME", "");
            float temperature = mSharedPreferences.getFloat("TEMPERATURE", 0);
            float windSpeed = mSharedPreferences.getFloat("WIND_SPEED", 0);
            String windDirection = mSharedPreferences.getString("WIND_DIRECTION", "");

            Weather weather = new Weather();
            weather.setmLocation(location);
            weather.setmLatitude(latitude);
            weather.setmLongitude(longitude);
            weather.setIconName(iconName);
            weather.setmTimeStamp(timeStamp);
            weather.setmWeatherCondition(weatherCondition);
            weather.setTemperature(temperature);
            weather.setWindSpeed(windSpeed);
            weather.setWindDirection(windDirection);
            return weather;
        }
        else
            return null;

    }
    /**
     * This method is used for writing a weather instance into the sharedpreferences
     *
     * @param weather Weather This method takes a weather instance.
     */
    public void writeSharedPreferences(Weather weather)
    {
        if (weather!=null) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString("LOCATION", weather.getmLocation());
            editor.putFloat("LATITUDE", weather.getmLatitude());
            editor.putFloat("LONGITUDE", weather.getmLongitude());
            editor.putLong("TIMESTAMP", weather.getmTimeStamp());
            editor.putString("WEATHER_CONDITION", weather.getmWeatherCondition());
            editor.putString("ICON_NAME", weather.getIconName());
            editor.putFloat("TEMPERATURE", weather.getTemperature());
            editor.putFloat("WIND_SPEED", weather.getWindSpeed());
            editor.putString("WIND_DIRECTION", weather.getWindDirection());
            editor.apply();
        }
    }
}

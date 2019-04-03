package com.juan.weatherapp.com.juan.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import com.juan.weatherapp.com.juan.model.Weather;

/**
 * This class creates a CursorWrapper to handle Weather type data.
 */
public class WeatherWrapper extends CursorWrapper {

    private Cursor mCursor;
    private Weather mWeather;


    /**
     * Constructor
     * @param cursor The cursor to wrap. Required to call super().
     */
    public WeatherWrapper(Cursor cursor) {
        super(cursor);
    }

    /**Returns a Weather instance created from a Cursor.
     *
     * @param cursor Cursor Takes a cursor and extract its data to create a Weather.
     * @return Weather This is the Weather instance filled with the data extracted from the Cursor.
     */
    public Weather getWeatherData(Cursor cursor)
    {
        mCursor=cursor;
        mWeather=new Weather();
        if (mCursor==null || mCursor.getCount()==0)
        {
            return null;
        }
        else
        {
            try
            {
                mCursor.moveToFirst();
                mWeather.setmLocation(mCursor.getString(mCursor.getColumnIndex(WeatherColumns.Columns.LOCATION)));
                mWeather.setmLatitude(mCursor.getFloat(mCursor.getColumnIndex(WeatherColumns.Columns.LATITUDE)));
                mWeather.setmLongitude(mCursor.getFloat(mCursor.getColumnIndex(WeatherColumns.Columns.LONGITUDE)));
                mWeather.setmTimeStamp(mCursor.getLong(mCursor.getColumnIndex(WeatherColumns.Columns.TIMESTAMP)));
                mWeather.setmWeatherCondition(mCursor.getString(mCursor.getColumnIndex(WeatherColumns.Columns.WEATHER_CONDITION)));
                mWeather.setIconName(mCursor.getString(mCursor.getColumnIndex(WeatherColumns.Columns.ICON_NAME)));
                mWeather.setTemperature(mCursor.getInt(mCursor.getColumnIndex(WeatherColumns.Columns.TEMPERATURE)));
                mWeather.setWindSpeed(mCursor.getInt(mCursor.getColumnIndex(WeatherColumns.Columns.WIND_SPEED)));
                mWeather.setWindDirection(mCursor.getString(mCursor.getColumnIndex(WeatherColumns.Columns.WIND_DIRECTION)));
                mCursor.moveToNext();
            }
            finally
            {
                mCursor.close();
            }
            return mWeather;
        }
    }
}

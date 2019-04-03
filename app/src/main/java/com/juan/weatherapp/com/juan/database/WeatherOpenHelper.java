package com.juan.weatherapp.com.juan.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**This class creates a SQLite database
 *
 */
public class WeatherOpenHelper extends SQLiteOpenHelper {

    private Context mContext;
    private static final int VERSION=1;
    private static final String DATABASE_NAME="weatherdatabase.db";

    /**This is the constructor
     *
     * @param context Context THis is required by the constructor.
     */
    public WeatherOpenHelper(Context context)
    {
        super(context,DATABASE_NAME,null,VERSION);
        mContext=context;
    }

    /**This method is used to create and initialize the database
     *
     * @param db SQLiteDatabase is the instance database. Call it with getWritableDatabase() or getReadableDatabase() when creating a new WeatherOpenHelper
     *
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE "+WeatherColumns.TABLE+" ("+
                        WeatherColumns.Columns.LOCATION+", "+
                        WeatherColumns.Columns.LATITUDE+", "+
                        WeatherColumns.Columns.LONGITUDE+", "+
                        WeatherColumns.Columns.TIMESTAMP+", "+
                        WeatherColumns.Columns.WEATHER_CONDITION+", "+
                        WeatherColumns.Columns.ICON_NAME+", "+
                        WeatherColumns.Columns.TEMPERATURE+", "+
                        WeatherColumns.Columns.WIND_SPEED+", "+
                        WeatherColumns.Columns.WIND_DIRECTION+")"
                );
    }

    /**This method is used to upgrade to a new version of the database
     *
     * @param db SQLiteDatabase This is the database to upgrade.
     * @param oldVersion int This is the old version number.
     * @param newVersion int This is the new version number.
     *
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

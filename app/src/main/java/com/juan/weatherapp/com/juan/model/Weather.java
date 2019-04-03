package com.juan.weatherapp.com.juan.model;

/**
 * This class is the model/structure of the Weather class.
 */
public class Weather {

    private String mLocation;
    private float mLatitude;
    private float mLongitude;
    private long mTimeStamp;
    private String mWeatherCondition;
    private float temperature;
    private float windSpeed;
    private String windDirection;
    private String iconName;

    /**
     * This method reads the current latitude.
     * @return float This returns current latitude.
     */
    public float getmLatitude() {
        return mLatitude;
    }

    /**
     * This method set the current latitude
     * @param mLatitude float This is used to set current latitude.
     *
     */
    public void setmLatitude(float mLatitude) {
        this.mLatitude = mLatitude;
    }

    /**
     * This method reads the current longitude.
     * @return float This returns current longitude.
     */
    public float getmLongitude() {
        return mLongitude;
    }

    /**
     * This method set the current longitude
     * @param mLongitude float This is used to set current longitude.
     *
     */
    public void setmLongitude(float mLongitude) {
        this.mLongitude = mLongitude;
    }

    /**
     * This method set the name of the icon to show on UI.
     * @param iconName String This is the icon name.
     *
     */
    public void setIconName(String iconName){ this.iconName=iconName; }

    /**
     * This method reads the name of the icon required for the weather condition.
     * @return String This is the name of the icon required for the weather condition.
     */
    public String getIconName() { return iconName; }

    /**
     * This method reads the name of the current location (city, town...)
     * @return String This is the name of the current location.
     */
    public String getmLocation() {
        return mLocation;
    }

    /**
     * This method set the current location's name.
     * @param mLocation String This is used to set current location's name.
     *
     */
    public void setmLocation(String mLocation) {
        this.mLocation = mLocation;
    }

    /**
     * This method returns the timestamp when the weather instance was taken.
     * @return long This is the timestamp when the weather instance was taken.
     */
    public long getmTimeStamp() {
        return mTimeStamp;
    }

    /**
     * This method set the timestamp for weather instance when it was created.
     * @param mTimeStamp long This is the timestamp when wheather instance was created.
     *
     */
    public void setmTimeStamp(long mTimeStamp) {
        this.mTimeStamp = mTimeStamp;
    }

    /**
     * This method returns the current weather condition
     * @return String This is the weather condition (Sunny, Foggy...)
     */
    public String getmWeatherCondition() {
        return mWeatherCondition;
    }

    /**
     * This method set the current weather condition.
     * @param mWeatherCondition String This describes the weather condition.
     *
     */
    public void setmWeatherCondition(String mWeatherCondition) { this.mWeatherCondition = mWeatherCondition; }

    /**
     * This method returns the current temperature.
     * @return float This is the current temperature.
     */
    public float getTemperature() {
        return temperature;
    }

    /**
     * This method set the current temperature.
     * @param temperature float This is the current temperature.
     *
     */
    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    /**
     * This method returns the current wind speed.
     * @return float This is the current wind speed.
     */
    public float getWindSpeed() {
        return windSpeed;
    }

    /**
     * This method sets the current wind speed
     * @param windSpeed float This is the current wind speed.
     *
     */
    public void setWindSpeed(float windSpeed) {
        this.windSpeed = windSpeed;
    }
    /**
     * This method returns the current wind direction.
     * @return String This is the current wind direction.
     */
    public String getWindDirection() {
        return windDirection;
    }

    /**
     * This method sets the current wind direction.
     * @param windDirection String This is the current wind direction.
     *
     */
    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }
}

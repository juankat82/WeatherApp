package com.juan.weatherapp.com.juan.controller;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.juan.weatherapp.MainActivity;
import com.juan.weatherapp.com.juan.model.Weather;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;


/**
 * This class implements a background process. It's basically a manager, and it provides:
 * Managing of the internet connection and the connection with the weather server.
 * Managing of the parsing of the server answer in JSON.
 * Creates an instance of Weather class that gets stored in database and/or shared preferences.
 * Managing of the "Loading" effects on screen.
 */
public class MyWeatherAsyncTask extends AsyncTask<String,Void, Weather> {

    private ProgressBar progressCircle;
    private TextView loadingTextView;
    private Weather weather=new Weather();
    private AppCompatActivity mActivity;
    private Context mContext;
    private SingletonClass mSingletonClass;

    /**This constructor exists for testing reasons.
     */
    public MyWeatherAsyncTask(){} //this exists for testing reasons, set to private of erase

    /**This is an overloaded constructor, and recommended to use.
     *
     * @param context Context Initializing an instance of google play services, requires this context
     * @param  activity MainActivity This is an instance of MainActivity required to execute tasks on UI thread.
     * @param progCircle ProgressCircle This provides the loading effect.
     * @param loadTextView TextView This shows the "Loading..." text while progCircle is on screen.
     */
    public MyWeatherAsyncTask(Context context, MainActivity activity, ProgressBar progCircle, TextView loadTextView)//pass textviews so we can use the update bar and change visibilities and write any message in onPreExecute()
    {
        mSingletonClass=SingletonClass.getSingletClass(context);
        mActivity=activity;
        mContext=context;
        progressCircle=progCircle;
        loadingTextView=loadTextView;
    }

    /**Overrides onPreExecute method.
     *
     * @return nothing.
     */
    @Override
    protected void onPreExecute()
    {
        //it runs on UI Thread so here we set up the views
        progressCircle.setVisibility(View.VISIBLE);
        loadingTextView.setVisibility(View.VISIBLE);
    }

    /**This method executes code in a background thread.
     *
     * @param string String[] This provides a set of strings to use as data for the background process.
     * @throws JSONException This exception will be thrown if JSON parsing fails.
     * @throws IOException This exception will be thrown if HttpsConnection fails.
     * @return Weather This is an instance of Weather already set with Json code parsed from the server.
     */
    @Override
    protected Weather doInBackground(String... string) {
        //here we execute the code, we get the JSON code here and we process it. No UI Thread
        String query=string[0];
        String result=null;
        Log.i("SingletonClass","QUeryis: "+query);
        try
        {
            result=getUrlString(query);

            //parsing Json
            try {
                if (result!=null) {
                    JSONObject base = new JSONObject(result);
                    JSONObject coordObject = base.getJSONObject("coord");
                    //Creating a weather object
                    weather.setmLongitude((float)coordObject.getDouble("lon"));
                    weather.setmLatitude((float)coordObject.getDouble("lat"));

                    JSONArray weatherArray = base.getJSONArray("weather");
                    JSONObject weatherObject = weatherArray.getJSONObject(0);
                    String condition=weatherObject.getString("description");
                    String capitalizedCondition=condition.substring(0,1).toUpperCase()+condition.substring(1);
                    weather.setmWeatherCondition(capitalizedCondition);
                    weather.setIconName("i"+weatherObject.getString("icon"));

                    JSONObject mainObject=base.getJSONObject("main");
                    weather.setTemperature(Math.round(mSingletonClass.kelvinToCelsius(mainObject.getDouble("temp"))));
                    JSONObject wind=base.getJSONObject("wind");
                    weather.setWindSpeed((float)(wind.getDouble("speed")*2.23694));
                    weather.setWindDirection(mSingletonClass.getWindDirection((int)wind.getDouble("deg")));
                    weather.setmLocation(base.getString("name"));
                    weather.setmTimeStamp(new Date().getTime());
                }
                else
                {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext,"No data received from server. Try again later",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        catch(IOException ioE)
        {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext,"Connection not available",Toast.LENGTH_SHORT).show();
                }
            });
            ioE.printStackTrace();
        }

        return weather;
    }

    /**This method is used to execute instructions after background code has been executed.
     *
     * @param weather Weather This will be used by the get() method on the intantiation and execution of the AsyncTask
     *               to capture Weather instance back to the caller.
     * @return nothing
     */
    @Override
    protected void onPostExecute(Weather weather)
    {
        //make loading text and progress bar dissapear.
        Timer timer=new Timer();
        TimerTask task=new TimerTask() {
            @Override
            public void run() {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressCircle.setVisibility(View.GONE);
                        loadingTextView.setVisibility(View.GONE);
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(task,500,500);
    }

    /**This method call another method for Json, transforming a byteArray into a String.
     *
     * @param url String This is the url to connect to the server.
     * @throws IOException This exception will be thrown if HttpsConnection fails.
     * @return String with the JSON code.
     */
    public String getUrlString(String url) throws IOException
    {
        return new String(getUrlBytes(url));
    }

    /**This method connects to the server, request data into a byteArray and returns it to be handled.
     *
     * @param urlString String This is the url to connect to the server.
     * @throws IOException This exception will be thrown if HttpsConnection fails.
     * @return byte[] This is the answer from the server in the shape of a byte array.
     */
    private byte[] getUrlBytes(String urlString) throws IOException
    {

        URL url=new URL(urlString);
        HttpsURLConnection connection=(HttpsURLConnection)url.openConnection();

        try
        {
            ByteArrayOutputStream out=new ByteArrayOutputStream();
            InputStream in=connection.getInputStream();

            if (connection.getResponseCode()!= HttpURLConnection.HTTP_OK)
            {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext,"Connection with server is not available",Toast.LENGTH_SHORT).show();
                    }
                });
                throw new IOException(connection.getResponseMessage()+": with "+urlString);
            }

            int bytesRead=0;
            byte[] buffer=new byte[1024];

            while((bytesRead=in.read(buffer))>0)
            {
                out.write(buffer,0,bytesRead);
            }
            out.close();
            return out.toByteArray();
        }
        finally {
            connection.disconnect();
        }
    }
}


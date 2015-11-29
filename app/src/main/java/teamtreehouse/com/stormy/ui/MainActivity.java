package teamtreehouse.com.stormy.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import teamtreehouse.com.stormy.R;
import teamtreehouse.com.stormy.weather.Current;
import teamtreehouse.com.stormy.weather.Day;
import teamtreehouse.com.stormy.weather.Forecast;
import teamtreehouse.com.stormy.weather.Hour;


public class MainActivity extends ActionBarActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static final String DAILY_FORECAST = "DAILY_FORECAST";
    public static final String HOURLY_FORECAST = "HOURLY_FOREAST";

    private Forecast mForecast;

    @InjectView(R.id.timeLabel) TextView mTimeLabel;
    @InjectView(R.id.temperatureLabel) TextView mTemperatureLabel;
    @InjectView(R.id.humidityValue) TextView mHumidityValue;
    @InjectView(R.id.precipValue) TextView mPrecipValue;
    @InjectView(R.id.summaryLabel) TextView mSummaryLabel;
    @InjectView(R.id.iconImageView) ImageView mIconImageView;
    @InjectView(R.id.refreshImageView) ImageView mRefreshImageView;
    @InjectView(R.id.progressBar) ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        mProgressBar.setVisibility(View.INVISIBLE);

        final double latitude = 37.8267;
        final double longitude = -122.423;

        mRefreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getForecast(latitude, longitude);
            }
        });

        getForecast(latitude, longitude);

        Log.d(TAG, "Main UI code is running!");
    }

    /**
     * Fetches data from the Forecast IO API
     * @param latitude Latitude of the location
     * @param longitude Longitude of the location
     */
    private void getForecast(double latitude, double longitude) {
        String apiKey = "5a20df57556bd8bcb4924c5a083bc653";
        String forecastUrl = "https://api.forecast.io/forecast/" + apiKey +
                "/" + latitude + "," + longitude;

        if (isNetworkAvailable()) {
            toggleRefresh();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(forecastUrl)
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    alertUserAboutError();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });

                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            mForecast = parseWeatherData(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });
                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    }
                }
            });
        }
        else {
            Toast.makeText(this, getString(R.string.network_unavailable_message),
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Refreshes the UI
     */
    private void toggleRefresh() {
        if (mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshImageView.setVisibility(View.INVISIBLE);
        }
        else {
            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshImageView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Update the UI by setting the weather condition values
     */
    private void updateDisplay() {
        Current current = mForecast.getCurrent();

        mTemperatureLabel.setText(current.getTemperature() + "");
        mTimeLabel.setText("At " + current.getFormattedTime() + " it will be");
        mHumidityValue.setText(current.getHumidity() + "");
        mPrecipValue.setText(current.getPrecipChance() + "%");
        mSummaryLabel.setText(current.getSummary());

        Drawable drawable = getResources().getDrawable(current.getIconId());
        mIconImageView.setImageDrawable(drawable);
    }

    /**
     * Parses weather data to the different classes
     *
     * @param jsonData Json String
     * @return Forecast Object
     * @throws JSONException
     */
    private Forecast parseWeatherData(String jsonData)
            throws JSONException
    {
        Forecast forecast = new Forecast();

        forecast.setCurrent(getCurrentDetails(jsonData));
        forecast.setHourlyForecast(getHourlyDetails(jsonData));
        forecast.setDailyForecast(getDailyDetails(jsonData));

        return forecast;
    }

    /**
     * Getting daily weather data from the json object
     *
     * @param jsonData Json string
     * @return Daily Array
     * @throws JSONException
     */
    private Day[] getDailyDetails(String jsonData)
            throws JSONException{
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");

        JSONObject daily = forecast.getJSONObject("daily");
        JSONArray data = daily.getJSONArray("data");

        Day[] days = new Day[data.length()];

        for (int i = 0; i < data.length(); i++)
        {
            JSONObject jsonDaily = data.getJSONObject(i);

            Day day = new Day();
            day.setSummary(jsonDaily.getString("summary"));
            day.setTime(jsonDaily.getLong("time"));
            day.setIcon(jsonDaily.getString("icon"));
            day.setTemperatureMax(jsonDaily.getDouble("temperatureMax"));
            day.setTimezone(timezone);

            days[i] = day;
        }
        return days;
    }

    /**
     * Getting the hourly data from the json object
     *
     * @param jsonData Json String
     * @return Hour Array
     * @throws JSONException
     */
    private Hour[] getHourlyDetails(String jsonData)
            throws JSONException{
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");

        final String HOURLY = "hourly";
        final String DATA = "data";

        JSONObject hourly = forecast.getJSONObject(HOURLY);
        JSONArray data = hourly.getJSONArray(DATA);

        //Creating a Hourly array
        Hour[] hours = new Hour[data.length()];

        for (int i = 0; i < data.length(); i++)
        {
            JSONObject jsonHour = data.getJSONObject(i);

            //Setting the hourly weather data
            Hour hour = new Hour();
            hour.setIcon(jsonHour.getString("icon"));
            hour.setSummary(jsonHour.getString("summary"));
            hour.setTemperature(jsonHour.getDouble("temperature"));
            hour.setTime(jsonHour.getLong("time"));
            hour.setTimezone(timezone);

            hours[i] = hour;
            Log.i(LOG_TAG,"Hourly array: " + hours[i].toString());
        }
        return hours;
    }

    /**
     * Getting the current weather Json and formatting it or decoding it
     *
     * @param jsonData Json data string
     * @return Current
     * @throws JSONException
     */
    private Current getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        Log.i(TAG, "From JSON: " + timezone);

        JSONObject currently = forecast.getJSONObject("currently");

        Current current = new Current();
        current.setHumidity(currently.getDouble("humidity"));
        current.setTime(currently.getLong("time"));
        current.setIcon(currently.getString("icon"));
        current.setPrecipChance(currently.getDouble("precipProbability"));
        current.setSummary(currently.getString("summary"));
        current.setTemperature(currently.getDouble("temperature"));
        current.setTimeZone(timezone);

        Log.d(TAG, current.getFormattedTime());

        return current;
    }

    /**
     * Checks for network or internet on the user`s device
     *
     * @return boolean
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }

        return isAvailable;
    }

    /**
     * Shows the dialog in case of an error
     */
    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }

    /**
     * Using the butterKnife annotation to make a Onclick Listener
     * @param view
     */
    @OnClick (R.id.dailyButton)
    public void startDailyActivity(View view)
    {
        Intent intent = new Intent(this,DailyForecastActivity.class);
        intent.putExtra(DAILY_FORECAST,mForecast.getDailyForecast());
        startActivity(intent);
    }

    @OnClick (R.id.hourlyButton)
    public void startHourlyForecast(View view)
    {
        Intent intent = new Intent(this,HourlyForecastActivity.class);
        intent.putExtra(HOURLY_FORECAST,mForecast.getHourlyForecast());
        startActivity(intent);
    }
}















package teamtreehouse.com.stormy.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Arrays;

import teamtreehouse.com.stormy.R;
import teamtreehouse.com.stormy.adapters.DayAdapter;
import teamtreehouse.com.stormy.weather.Day;

public class DailyForecastActivity extends ListActivity {

    private Day[] mDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecast);

        Intent intent = getIntent();
        Parcelable[] parcelables = intent.getParcelableArrayExtra(
                MainActivity.DAILY_FORECAST);
        //Converting to Days array
        mDays = Arrays.copyOf(parcelables,parcelables.length,Day[].class);

        DayAdapter adapter = new DayAdapter(this,mDays);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String dayOfTheWeek = mDays[position].getDayOfTheWeek();
        String temperature = mDays[position].getTemperatureMax() + "";
        String condition = mDays[position].getSummary();

        String message = String.format("On %s the temperature is %s and it is %s",
                dayOfTheWeek,
                temperature,
                condition);

        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }
}

package teamtreehouse.com.stormy.weather;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by jokamjohn on 11/24/2015.
 */
public class Day implements Parcelable{

    private long mTime;
    private String mSummary;
    private double mTemperatureMax;
    private String mIcon;
    private String mTimezone;

    public Day() {
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    //Rounding off to return an int
    public int getTemperatureMax() {
        return (int) Math.round(mTemperatureMax);
    }

    public void setTemperatureMax(double temperatureMax) {
        mTemperatureMax = temperatureMax;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public String getTimezone() {
        return mTimezone;
    }

    public void setTimezone(String timezone) {
        mTimezone = timezone;
    }

    /**
     * Getting the weather icon id
     *
     * @return Id of the weather icon drawable
     */
    public int getIconId() {

        return Forecast.getIconId(mIcon);
    }

    /**
     * Format the timestamp from the Api to get a Human Readable string
     * @return Date
     */
    public String getDayOfTheWeek()
    {
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
        formatter.setTimeZone(TimeZone.getTimeZone(mTimezone));

        Date dateTime = new Date(mTime * 1000);

        return formatter.format(dateTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(mSummary);
        parcel.writeString(mIcon);
        parcel.writeString(mTimezone);
        parcel.writeLong(mTime);
        parcel.writeDouble(mTemperatureMax);
    }

    //Creating a private constructor to use, the order
    //in which they were written matters
    private Day (Parcel parcel){
        mSummary = parcel.readString();
        mIcon = parcel.readString();
        mTimezone = parcel.readString();
        mTime = parcel.readLong();
        mTemperatureMax = parcel.readDouble();
    }

    //Required to implement Parcelable
    public static final Creator<Day> CREATOR = new Creator<Day>() {
        @Override
        public Day createFromParcel(Parcel parcel) {
            //return the private constructor
            return new Day(parcel);
        }

        @Override
        public Day[] newArray(int size) {
            return new Day[size];
        }
    };
}

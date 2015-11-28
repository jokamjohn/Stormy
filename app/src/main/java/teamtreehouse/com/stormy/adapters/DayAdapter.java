package teamtreehouse.com.stormy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import teamtreehouse.com.stormy.R;
import teamtreehouse.com.stormy.weather.Day;

/**
 * Created by jokamjohn on 11/28/2015.
 */
public class DayAdapter extends BaseAdapter {

    private Context mContext;
    private Day[] mDays;

    public DayAdapter(Context context, Day[] days) {
        mContext = context;
        mDays = days;
    }

    @Override
    public int getCount() {
        return mDays.length;
    }

    @Override
    public Object getItem(int position) {
        return mDays[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;//Tagging items for easy use
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        ViewHolder holder;

        if (view == null)
        {
            //Will be null when brand new
            view = LayoutInflater.from(mContext).inflate(R.layout.daily_list_item,null);
            holder = new ViewHolder();

            holder.iconImageView = (ImageView) view.findViewById(R.id.circleImageView);
            holder.dayLabel = (TextView) view.findViewById(R.id.dayNameLabel);
            holder.temperatureLabel = (TextView) view.findViewById(R.id.temperatureLabel);

            view.setTag(holder);
        }
        else {
            //Already have the views
            holder = (ViewHolder) view.getTag();
        }
        return null;
    }

    private static class ViewHolder {
        public ImageView iconImageView;
        public TextView temperatureLabel;
        public TextView dayLabel;
    }
}

package ssp.tt.com.ssp.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.widgets.IconTextView;

public class DashboardAdapter extends BaseAdapter {

    private Context mContext;
    private final String[] gridViewString;
    private final int[] gridViewImageId;

    public DashboardAdapter(Context context, String[] gridViewString, int[] gridViewImageId) {
        mContext = context;
        this.gridViewImageId = gridViewImageId;
        this.gridViewString = gridViewString;
    }

    @Override
    public int getCount() {
        return gridViewString.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View gridViewAndroid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            gridViewAndroid = new View(mContext);
            gridViewAndroid = inflater.inflate(R.layout.gridview_layout, null);
            TextView textViewAndroid = (TextView) gridViewAndroid.findViewById(R.id.tv_dashboard_text);
            Typeface fontAwesomeFont = Typeface.createFromAsset(mContext.getAssets(), "fonts/fontawesome-webfont.ttf");
            IconTextView imageViewAndroid = (IconTextView) gridViewAndroid.findViewById(R.id.tv_dashboard_icon);
            textViewAndroid.setText(gridViewString[i]);
            imageViewAndroid.setText(gridViewImageId[i]);
            imageViewAndroid.setTypeface(fontAwesomeFont);

        } else {
            gridViewAndroid = (View) convertView;
        }

        return gridViewAndroid;
    }
}
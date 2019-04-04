package ssp.tt.com.ssp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.activity.Profile;


/**
 * @author
 * 
 */
public class CountryAdapter extends BaseAdapter implements Filterable {

	private final Activity mActivity;
	private static LayoutInflater sInflater = null;
	/**
	 * PREFS_NAME
	 */
	public static final String PREFS_NAME = "MyApp_Settings";

	private ArrayList<HashMap<String, String>> mCountryData;
	private final ArrayList<HashMap<String, String>> mCountryList;

	HashMap<String, String> mCountrySortList;
	String mAction;

	/**
	 * @param a
	 * @param countryName
	 */
	public CountryAdapter(Activity a,
			ArrayList<HashMap<String, String>> countryName) {
		mActivity = a;
		mCountryData = countryName;
		mCountryData = new ArrayList<HashMap<String, String>>(countryName);
		mCountryList = new ArrayList<HashMap<String, String>>();
		mCountryList.addAll(countryName);
		sInflater = (LayoutInflater) mActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	@Override
	public int getCount() {

		if (mCountryData == null) {
			return 0;
		}
		return mCountryData.size();
	}

	@Override
	public Object getItem(int arg0) {

		return arg0;
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		View vi = convertView;
		if (convertView == null) {
			vi = sInflater.inflate(R.layout.custom_dropdown, null);
		}
		mCountrySortList = new HashMap<String, String>();
		mCountrySortList = mCountryData.get(position);

		TextView received_message = (TextView) vi
				.findViewById(android.R.id.text1);
		received_message.setText(mCountrySortList.get("name"));

		received_message.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent viewIntent = new Intent(mActivity, Profile.class);
					viewIntent.putExtra("name",
							mCountryData.get(position).get("name"));

				viewIntent.putExtra("id", mCountryData.get(position).get("id"));
				mActivity.setResult(1, viewIntent);
				mActivity.finish();

			}
		});

		return vi;
	}

	/**
	 * @method getFilter
	 * @description country search filter
	 */
	@Override
	public Filter getFilter() {

		return new Filter() {

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {

				constraint = constraint.toString().toLowerCase();
				FilterResults result = new FilterResults();
				String filterString = constraint.toString().toLowerCase();

				String filterableString;
				if (constraint != null && constraint.toString().length() > 0) {

					ArrayList<HashMap<String, String>> Filtered_Names = new ArrayList<HashMap<String, String>>();

					for (int i = 0, l = mCountryList.size(); i < l; i++) {

						HashMap<String, String> searchdata = mCountryList
								.get(i);
						String group_contact_name = searchdata.get("name");
						filterableString = group_contact_name;

						if (filterableString.toLowerCase().startsWith(
								filterString)) {
							Filtered_Names.add(mCountryList.get(i));

						}
					}
					result.count = Filtered_Names.size();
					result.values = Filtered_Names;
				} else {
					synchronized (this) {
						result.values = mCountryList;
						result.count = mCountryList.size();
					}
				}
				return result;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {

				mCountryData = (ArrayList<HashMap<String, String>>) results.values;
				notifyDataSetChanged();

			}

		};
	}

}

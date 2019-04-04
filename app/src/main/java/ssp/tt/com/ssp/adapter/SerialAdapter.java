package ssp.tt.com.ssp.adapter;

/**
 * Created by Ecommerce on 27-Jun-18.
 */

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.support.GetTicketSerial;

public class SerialAdapter extends ArrayAdapter<GetTicketSerial> {
    // Declare Variables
    Context context;
    LayoutInflater inflater;
    List<GetTicketSerial> worldpopulationlist;
    private SparseBooleanArray mSelectedItemsIds;

    public SerialAdapter(Context context, int resourceId,
                           List<GetTicketSerial> worldpopulationlist) {
        super(context, resourceId, worldpopulationlist);
        mSelectedItemsIds = new SparseBooleanArray();
        this.context = context;
        this.worldpopulationlist = worldpopulationlist;
        inflater = LayoutInflater.from(context);
    }

    private class ViewHolder {
        TextView ticketNumber;
    }

    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.ticket_serial_item, null);
            // Locate the TextViews in listview_item.xml
            holder.ticketNumber = (TextView) view.findViewById(R.id.ticketNumber);

            view.setTag(holder);
            return view;
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Capture position and set to the TextViews
        holder.ticketNumber.setText(worldpopulationlist.get(position).getTicketNumber());

        return view;
    }

    @Override
    public void remove(GetTicketSerial object) {
        worldpopulationlist.remove(object);
        notifyDataSetChanged();
    }

    public List<GetTicketSerial> getWorldPopulation() {
        return worldpopulationlist;
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }
}
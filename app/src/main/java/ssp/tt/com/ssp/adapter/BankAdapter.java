package ssp.tt.com.ssp.adapter;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.model.Bank;

public class BankAdapter extends ArrayAdapter<Bank> {

    LayoutInflater flater;

    public BankAdapter(Activity context, int resouceId, int textviewId, List<Bank> list) {
        super(context, resouceId, textviewId, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return rowview(convertView, position);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return rowview(convertView, position);
    }

    private View rowview(View convertView, int position) {

        Bank rowItem = getItem(position);

        viewHolder holder;
        View rowview = convertView;
        if (rowview == null) {

            holder = new viewHolder();
            flater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowview = flater.inflate(R.layout.item_bank, null, false);
            holder.txtTitle = (TextView) rowview.findViewById(R.id.tv_title);
            rowview.setTag(holder);
        } else {
            holder = (viewHolder) rowview.getTag();
        }
        holder.txtTitle.setText(rowItem.getBankName());

        return rowview;
    }

    private class viewHolder {
        TextView txtTitle;
    }
}
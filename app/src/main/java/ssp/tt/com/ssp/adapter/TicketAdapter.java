package ssp.tt.com.ssp.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.model.Lottery;

public class TicketAdapter extends HFRecyclerViewAdapter<Lottery, TicketAdapter.DataViewHolder> {

    ArrayList<Lottery> lotteryArrayList;

    public TicketAdapter(Context context, ArrayList<Lottery> lotteryArrayList) {
        super(context);
        this.lotteryArrayList = lotteryArrayList;
    }


    @Override
    public void footerOnVisibleItem() {
    }

    @Override
    public DataViewHolder onCreateDataItemViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ticket_serial_item, parent, false);
        return new DataViewHolder(v);
    }

    @Override
    public void onBindDataItemViewHolder(DataViewHolder holder, int position) {
        Lottery lottery = lotteryArrayList.get(position);
        String ticketDetails = lottery.getTicketStatus() + " " + lottery.getTicketNumber();
        holder.ticketNumber.setText(ticketDetails);
        if (lottery.isTicketSelected()) {
            holder.relativeLayout.setBackgroundColor(Color.parseColor("#069BCD"));
        } else {
            holder.relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
    }

    class DataViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ticketNumber)
        TextView ticketNumber;

        @BindView(R.id.rl_lottery_item)
        RelativeLayout relativeLayout;

        public DataViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}


package ssp.tt.com.ssp.adapter;

import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.model.Lottery;

public class LotteryAdapter extends RecyclerView.Adapter<LotteryAdapter.MyViewHolder> {

    private List<Lottery> lotteryList;


    public LotteryAdapter(List<Lottery> lotteryList) {
        this.lotteryList = lotteryList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ticket_serial_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Lottery lottery = lotteryList.get(position);
        String ticketDetails = lottery.getTicketStatus() + " " + lottery.getTicketNumber();
        holder.ticketNumber.setText(ticketDetails);
        if (lottery.isTicketSelected()) {
            holder.relativeLayout.setBackgroundColor(Color.parseColor("#069BCD"));
        } else {
            holder.relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
    }

    @Override
    public int getItemCount() {
        return lotteryList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.ticketNumber)
        TextView ticketNumber;

        @BindView(R.id.rl_lottery_item)
        RelativeLayout relativeLayout;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
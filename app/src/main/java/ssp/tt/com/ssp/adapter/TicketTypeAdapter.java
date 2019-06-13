package ssp.tt.com.ssp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.model.Ticket;
import ssp.tt.com.ssp.utils.Util;

public class TicketTypeAdapter extends HFRecyclerViewAdapter<Ticket, TicketTypeAdapter.DataViewHolder> {

    ArrayList<Ticket> ticketArrayList;

    public TicketTypeAdapter(Context context, ArrayList<Ticket> ticketArrayList) {
        super(context);
        this.ticketArrayList = ticketArrayList;
    }

    @Override
    public void footerOnVisibleItem() {

    }

    @Override
    public DataViewHolder onCreateDataItemViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ticket_type_item, parent, false);
        return new DataViewHolder(v);
    }

    @Override
    public void onBindDataItemViewHolder(DataViewHolder holder, int position) {
        Ticket ticket = ticketArrayList.get(position);
        holder.tvFirstPrize.setText("First Prize " + ticket.getGrandPrize());
        holder.tvTicketType.setText(ticket.getTicketName());
        holder.tvDrawDate.setText("Draw on " + Util.convertLocalDate(ticket.getTicketDrawDate()));
        holder.tvINR.setText("INR " + ticket.getTicketRate() + " only");
        if (ticket.getSelectedTicket() == 0) {
            holder.tvSeriousCount.setVisibility(View.GONE);
        } else {
            holder.tvSeriousCount.setVisibility(View.VISIBLE);
            holder.tvSeriousCount.setText("" + ticket.getSelectedTicket());
        }

        if (position % 2 == 0) {
            holder.viewRight.setVisibility(View.GONE);
        } else {
            holder.viewRight.setVisibility(View.VISIBLE);
        }

        holder.tvSeriousCount.setVisibility(View.GONE);
        holder.tvDrawDate.setVisibility(View.GONE);
        holder.tvINR.setVisibility(View.GONE);
        holder.viewRight.setVisibility(View.GONE);
    }

    class DataViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_ticket_type)
        TextView tvTicketType;

        @BindView(R.id.tv_first_prize)
        TextView tvFirstPrize;

        @BindView(R.id.tv_date)
        TextView tvDrawDate;

        @BindView(R.id.tv_inr)
        TextView tvINR;

        @BindView(R.id.view_right)
        View viewRight;

        @BindView(R.id.tv_count)
        TextView tvSeriousCount;

        public DataViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}


package ssp.tt.com.ssp.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.model.MyBlockResultDao;
import ssp.tt.com.ssp.model.Ticket;
import ssp.tt.com.ssp.utils.Util;

public class MyResultNewAdapter extends HFRecyclerViewAdapter<MyBlockResultDao, MyResultNewAdapter.DataViewHolder> {

    private ArrayList<MyBlockResultDao> myResultDaos;

    public MyResultNewAdapter(Context context, ArrayList<MyBlockResultDao> myResultDaos) {
        super(context);
        this.myResultDaos = myResultDaos;
    }

    @Override
    public void footerOnVisibleItem() {

    }

    @Override
    public DataViewHolder onCreateDataItemViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_result, parent, false);
        return new DataViewHolder(v);
    }

    @Override
    public void onBindDataItemViewHolder(DataViewHolder holder, int position) {
        MyBlockResultDao myBlockResultDao = myResultDaos.get(position);
        holder.tvTicketName.setText(myBlockResultDao.getTicketTypeName());
        holder.tvPrize.setText(myBlockResultDao.getPrize());
        holder.block_name.setText(myBlockResultDao.getBlockName());
        holder.drawCodeDate.setText(myBlockResultDao.getDrawCode() + " & " + myBlockResultDao.getDate());
        holder.ltSerious.setText(myBlockResultDao.getLtSerious() + " " + myBlockResultDao.getLtNumber());
        holder.tvMonth.setText("Prize for " + myBlockResultDao.getMonth() );

        if (myBlockResultDao.getMonth().equals("")) {
            holder.tvMonth.setVisibility(View.GONE);
        } else {
            holder.tvMonth.setVisibility(View.VISIBLE);
        }
        //tvMonth
        if (myBlockResultDao.getPrize().equals("")) {
            holder.tvPrizeTxt.setVisibility(View.GONE);
            holder.tvPrize.setVisibility(View.GONE);
        } else {
            holder.tvPrizeTxt.setVisibility(View.VISIBLE);
            holder.tvPrize.setVisibility(View.VISIBLE);
        }

        holder.tvPrizeTxt.setText(myBlockResultDao.getBreakPosition());
    }

    class DataViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ticket_name)
        TextView tvTicketName;

        @BindView(R.id.tv_prize_txt)
        TextView tvPrizeTxt;

        @BindView(R.id.tv_prize)
        TextView tvPrize;

        @BindView(R.id.block_name)
        TextView block_name;

        @BindView(R.id.drawcode_date)
        TextView drawCodeDate;

        @BindView(R.id.lt_serious)
        TextView ltSerious;

        @BindView(R.id.tv_month)
        TextView tvMonth;

        public DataViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}


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
import ssp.tt.com.ssp.model.LotterySeries;

public class TicketSeriousAdapter extends HFRecyclerViewAdapter<LotterySeries, TicketSeriousAdapter.DataViewHolder> {

    ArrayList<LotterySeries> lotterySeriesArrayList;

    public TicketSeriousAdapter(Context context, ArrayList<LotterySeries> lotterySeriesArrayList) {
        super(context);
        this.lotterySeriesArrayList = lotterySeriesArrayList;
    }


    @Override
    public void footerOnVisibleItem() {
    }

    @Override
    public DataViewHolder onCreateDataItemViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ticket_series_item, parent, false);
        return new DataViewHolder(v);
    }

    @Override
    public void onBindDataItemViewHolder(DataViewHolder holder, int position) {
        LotterySeries lotterySeries = lotterySeriesArrayList.get(position);
        holder.tvSeriousName.setText(lotterySeries.getLotterySeries());
        if (lotterySeries.getLotteryCount() == 0) {
            holder.tvSeriousCount.setVisibility(View.GONE);
        } else {
            holder.tvSeriousCount.setVisibility(View.VISIBLE);
            holder.tvSeriousCount.setText("" + lotterySeries.getLotteryCount());
        }

        if (position % 2 == 0) {
            holder.viewRight.setVisibility(View.GONE);
        } else {
            holder.viewRight.setVisibility(View.VISIBLE);
        }
    }

    class DataViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_ticket_type_name)
        TextView tvSeriousName;

        @BindView(R.id.tv_count)
        TextView tvSeriousCount;

        @BindView(R.id.view_right)
        View viewRight;


        public DataViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}


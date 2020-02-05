package ssp.tt.com.ssp.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.model.LotterySeries;

public class LotterySeriousAdapter extends RecyclerView.Adapter<LotterySeriousAdapter.MyViewHolder> {

    private List<LotterySeries> lotteryList;


    public LotterySeriousAdapter(List<LotterySeries> lotteryList) {
        this.lotteryList = lotteryList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ticket_series_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        LotterySeries lotterySeries = lotteryList.get(position);
        holder.tvSeriousName.setText(lotterySeries.getLotterySeries());
        if (lotterySeries.getLotteryCount() == 0) {
            holder.tvSeriousCount.setVisibility(View.GONE);
        } else {
            holder.tvSeriousCount.setVisibility(View.VISIBLE);
            holder.tvSeriousCount.setText("" + lotterySeries.getLotteryCount());
        }


    }

    @Override
    public int getItemCount() {
        return lotteryList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.tv_ticket_type_name)
        TextView tvSeriousName;

        @BindView(R.id.tv_count)
        TextView tvSeriousCount;


        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
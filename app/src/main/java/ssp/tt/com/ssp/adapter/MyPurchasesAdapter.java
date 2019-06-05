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
import ssp.tt.com.ssp.model.MyBlockResultDao;
import ssp.tt.com.ssp.model.MyPurchasesModel;

public class MyPurchasesAdapter extends HFRecyclerViewAdapter<MyPurchasesModel, MyPurchasesAdapter.DataViewHolder> {

    private ArrayList<MyPurchasesModel> myResultDaos;

    public MyPurchasesAdapter(Context context, ArrayList<MyPurchasesModel> myResultDaos) {
        super(context);
        this.myResultDaos = myResultDaos;
    }

    @Override
    public void footerOnVisibleItem() {

    }

    @Override
    public DataViewHolder onCreateDataItemViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_purchases, parent, false);
        return new DataViewHolder(v);
    }

    @Override
    public void onBindDataItemViewHolder(DataViewHolder holder, int position) {
        MyPurchasesModel myPurchasesModelArrayList = myResultDaos.get(position);
        holder.blockType.setText("My Purchases");

            holder.drawcode_date.setVisibility(View.GONE);
            holder.tvPrizeTxt.setVisibility(View.GONE);
            holder.tvPrize.setVisibility(View.GONE);


        holder.tvTicketName.setText(myPurchasesModelArrayList.getPur_date());
        holder.tvMembers.setText(myPurchasesModelArrayList.getNo_of_units());
        holder.block_name.setText(myPurchasesModelArrayList.getTrans_amount());

    }

    class DataViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.ticket_name)
        TextView tvTicketName;

        @BindView(R.id.block_type)
        TextView blockType;

        @BindView(R.id.tv_members)
        TextView tvMembers;

        @BindView(R.id.tv_prize_txt)
        TextView tvPrizeTxt;

        @BindView(R.id.tv_prize)
        TextView tvPrize;

        @BindView(R.id.block_name)
        TextView block_name;

        @BindView(R.id.tv_member_title)
        TextView tvTitle;

        @BindView(R.id.drawcode_date)
        TextView drawcode_date;

        public DataViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}


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
import ssp.tt.com.ssp.utils.Util;

public class MyBlockResultNewAdapter extends HFRecyclerViewAdapter<MyBlockResultDao, MyBlockResultNewAdapter.DataViewHolder> {

    private ArrayList<MyBlockResultDao> myResultDaos;

    public MyBlockResultNewAdapter(Context context, ArrayList<MyBlockResultDao> myResultDaos) {
        super(context);
        this.myResultDaos = myResultDaos;
    }

    @Override
    public void footerOnVisibleItem() {

    }

    @Override
    public DataViewHolder onCreateDataItemViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_block_result, parent, false);
        return new DataViewHolder(v);
    }

    @Override
    public void onBindDataItemViewHolder(DataViewHolder holder, int position) {
        MyBlockResultDao myBlockResultDao = myResultDaos.get(position);
        holder.blockType.setText(myBlockResultDao.getBlockType());
        if (myBlockResultDao.getBlockType().equals("")) {
            holder.blockType.setVisibility(View.GONE);
        } else {
            holder.blockType.setVisibility(View.VISIBLE);
        }

        holder.tvTicketName.setText(myBlockResultDao.getTicketTypeName());
        holder.tvMembers.setText(myBlockResultDao.getMember());
        holder.tvPrize.setText(myBlockResultDao.getPrize());
        holder.block_name.setText(myBlockResultDao.getBlockName());
        String dd = myBlockResultDao.getDate();
        holder.drawcode_date.setText(myBlockResultDao.getDrawCode() + " & " + dd);
        //holder.drawcode_date.setText(Util.convertLocalDateTime(myBlockResultDao.getDate()));

        if (myBlockResultDao.getPrize().equals("")) {
            holder.tvPrizeTxt.setVisibility(View.GONE);
            holder.tvPrize.setVisibility(View.GONE);
        }else{
            holder.tvPrizeTxt.setVisibility(View.VISIBLE);
            holder.tvPrize.setVisibility(View.VISIBLE);
        }
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


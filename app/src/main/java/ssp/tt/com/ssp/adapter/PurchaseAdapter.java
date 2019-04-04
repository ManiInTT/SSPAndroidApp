package ssp.tt.com.ssp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.model.PurchaseHistory;

public class PurchaseAdapter extends RecyclerView.Adapter<PurchaseAdapter.MyViewHolder> {

    private List<PurchaseHistory> purchaseHistories;


    public PurchaseAdapter(List<PurchaseHistory> purchaseHistoryList) {
        this.purchaseHistories = purchaseHistoryList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.purchase_summary_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PurchaseHistory purchaseHistory = purchaseHistories.get(position);
        holder.ticketTypeName.setText(purchaseHistory.getlTypeName());
        holder.ticketSeriousName.setText(purchaseHistory.getLtNumber());
        holder.ticketCode.setText(purchaseHistory.getLtDrawCode());
        holder.ticketAmount.setText("1 X " + purchaseHistory.getLtRate() + " = " + purchaseHistory.getLtRate());
    }

    @Override
    public int getItemCount() {
        return purchaseHistories.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.tv_ticket_type_name)
        TextView ticketTypeName;

        @BindView(R.id.tv_ticket_serious_name)
        TextView ticketSeriousName;

        @BindView(R.id.tv_ticket_code)
        TextView ticketCode;

        @BindView(R.id.tv_ticket_amount)
        TextView ticketAmount;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
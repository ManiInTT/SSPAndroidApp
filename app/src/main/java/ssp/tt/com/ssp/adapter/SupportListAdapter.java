package ssp.tt.com.ssp.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.interfaces.SupportRequestListener;
import ssp.tt.com.ssp.model.SupportRequest;

public class SupportListAdapter extends RecyclerView.Adapter<SupportListAdapter.MyViewHolder> {

    private List<SupportRequest> supportRequestList;
    private SupportRequestListener supportRequestListener;

    public SupportListAdapter(List<SupportRequest> supportRequests) {
        this.supportRequestList = supportRequests;
    }

    public void setSupportRequestListener(SupportRequestListener supportRequestListener){
        this.supportRequestListener  = supportRequestListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.support_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final SupportRequest supportRequest = supportRequestList.get(position);
        holder.tvSubject.setText(supportRequest.getRequestSubject());
        holder.tvDateTime.setText(supportRequest.getSupportDate());
        holder.tvStatus.setText(supportRequest.getSupportStatus());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportRequestListener.clickSupport(supportRequest.getRequestId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return supportRequestList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.tv_date_time)
        TextView tvDateTime;

        @BindView(R.id.tv_subject)
        TextView tvSubject;

        @BindView(R.id.tv_status)
        TextView tvStatus;


        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
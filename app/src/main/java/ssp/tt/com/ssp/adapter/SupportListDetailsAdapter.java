package ssp.tt.com.ssp.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.interfaces.SupportRequestListener;
import ssp.tt.com.ssp.model.SupportRequest;
import ssp.tt.com.ssp.model.SupportTrack;

public class SupportListDetailsAdapter extends RecyclerView.Adapter<SupportListDetailsAdapter.MyViewHolder> {

    private List<SupportTrack> supportRequestList;
    private SupportRequestListener supportRequestListener;

    public SupportListDetailsAdapter(List<SupportTrack> supportRequests) {
        this.supportRequestList = supportRequests;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.support_list_item_track, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final SupportTrack supportTrack = supportRequestList.get(position);
        holder.tvComment.setText(supportTrack.getTsComment());
        holder.tvCommentOn.setText(supportTrack.getTsCommentOn());
        holder.tvAgentName.setText(supportTrack.getAgentName());
    }

    @Override
    public int getItemCount() {
        return supportRequestList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.tv_comment)
        TextView tvComment;

        @BindView(R.id.tv_comment_on)
        TextView tvCommentOn;

        @BindView(R.id.tv_agent_name)
        TextView tvAgentName;


        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
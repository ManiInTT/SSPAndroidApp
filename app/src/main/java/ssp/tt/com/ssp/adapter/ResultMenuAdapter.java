package ssp.tt.com.ssp.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.model.LotterySeries;
import ssp.tt.com.ssp.model.ResultMenu;
import ssp.tt.com.ssp.widgets.IconTextView;

public class ResultMenuAdapter extends RecyclerView.Adapter<ResultMenuAdapter.MyViewHolder> {

    private List<ResultMenu> menuList;


    public ResultMenuAdapter(List<ResultMenu> menuList) {
        this.menuList = menuList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ;
        holder.tvName.setText(menuList.get(position).getName());
        holder.tvIcon.setText(menuList.get(position).getResourceId());
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.tv_dashboard_text)
        TextView tvName;

        @BindView(R.id.tv_dashboard_icon)
        IconTextView tvIcon;


        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
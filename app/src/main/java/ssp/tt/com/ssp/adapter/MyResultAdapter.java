package ssp.tt.com.ssp.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.model.MyBlockResultDao;


public class MyResultAdapter extends RecyclerView.Adapter<MyResultAdapter.MyViewHolder> implements Filterable {

    private List<MyBlockResultDao> myBlockResultDaoList;
    private List<MyBlockResultDao> myBlockResultDaoListFilter;


    public MyResultAdapter(List<MyBlockResultDao> departList) {
        this.myBlockResultDaoList = departList;
        this.myBlockResultDaoListFilter = departList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_result, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        MyBlockResultDao myBlockResultDao = myBlockResultDaoListFilter.get(position);
        holder.tvTicketName.setText(myBlockResultDao.getTicketTypeName());
        holder.tvPrize.setText(myBlockResultDao.getPrize());
        holder.block_name.setText(myBlockResultDao.getBlockName());
        holder.drawCodeDate.setText(myBlockResultDao.getDrawCode() + " & " + myBlockResultDao.getDate());
        holder.ltSerious.setText(myBlockResultDao.getLtSerious() + " " + myBlockResultDao.getLtNumber());
        holder.tvMonth.setText(myBlockResultDao.getMonth());

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

    @Override
    public int getItemCount() {
        return myBlockResultDaoListFilter.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {


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

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    myBlockResultDaoListFilter = myBlockResultDaoList;
                } else {
                    List<MyBlockResultDao> filteredList = new ArrayList<>();
                    for (MyBlockResultDao row : myBlockResultDaoList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getDate().toLowerCase().contains(charString.toLowerCase()) || row.getBlockName().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    myBlockResultDaoListFilter = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = myBlockResultDaoListFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                myBlockResultDaoListFilter = (ArrayList<MyBlockResultDao>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
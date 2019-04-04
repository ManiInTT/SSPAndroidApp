package ssp.tt.com.ssp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

public abstract class BaseRecyclerViewAdapter<Class> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Class> mList;
    public Context mContext;
    public OnItemClickLitener mOnItemClickLitener;


    public BaseRecyclerViewAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getItemCount() {
        if (mList != null)
            return mList.size();
        else
            return 0;
    }

    public void setData(ArrayList<Class> data) {
        this.mList = data;
        notifyDataSetChanged();
    }

    public ArrayList<Class> getData() {
        return mList;
    }

    public void setData(Class[] list) {
        ArrayList<Class> arrayList = new ArrayList<>(list.length);
        for (Class t : list) {
            arrayList.add(t);
        }
        setData(arrayList);
    }


    public void addData(int position, Class item) {
        if (mList != null && position < mList.size()) {
            mList.add(position, item);
            notifyMyItemInserted(position);
        }
    }

    public void removeData(int position) {
        if (mList != null && position < mList.size()) {
            mList.remove(position);
            notifyMyItemRemoved(position);
        }
    }

    protected void notifyMyItemInserted(int position) {
        notifyItemInserted(position);
    }

    protected void notifyMyItemRemoved(int position) {
        notifyItemRemoved(position);
    }

    protected void notifyMyItemChanged(int position) {
        notifyItemChanged(position);
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }


}

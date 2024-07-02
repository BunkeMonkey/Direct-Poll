package bunke.DirectPoll.View;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


import bunke.DirectPoll.Model.Poll;
import bunke.DirectPoll.R;

public class RecyclerAdapterPastPolls extends RecyclerView.Adapter<RecyclerAdapterPastPolls.ViewHolder> {


    private List<Poll> mData;
    private LayoutInflater mInflater;
    private RecyclerAdapterPastPolls.ItemClickListener mClickListener;


    // data is passed into the constructor
    RecyclerAdapterPastPolls(Context context, ArrayList<Poll> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;


    }

    // inflates the row layout from xml when needed
    @Override
    public RecyclerAdapterPastPolls.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new RecyclerAdapterPastPolls.ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(RecyclerAdapterPastPolls.ViewHolder holder, int position) {
        Poll entry = mData.get(position);
        if (entry == null) {
            Log.d("RecyclerAdapterPastPolls", "------------------Entry is null------------------------");
            return;
        }
        holder.keyTextView.setText(entry.getQuestion());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView keyTextView;
        TextView valueTextView;

        ViewHolder(View itemView) {
            super(itemView);
            keyTextView = itemView.findViewById(R.id.Keys);
            valueTextView = itemView.findViewById(R.id.Values);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public Poll getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(RecyclerAdapterPastPolls.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public void updateData(ArrayList<Poll> data){
        mData = data;
        notifyDataSetChanged();

    }


    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }


}

package bunke.DirectPoll.View;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import bunke.DirectPoll.R;

public class RecyclerAdapterPolls extends RecyclerView.Adapter<RecyclerAdapterPolls.ViewHolder> {

    private List<LinkedHashMap<String,String>> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    private List<String> seenQuestions;

    // data is passed into the constructor
    RecyclerAdapterPolls(Context context, LinkedHashMap<String,String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = new ArrayList<>();
        seenQuestions = new ArrayList<>();
        if (!data.isEmpty()) {
            mData.add(data);
            seenQuestions.add(data.get("question"));
        }
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LinkedHashMap<String, String> entry = mData.get(position);
        if (entry.isEmpty()) {
            Log.d("RecyclerAdapterPolls", "------------------Entry is null------------------------");
            return;
        }
        holder.keyTextView.setText("Question : ");
        holder.valueTextView.setText(entry.get("question"));
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
    public String[] getItem(int id) {
        if (id > mData.size()) return null;
        Log.d("RecyclerAdapterPolls", "getItem called with id " + id);
        Log.d("RecyclerAdapterPolls", "getItem called with mData " + mData.toString());;
        Log.d("RecyclerAdapterPolls", "getItem called with mData.get(id) " + mData.get(id).toString());
        LinkedHashMap<String, String> item = mData.get(id);
        ArrayList<String> optionsList = new ArrayList<>();
        //two need to be here at the very least itll never get called otherwise
        optionsList.add(item.get("option1"));
        optionsList.add(item.get("option2"));
        if ( item.get("option3") != null) optionsList.add(item.get("option3"));
        //options[0] = item.get("option1");
        //options[1] = item.get("option2");
        //options[2] = item.get("option 3");
        return optionsList.toArray(new String[0]);
    }

    public String getHost(int id) {
        return mData.get(id).get("hostAddress");
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public void updateData(LinkedHashMap<String,String> data){
        data.entrySet().removeIf(entry -> Objects.equals(entry.getKey(), "service"));
        if (seenQuestions.contains(data.get("question"))) {
            Log.d("RecyclerAdapterPolls", "Question already seen - skipping");
            return;
        }
        seenQuestions.add(data.get("question"));
        if (mData.size() == 0) {
            mData.add(0, data);
            notifyDataSetChanged();
            return;
        }
        mData.add(data);
        Log.d("RecyclerAdapterPolls", "Data updated with " + data);
        notifyDataSetChanged();
    }

    public void deleteEntry(String host) {
        for (LinkedHashMap<String,String> map : mData) {
            if (map.get("hostAddress").equals(host)) {
                mData.remove(map);
                notifyDataSetChanged();
                return;
            }
        }
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}

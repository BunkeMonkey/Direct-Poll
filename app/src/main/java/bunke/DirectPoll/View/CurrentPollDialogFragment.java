package bunke.DirectPoll.View;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Objects;

import bunke.DirectPoll.Model.Poll;
import bunke.DirectPoll.R;

public class CurrentPollDialogFragment extends DialogFragment{

    private Poll poll;

    private String[] pollDataAsString;

    public CurrentPollDialogFragment(Poll poll){
        this.poll = poll;
        this.pollDataAsString = buildPollInfo(poll);
    }


    public interface NoticeDialogListener {
        public void onDialogPositiveClick(CurrentPollDialogFragment dialog, Poll poll);

        public void onDialogNegativeClick(CurrentPollDialogFragment dialog);


    }


    CurrentPollDialogFragment.NoticeDialogListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (CurrentPollDialogFragment.NoticeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    "PastPollActivity must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(poll.getQuestion());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_votepoll, null);
        builder.setView(dialogView).setPositiveButton("Close Poll", (dialog, id) -> {
            listener.onDialogPositiveClick(CurrentPollDialogFragment.this, poll );

        }).setNegativeButton("Back", (dialog, id) -> {
            listener.onDialogNegativeClick(CurrentPollDialogFragment.this);
        }).setItems(pollDataAsString, (dialog, which) -> {
            //we dont need on click events here so this can stay empty
        });
        return builder.create();
    }

    private String[] buildPollInfo(Poll poll){
        ArrayList<String> pollData = new ArrayList<>();
        pollData.add("Total Votes: " + poll.getTotalVotes());
        for (int i = 0; i < poll.getOptions().length; i++){
            if (poll.getOptions()[i] != null || !Objects.equals(poll.getOptions()[i], "")) {
                pollData.add(poll.getOptions()[i] + " Votes: " + poll.getVotes()[i]);
            }
        }

        return pollData.toArray(new String[0]);
    }
}



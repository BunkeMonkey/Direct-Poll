package bunke.DirectPoll.View;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import bunke.DirectPoll.R;

public class CreatePollDialogFragment extends DialogFragment {


    public interface NoticeDialogListener {
        public void onDialogPositiveClick(CreatePollDialogFragment dialog);
        public void onDialogNegativeClick(CreatePollDialogFragment dialog);


    }

    private EditText questionField;
    private EditText option1Field;
    private EditText option2Field;
    private EditText option3Field;

    private String question;
    private String option1;
    private String option2;
    private String option3;


    NoticeDialogListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface.
        try {
            // Instantiate the NoticeDialogListener so you can send events to
            // the host.
            listener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface. Throw exception.
            throw new ClassCastException(
                    "MainActivity must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_createpoll, null);
        builder.setView(dialogView).setPositiveButton("Create", (dialog, id) -> {
            questionField = dialogView.findViewById(R.id.pollTitle);
            option1Field = dialogView.findViewById(R.id.PollOption);
            option2Field = dialogView.findViewById(R.id.PollOption2);
            option3Field = dialogView.findViewById(R.id.PollOption3);

            question = questionField.getText().toString();
            option1 = option1Field.getText().toString();
            option2 = option2Field.getText().toString();
            option3 = option3Field.getText().toString();



            listener.onDialogPositiveClick(CreatePollDialogFragment.this);

        }).setNegativeButton("Cancel", (dialog, id) -> {
            listener.onDialogNegativeClick(CreatePollDialogFragment.this);
        });
        return builder.create();
    }


    public String getQuestion() {
        return question;
    }

    public String getOption1() {
        return option1;
    }

    public String getOption2() {
        return option2;
    }

    public String getOption3() {
        return option3;
    }
}
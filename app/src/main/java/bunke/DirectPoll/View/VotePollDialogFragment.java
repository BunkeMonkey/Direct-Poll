package bunke.DirectPoll.View;

import android.app.Dialog;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import bunke.DirectPoll.R;

public class VotePollDialogFragment extends DialogFragment {


    private String options[];

    private String host;


    public VotePollDialogFragment(String options[], String host) {
        this.options = options;
        this.host = host;
    }


    public interface NoticeDialogListener {
        public void onDialogPositiveClick(VotePollDialogFragment dialog, String choice, String host);

        public void onDialogNegativeClick(VotePollDialogFragment dialog);


    }


    VotePollDialogFragment.NoticeDialogListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface.
        try {
            // Instantiate the NoticeDialogListener so you can send events to
            // the host.
            listener = (VotePollDialogFragment.NoticeDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface. Throw exception.
            throw new ClassCastException(
                    "MainActivity must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_votepoll, null);
        builder.setView(dialogView).setPositiveButton("Cast Vote", (dialog, id) -> {
            listener.onDialogPositiveClick(VotePollDialogFragment.this,  options[((AlertDialog) dialog).getListView().getCheckedItemPosition()], host);

        }).setNegativeButton("Cancel", (dialog, id) -> {
            listener.onDialogNegativeClick(VotePollDialogFragment.this);
        }).setSingleChoiceItems(options, -1, (dialog, which) -> {
            // The 'which' argument contains the index position
            // of the selected item
        });
        return builder.create();
    }

    public String getHostAddress() {
        return host;
    }
}



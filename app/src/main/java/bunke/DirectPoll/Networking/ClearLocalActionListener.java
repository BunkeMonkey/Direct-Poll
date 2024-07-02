package bunke.DirectPoll.Networking;

import android.Manifest;
import android.annotation.SuppressLint;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.HashMap;
import java.util.LinkedHashMap;

import bunke.DirectPoll.Control.PollManager;
import bunke.DirectPoll.Model.Poll;
import bunke.DirectPoll.View.MainActivity;


public class ClearLocalActionListener implements WifiP2pManager.ActionListener {
    WifiP2pDnsSdServiceRequest serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
    LinkedHashMap<String, String> pollHostInfo = new LinkedHashMap<>();

    private String address;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("MissingPermission")
    public ClearLocalActionListener(WifiP2pManager manager, WifiP2pManager.Channel channel) {
        this.manager = manager;
        this.channel = channel;

        //pollHostInfo.put("address", address);

    }

    private WifiP2pManager manager;

    private WifiP2pManager.Channel channel;


    boolean isHost;

    @SuppressLint("MissingPermission")
    @Override
    public void onSuccess() {
        // Code for success
        if (DirectNetworkManager.getOfferStatus()) {

            pollHostInfo = buildPollHostInfo();
            Log.d("ClearLocalActionListener", "Poll Host Info: " + pollHostInfo.toString());
            manager.addLocalService(channel, WifiP2pDnsSdServiceInfo.newInstance("_DirectPoll", "_presence._tcp", pollHostInfo),
                    new AddLocalServiceListener(manager, channel));
            Log.d("ClearLocalActionListener", "Success");
        }
    }

    @Override
    public void onFailure(int reason) {
        // Code for failure
        DirectNetworkManager.setOfferStatus(false);
        Log.d("ClearLocalActionListener", "Failure" + reason);
    }

    public LinkedHashMap<String, String> buildPollHostInfo() {
        LinkedHashMap<String, String> pollHostInfo = new LinkedHashMap<>();
        Poll poll = PollManager.getPollManager().getPoll();
        pollHostInfo.put("service", "_DirectPoll");
       // pollHostInfo.put("address", "nothing for now");
        pollHostInfo.put("question", poll.getQuestion());
        //pollHostInfo.put("optionCount", String.valueOf(poll.getOptionCount()));
        pollHostInfo.put("option 1", poll.getOptions()[0]);
        pollHostInfo.put("option 2", poll.getOptions()[1]);
       // pollHostInfo.put("option 3", poll.getOptions()[2]);
        return pollHostInfo;
    }
}

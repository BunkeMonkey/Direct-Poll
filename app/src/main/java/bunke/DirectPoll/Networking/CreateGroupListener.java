package bunke.DirectPoll.Networking;

import android.annotation.SuppressLint;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

public class CreateGroupListener implements WifiP2pManager.ActionListener {
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;

    public CreateGroupListener(WifiP2pManager manager, WifiP2pManager.Channel channel) {
        this.manager = manager;
        this.channel = channel;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onSuccess() {
        Log.d( "CreateGroupListener", "Group created");
        manager.requestGroupInfo(channel, new GroupInfoListener());

    }

    @Override
    public void onFailure(int reason) {
        Log.d( "CreateGroupListener", "Group creation failed: " + reason);
        DirectNetworkManager.setOfferStatus(false);

    }
}

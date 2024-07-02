package bunke.DirectPoll.Networking;

import android.annotation.SuppressLint;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

public class AddServiceRequestListener  implements WifiP2pManager.ActionListener{

    private WifiP2pManager manager;

    private WifiP2pManager.Channel channel;

    public AddServiceRequestListener( WifiP2pManager manager, WifiP2pManager.Channel channel) {
        this.manager = manager;
        this.channel = channel;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onSuccess() {
        if (DirectNetworkManager.getSearchStatus() ) {
            Log.d("startDiscService3", "Adding service Request Success");
            manager.discoverServices(channel, new DiscoverServicesListener());
        }
    }

    @Override
    public void onFailure(int reason) {
        DirectNetworkManager.setSearchStatus(false);
        Log.d("startDiscService3", "Adding service Request failed" + reason);
    }


}

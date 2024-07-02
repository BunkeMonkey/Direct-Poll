package bunke.DirectPoll.Networking;

import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

public class DiscoverServicesListener implements WifiP2pManager.ActionListener{
    @Override
    public void onSuccess() {
        if (DirectNetworkManager.getSearchStatus()) {
            Log.d("Discover Services Listener", "Service discovery success");
        }
    }

    @Override
    public void onFailure(int reason) {
        DirectNetworkManager.setSearchStatus(false);
        Log.d("startDiscService3", "service discovery Failure: " + reason);


    }
}

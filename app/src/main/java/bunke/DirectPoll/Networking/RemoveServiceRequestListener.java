package bunke.DirectPoll.Networking;

import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.util.Log;

public class RemoveServiceRequestListener implements WifiP2pManager.ActionListener{

    private WifiP2pManager manager;

    private WifiP2pManager.Channel channel;
    WifiP2pDnsSdServiceRequest serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();



    public RemoveServiceRequestListener(WifiP2pManager manager, WifiP2pManager.Channel channel) {
        this.manager = manager;
        this.channel = channel;
    }

    @Override
    public void onSuccess() {
        if (DirectNetworkManager.getSearchStatus()) {
            Log.d("startDiscService3", "removed request success");
            manager.addServiceRequest(channel, serviceRequest, new AddServiceRequestListener(manager, channel));
        }
    }

    @Override
    public void onFailure(int reason) {
        DirectNetworkManager.setSearchStatus(false);
        Log.d("startDiscService3", "removed request failed" + reason);
    }
}

package bunke.DirectPoll.Networking;

import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;

public class GroupInfoListener implements WifiP2pManager.GroupInfoListener{
    @Override
    public void onGroupInfoAvailable(WifiP2pGroup group) {
        if (group != null) {
            DirectNetworkManager.setGroup(group);
        }
    }
}

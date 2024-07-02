package bunke.DirectPoll.Networking;

import android.annotation.SuppressLint;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import bunke.DirectPoll.View.MainActivity;

public class ServiceTxtRecordListener implements WifiP2pManager.DnsSdTxtRecordListener {

    private String groupOwnerAddress;
    private WifiP2pManager manager;

    private WifiP2pManager.Channel channel;

    public ServiceTxtRecordListener(WifiP2pManager manager, WifiP2pManager.Channel channel) {
        this.manager = manager;
        this.channel = channel;
    }


    @Override
    public void onDnsSdTxtRecordAvailable(String fullDomainName, Map<String, String> record, WifiP2pDevice srcDevice) {
        Log.d("serviceTxtListener", "DnsSdTxtRecord available - " + record.get("question"));
        if (Objects.equals(record.get("service"), "_DirectPoll")) {
            record = buildPollHostInfo((HashMap<String, String>) record);
            record.put("hostAddress", srcDevice.deviceAddress);
            DirectNetworkManager.getNetworkManager(null,null,null).updateRecyclerAdapter((LinkedHashMap<String, String>) record);
        }
    }

    public LinkedHashMap<String, String> buildPollHostInfo(HashMap<String,String> record) {
        LinkedHashMap<String, String> pollHostInfo = new LinkedHashMap<>();
        pollHostInfo.put("question", record.get("question"));
        pollHostInfo.put("option1", record.get("option 1"));
        pollHostInfo.put("option2", record.get("option 2"));
        pollHostInfo.put("option3", record.get("option 3"));
        return pollHostInfo;
    }


}  //class end

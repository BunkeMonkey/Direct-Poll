package bunke.DirectPoll.Networking;

import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.util.Log;

public class ServiceResponseListener implements WifiP2pManager.DnsSdServiceResponseListener{

    public ServiceResponseListener() {
    }
    @Override
    public void onDnsSdServiceAvailable(String instanceName, String registrationType, WifiP2pDevice srcDevice) {
        Log.d("prepareDiscService3", "Service available - " + instanceName + " " + registrationType);


    }
}

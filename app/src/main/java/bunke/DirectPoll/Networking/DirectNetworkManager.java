package bunke.DirectPoll.Networking;


import android.annotation.SuppressLint;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.util.Log;

import java.util.LinkedHashMap;

import bunke.DirectPoll.View.MainActivity;

public class DirectNetworkManager {


    private WifiP2pManager manager;

    private WifiP2pManager.Channel channel;

    WifiP2pDnsSdServiceRequest serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();

    boolean isHost;

    private static DirectNetworkManager instance;

    private static boolean searchStatus = false;

    private static boolean offerStatus = false;

    private MainActivity mainActivity;
    private static boolean sendingVote = false;

    private static WifiP2pGroup group = null;

    private DirectNetworkManager(WifiP2pManager manager, WifiP2pManager.Channel channel, MainActivity mainActivity) {
        this.manager = manager;
        this.channel = channel;
        this.mainActivity = mainActivity;
    }

    public static DirectNetworkManager getNetworkManager(WifiP2pManager manager, WifiP2pManager.Channel channel, MainActivity mainActivity) {
        if (instance == null) {
            if (manager == null || channel == null || mainActivity == null) {
                throw new IllegalArgumentException("All parameters must be non-null");
            }
            instance = new DirectNetworkManager(manager, channel, mainActivity);
        }
        return instance;
    }

    protected static void setGroup(WifiP2pGroup group) {
        group = group;
    }


    @SuppressLint({"NewApi", "MissingPermission"})
    public void startOfferingService() {
        manager.clearLocalServices(channel, new ClearLocalActionListener(manager, channel));
        manager.createGroup(channel, new CreateGroupListener(manager, channel));
        offerStatus = true;
        Log.d("DirectNetworkManager", "Offering service");
    }

    public void startPrepDiscoveringService() {
        searchStatus = true;
        ServiceResponseListener serviceResponseListener = new ServiceResponseListener();
        ServiceTxtRecordListener serviceTxtRecordListener = new ServiceTxtRecordListener(manager, channel);
        manager.setDnsSdResponseListeners(channel, serviceResponseListener, serviceTxtRecordListener);
        Log.d("DirectNetworkManager", "Prep discovering service");
        Thread t = new Thread(mServiceDiscoveringRunnable);
        t.start();
    }

    public void startDiscoveringService() {
        if (searchStatus) {
            manager.removeServiceRequest(channel, serviceRequest, new RemoveServiceRequestListener(manager, channel));
        }
    }

    private Runnable mServiceDiscoveringRunnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                startDiscoveringService();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    protected static void setOfferStatus(boolean status) {
        offerStatus = status;
    }

    protected static void setSearchStatus(boolean status) {
        searchStatus = status;
    }

    public static boolean getOfferStatus() {
        return offerStatus;
    }

    public static boolean getSearchStatus() {
        return searchStatus;
    }

    public static boolean getSendingVote() {
        return sendingVote;
    }

    public static WifiP2pGroup getGroup() {
        return group;
    }

    public void updateRecyclerAdapter(LinkedHashMap<String, String> record) {
        mainActivity.updateRecyclerAdapter(record);
    }


    @SuppressLint("MissingPermission")
    public void castVote(String hostAddress, String choice) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = hostAddress;
        config.wps.setup = WpsInfo.PBC;

        try {
            manager.connect(channel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.d("DirectNetworkManager", "Connection successful");
                }

                @Override
                public void onFailure(int reason) {
                    Log.d("DirectNetworkManager", "Connection failed");
                }
            });
        }catch (Exception e){
            Log.d("DirectNetworkManager", "Exception in casting vote" + e.getMessage());
        }

        sendingVote = true;
        PollHostingEndpoint send = new PollHostingEndpoint(hostAddress, choice);


    }
}

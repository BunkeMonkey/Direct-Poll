package bunke.DirectPoll.Networking;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import bunke.DirectPoll.View.MainActivity;

public class AddLocalServiceListener implements WifiP2pManager.ActionListener{

    private WifiP2pManager manager;

    private WifiP2pManager.Channel channel;

    @SuppressLint("MissingPermission")
    public AddLocalServiceListener(WifiP2pManager manager, WifiP2pManager.Channel channel) {
        this.manager = manager;
        this.channel = channel;
    }


    @Override
    public void onSuccess() {
        if (DirectNetworkManager.getOfferStatus()) {
            Log.d("addLocalService", "Success");
            Thread t = new Thread(mServiceBroadcastingRunnable);
            t.start();
        }

    }

    @Override
    public void onFailure(int reason) {
        Log.d("addLocalService", "Failure" + reason);
        DirectNetworkManager.setOfferStatus(false);

    }

    private Runnable mServiceBroadcastingRunnable = new Runnable() {
        @SuppressLint("MissingPermission")
        @Override
        public void run() {

            while (true) {
                manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d("mServiceBroadcastingRunnable", "Discovering peers success");
                    }

                    @Override
                    public void onFailure(int error) {
                        DirectNetworkManager.setOfferStatus(false);
                        Log.d("mServiceBroadcastingRunnable", "Discovering peers failed");
                    }
                });
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();

                }
            }
        }
    };
}

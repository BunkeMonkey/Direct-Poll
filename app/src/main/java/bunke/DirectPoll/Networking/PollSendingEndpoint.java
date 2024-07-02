package bunke.DirectPoll.Networking;

import android.net.wifi.p2p.WifiP2pInfo;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class PollSendingEndpoint implements Runnable{

    private String address;

    private String data;

    private WifiP2pInfo peer;
    private String peerName;

    private int PORT;

    public PollSendingEndpoint() {

    }
    @Override
    public void run() {
        Socket sock = null;
        try {
            sock = new Socket(address,PORT);

        Log.d("PollProtocolMachine", "Data sent to: " + peerName);
        DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
        dos.writeUTF(data);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

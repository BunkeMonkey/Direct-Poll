package bunke.DirectPoll.Networking;



import android.net.wifi.p2p.WifiP2pInfo;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import bunke.DirectPoll.Control.PollManager;
import bunke.DirectPoll.Model.Poll;

public class PollHostingEndpoint implements Runnable {

    private static ServerSocket servSock;

    private static final int PORT = 7777;

    private String address;

    private String data;

    private WifiP2pInfo peer;
    private String peerName;

    private String dataToSend;

    private boolean owner;

    private final int TIMEOUT = 10000;
    private int receivedConnections = 0;

    //TODO: make this class a singleton



    public PollHostingEndpoint(String address, String data) {
        this.address = address;
        this.data = data;
        this.owner = false;

    }

    public PollHostingEndpoint(String address){
        this.address = address;
        this.owner = true;
    }


    @Override
    public void run() {
        try {

            if (owner) {
                while (!Thread.interrupted()){
                    //InetAddress addr = InetAddress.getByName(address);
                    // servSock.getLocalSocketAddress();
                    servSock = new ServerSocket(PORT);
                    servSock.setSoTimeout(10000);
                    //Log.d("PollHostingEndpoint", servSock.getLocalSocketAddress().toString() + addr.toString());
                    try {
                        Log.d("PollHostingEndpoint", "Waiting for client");
                        Socket clientSock = servSock.accept();
                        DataInputStream dis = new DataInputStream(clientSock.getInputStream());
                        receivedConnections++;
                        String recvData = dis.readUTF();
                        Log.d("PollProtocolMachine", "Data received: " + recvData);

                        PollManager pollManager = PollManager.getPollManager();
                        pollManager.receiveVote(recvData);
                        servSock.close();
                        clientSock.close();
                        //return;

                    } catch (SocketException sockex) {
                        Log.d("PollProtocolMachine", "Socket timed out");
                        servSock.close();
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }else{
                InetAddress addr = InetAddress.getByName(address);
                Socket sock = new Socket(addr, PORT);
                DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
                dos.writeUTF(data);
                sock.close();

            }
            } catch(IOException ex){
                ex.printStackTrace();
            }
        }


        public int getReceivedConnections(){
            return receivedConnections;
        }
        }






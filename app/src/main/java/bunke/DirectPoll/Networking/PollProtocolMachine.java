package bunke.DirectPoll.Networking;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;

import bunke.DirectPoll.Model.Poll;

public class PollProtocolMachine {
    private OutputStream os;

    private InputStream is;

    public PollProtocolMachine(InputStream is ){
        this.is = is;
    }

    public PollProtocolMachine(OutputStream os){
        this.os = os;
    }

    public void serializePoll(Poll poll) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(poll);

    }

    public Poll deserializePoll() throws IOException {
        try {
            ObjectInputStream ois = new ObjectInputStream(is);
            return (Poll) ois.readObject();
        } catch (ClassNotFoundException | StreamCorruptedException e) {
            return null;
        }
    }
}

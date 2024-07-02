package bunke.DirectPoll.Control;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;

import bunke.DirectPoll.Model.Poll;
import bunke.DirectPoll.View.MainActivity;

public class SavePollToDisk {




    public void savePollToDisk(Poll poll, FileOutputStream fos) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(poll);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Poll loadPollFromDisk(FileInputStream fis ){
        try (ObjectInputStream ois = new ObjectInputStream(fis) ){
            return (Poll) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void deletePollFromDisk(){
    }
}

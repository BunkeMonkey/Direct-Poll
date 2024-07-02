package bunke.DirectPoll.Control;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;

import bunke.DirectPoll.Model.Poll;

public class PollExporter {

    public void exportPoll(Poll poll, OutputStream os){
        PrintWriter pw = new PrintWriter(os);
        pw.println(poll.getQuestion() + " : " + poll.getTotalVotes());
        for (int i = 0; i < poll.getOptionCount(); i++){
            pw.println(poll.getOptions()[i] + " : " + poll.getVotes()[i]);
        }
        LocalDate date = LocalDate.now();
        pw.println("Exported on: " + date);
        pw.close();

    }
}

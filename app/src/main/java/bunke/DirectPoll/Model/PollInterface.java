package bunke.DirectPoll.Model;

public interface PollInterface {

    public String getQuestion();

    public int getTotalVotes();

    public int getOptionCount();

    public String[] getOptions();

    public int[] getVotes();

    public boolean isClosed();



}

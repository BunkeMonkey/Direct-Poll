package bunke.DirectPoll.Model;

import java.io.Serializable;

public class Poll implements PollInterface, Serializable {

    private String question;
    private int totalVotes;
    private int optionCount;
    private String[] options;
    private int[] votes;

    boolean isClosed = false;

    public Poll(String question, int optionCount, String[] options){
        this.question = question;
        this.optionCount = optionCount;
        this.options = options;
        this.votes = new int[optionCount];
        this.totalVotes = 0;
    }
    @Override
    public String getQuestion() {
        return question;
    }

    @Override
    public int getTotalVotes() {
        return totalVotes;
    }

    @Override
    public int getOptionCount() {
        return optionCount;
    }

    @Override
    public String[] getOptions() {
        return options;
    }

    @Override
    public int[] getVotes() {
        return votes;
    }

    @Override
    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closing){
        isClosed = closing;
    }

    public void setVotes(int optionIndex){
        this.votes[optionIndex]++;
        totalVotes++;
    }


}

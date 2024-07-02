package bunke.DirectPoll.Control;

import bunke.DirectPoll.Model.Poll;

public interface PollManagerInterface {

    public Poll createPoll(String question, int optionCount, String options[]);

    public Poll getPoll();

    public void receiveVote(String recvData);

    public void closePoll();




}

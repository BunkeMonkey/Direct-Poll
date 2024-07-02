package bunke.DirectPoll.Control;

import java.util.Arrays;

import bunke.DirectPoll.Model.Poll;

public class PollManager implements PollManagerInterface{

    private Poll currentPoll;
    private static PollManager instance;

    private PollManager(Poll poll) {
        currentPoll = poll;
    }

    private PollManager(){

    }

    public static PollManager getPollManager(){
        if (instance == null){
            instance = new PollManager();
        }
        return instance;
    }


    @Override
    public Poll createPoll(String question, int optionCount, String[] options) throws IllegalArgumentException{
        if (question == null || question.isEmpty()){
            throw new IllegalArgumentException("Question cannot be empty");
        }
        if (options[0] == null || options[0].isEmpty() || options[1] == null || options[1].isEmpty() ){
            throw new IllegalArgumentException("Poll must have at least two options");
        }

        if (currentPoll == null){
            currentPoll = new Poll(question, optionCount, options);
            return currentPoll;
        }
        return currentPoll;
    }

    @Override
    public Poll getPoll() {
        return currentPoll;
    }


    @Override
    public void closePoll() {
        if (currentPoll != null){
            currentPoll.setClosed(true);
        }
    }

    @Override
    public void receiveVote(String recvData) {
        if (currentPoll != null && !currentPoll.isClosed()){
            for (String option : currentPoll.getOptions()){
                if (option.equals(recvData)){
                    int optionIndex = Arrays.asList(currentPoll.getOptions()).indexOf(option);
                    currentPoll.setVotes(optionIndex);
                }
            }
        }
    }
}

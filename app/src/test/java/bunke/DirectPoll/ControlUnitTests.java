package bunke.DirectPoll;

import bunke.DirectPoll.Control.PollManager;
import bunke.DirectPoll.Model.Poll;

import static org.junit.Assert.*;

import org.junit.Test;

public class ControlUnitTests {

    @Test
    public void controllerPollClosedTest(){
        PollManager pollManager = PollManager.getPollManager();
        Poll currentPoll = pollManager.createPoll("test poll", 3, new String[]{"option1", "option2", "option3"});
        pollManager.closePoll();

        assertTrue(currentPoll.isClosed());
        pollManager.receiveVote("option1");
        assertFalse(pollManager.getPoll().getTotalVotes() > 0);
        assertFalse(currentPoll.getVotes()[0] > 0);

    }

    @Test
    public void controllerPollReceiveVoteTest(){
        PollManager pollManager = PollManager.getPollManager();
        Poll currentPoll = pollManager.createPoll("test poll", 3, new String[]{"option1", "option2", "option3"});
        pollManager.receiveVote("option1");

        assertEquals(1, currentPoll.getVotes()[0]);
        assertEquals(1,pollManager.getPoll().getTotalVotes());


        pollManager.receiveVote("optionDoesntExist");
        assertEquals(1, currentPoll.getVotes()[0]);
        assertEquals(1,pollManager.getPoll().getTotalVotes());
        assertEquals(0, currentPoll.getVotes()[1]);
        assertEquals(0, currentPoll.getVotes()[2]);
    }



}

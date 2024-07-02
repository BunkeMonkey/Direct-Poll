package bunke.DirectPoll;

import org.junit.Test;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import bunke.DirectPoll.Model.Poll;
import bunke.DirectPoll.Networking.PollHostingEndpoint;
import bunke.DirectPoll.Networking.PollProtocolMachine;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class NetworkingUnitTests {
    @Test
    public void pollProtocolSeralizeTest() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        PollProtocolMachine sender = new PollProtocolMachine(bos);

        Poll poll = new Poll("test poll", 5, new String[]{"option1", "option2", "option3"});
        sender.serializePoll(poll);



        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        PollProtocolMachine receiver = new PollProtocolMachine(bis);

        Poll receivedPoll = receiver.deserializePoll();

        assertEquals(poll.getQuestion(), receivedPoll.getQuestion());
        assertEquals(poll.getOptions().length, receivedPoll.getOptions().length);
        for (int i = 0; i < poll.getOptions().length; i++) {
            assertEquals(poll.getOptions()[i], receivedPoll.getOptions()[i]);
        }


    }

    @Test
    public void pollProtocolMalformattedTest() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        PollProtocolMachine sender = new PollProtocolMachine(bos);
        bos.write(5);
        bos.write(19);

        Poll poll = new Poll("test poll", 5, new String[]{"option1", "option2", "option3"});
        sender.serializePoll(poll);

        bos.write(0);

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        PollProtocolMachine receiver = new PollProtocolMachine(bis);

        Poll receivedPoll = receiver.deserializePoll();

        assertNull(receivedPoll);
    }


    @Test
    public void pollHostTest() throws IOException, InterruptedException {
        PollHostingEndpoint pollHost = new PollHostingEndpoint("localhost");
        Thread t = new Thread(pollHost);
        int receivedConnections = pollHost.getReceivedConnections();
        t.start();
        PollHostingEndpoint pollClient = new PollHostingEndpoint("localhost", "test data");
        Thread t2 = new Thread(pollClient);
        t2.start();

        //wait here for the sockets to connect
        Thread.sleep(1000);
        assertNotEquals(receivedConnections, pollHost.getReceivedConnections());

        receivedConnections = pollHost.getReceivedConnections();

        Thread t3 = new Thread(pollClient);
        t3.start();

        //wait here again
        Thread.sleep(1000);
        assertNotEquals(receivedConnections, pollHost.getReceivedConnections());

        //Socket sock = new Socket("localhost", 7777);

    }
}
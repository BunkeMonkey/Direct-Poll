package bunke.DirectPoll;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import bunke.DirectPoll.Control.PollManager;
import bunke.DirectPoll.Model.Poll;
import bunke.DirectPoll.Networking.PollHostingEndpoint;
import bunke.DirectPoll.Networking.PollProtocolMachine;
import bunke.DirectPoll.View.MainActivity;
import bunke.DirectPoll.View.PastPollsActivity;

public class EndToEndTests {

    @Rule // This line is crucial for the test to work, it defines the activity to be tested
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public ActivityScenarioRule<PastPollsActivity> pastPollScenario =
            new ActivityScenarioRule<>(PastPollsActivity.class);


    @Test
    public void flowTest() throws InterruptedException, IOException {
        //first we copy everything from the integration test and then add onto it

        onView(withId(R.id.createPollButton)).check(matches(isDisplayed()));
        onView(withId(R.id.createPollButton)).perform(click());

        //type the poll into the text field
        onView(withId(R.id.pollTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.pollTitle)).perform(typeText("test poll"));
        onView(withId(R.id.PollOption)).check(matches(isDisplayed()));
        onView(withId(R.id.PollOption)).perform(typeText("option1"));
        onView(withId(R.id.PollOption2)).check(matches(isDisplayed()));
        onView(withId(R.id.PollOption2)).perform(typeText("option2"));

        onView(withId(android.R.id.button1)).perform(click());

        PollManager pollman = PollManager.getPollManager();

        //assert that the pollmanager info is correct
        assert(pollman.getPoll().getOptionCount() == 2);
        assert (pollman.getPoll().getQuestion().equals("test poll"));
        assert (pollman.getPoll().getOptions()[0].equals("option1"));
        assert (pollman.getPoll().getOptions()[1].equals("option2"));
        assert (pollman.getPoll().getTotalVotes() == 0);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PollProtocolMachine sender = new PollProtocolMachine(bos);
        sender.serializePoll(pollman.getPoll());

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        PollProtocolMachine receiver = new PollProtocolMachine(bis);
        Poll receivedPoll = receiver.deserializePoll();


        //assert that info is still correct after serialization
        assert(receivedPoll.getQuestion().equals("test poll"));
        assert(receivedPoll.getOptions().length == 2);
        assert(receivedPoll.getOptions()[0].equals("option1"));
        assert(receivedPoll.getOptions()[1].equals("option2"));

        LinkedHashMap<String, String> polls = new LinkedHashMap<>();
        polls = makePollToHashMap(pollman.getPoll());
        //needs to be copied again for lambda expression
        LinkedHashMap<String, String> finalPolls = polls;
        activityScenarioRule.getScenario().onActivity(activity -> {

            activity.updateRecyclerAdapter(finalPolls);
        });


        //vote on the poll
        onView(withId(R.id.recViewPolls)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withText("option2")).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withText("option2")).perform(click());
        onView(withText("Cast Vote")).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withText("Cast Vote")).perform(click());

        //transfer the vote via localhost sockets because this test is without wifi direct
        PollHostingEndpoint voteReceiver = new PollHostingEndpoint("localhost");
        PollHostingEndpoint  voteSender = new PollHostingEndpoint("127.0.0.1","option2");
        Thread receiverThread = new Thread(voteReceiver);
        Thread senderThread = new Thread(voteSender);
        receiverThread.start();
        senderThread.start();

        //give it time to finish setting up the sockets
        Thread.sleep(1000);

        //assert that the vote was received
        assert(pollman.getPoll().getVotes()[1] == 1);
        assert(pollman.getPoll().getTotalVotes() == 1);

        //click the currentpoll button and check if info is right#
        onView(withId(R.id.currentPollButton)).perform(click());
        onView(withText("test poll")).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withText("option1 Votes: 0")).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withText("option2 Votes: 1")).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withText("Total Votes: 1")).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withText("Close Poll")).inRoot(isDialog()).perform(click());

        //assert that manager closed the poll
        assert(pollman.getPoll().isClosed());

        //assert that the newpoll button is there again and other is gone

        //click the pastpoll button and check if info is right
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.fabPastPolls)).perform(click());

        //define the scenario for the pastPolls and check if the poll is there
        ActivityScenario<PastPollsActivity> pastPollScenario =
                ActivityScenario.launch(PastPollsActivity.class);



            //Thread.sleep(1000);

            AtomicInteger recyclercount = new AtomicInteger();
            pastPollScenario.onActivity(activity -> {
                recyclercount.set(activity.getAdapter().getItemCount());

            });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                onView(withId(R.id.recViewPastPolls)).perform(RecyclerViewActions.actionOnItemAtPosition(recyclercount.get() - 1, click()));

                onView(withText("Total Votes: 1")).inRoot(isDialog()).check(matches(isDisplayed()));
                onView(withText("option1 Votes: 0")).inRoot(isDialog()).check(matches(isDisplayed()));
                onView(withText("option2 Votes: 1")).inRoot(isDialog()).check(matches(isDisplayed()));

                onView(withText("Cancel"))
                        .perform(click());

    }


    private LinkedHashMap<String, String> makePollToHashMap(Poll poll) {
        LinkedHashMap<String, String> polls = new LinkedHashMap<>();
        polls.put("question", poll.getQuestion());
        polls.put("option1", poll.getOptions()[0]);
        polls.put("option2", poll.getOptions()[1]);
        polls.put("hostAddress", "localhost");
        return polls;
    }
}

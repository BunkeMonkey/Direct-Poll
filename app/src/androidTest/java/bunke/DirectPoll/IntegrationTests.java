package bunke.DirectPoll;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import android.Manifest;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Rule;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import bunke.DirectPoll.Control.PollManager;
import bunke.DirectPoll.Model.Poll;
import bunke.DirectPoll.Networking.DirectNetworkManager;
import bunke.DirectPoll.Networking.PollHostingEndpoint;
import bunke.DirectPoll.Networking.PollProtocolMachine;
import bunke.DirectPoll.View.MainActivity;
import bunke.DirectPoll.View.RecyclerAdapterPolls;

public class IntegrationTests {

    @Rule // This line is crucial for the test to work, it defines the activity to be tested
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Rule public GrantPermissionRule permissionRuleLocation = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);
    @Rule public GrantPermissionRule permissionRuleWifi = GrantPermissionRule.grant(Manifest.permission.NEARBY_WIFI_DEVICES);

    @Test
    public void testIfSearchStarted() throws InterruptedException {
        onView(withId(R.id.discService)).check(matches(isDisplayed()));
        onView(withId(R.id.discService)).perform(click());
        Thread.sleep(1000);
        assert(DirectNetworkManager.getSearchStatus());
    }

    @Test
    public void localVoteTest() throws IOException, InterruptedException {
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


    }

    @Test
    public void isItemRemovedAfterVote(){

        AtomicInteger items = new AtomicInteger();
        activityScenarioRule.getScenario().onActivity(activity -> {
                    //do the vote pressing and then see if recycler items have gotten less
                    RecyclerView recyclerView = activity.findViewById(R.id.recViewPolls);
                    RecyclerAdapterPolls adapter = (RecyclerAdapterPolls) recyclerView.getAdapter();
                    items.set(adapter.getItemCount());
                });

            onView(withId(R.id.recViewPolls)) .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
            onView(withText("Red")).inRoot(isDialog()).check(matches(isDisplayed()));

            onData(allOf(is(instanceOf(String.class)), is("Red")))
                    .perform(click());

            onView(withText("Cast Vote"))
                    .perform(click());


        activityScenarioRule.getScenario().onActivity(activity -> {
            //do the vote pressing and then see if recycler items have gotten less
            RecyclerView recyclerView = activity.findViewById(R.id.recViewPolls);
            RecyclerAdapterPolls adapter = (RecyclerAdapterPolls) recyclerView.getAdapter();
            assert(adapter.getItemCount() == items.get() - 1);
        });
    }

    @Test
    public void testPastPolls(){

        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.fabPastPolls)).perform(click());

        onView(withId(R.id.recViewPastPolls)).check(matches(isDisplayed()));

        onView(withId(R.id.recViewPastPolls)) .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withText("Total Votes: 0")).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withText("ChatGPT Votes: 0")).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withText("Gemini Votes: 0")).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withText("Claude Votes: 0")).inRoot(isDialog()).check(matches(isDisplayed()));

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

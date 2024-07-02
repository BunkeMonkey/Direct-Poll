package bunke.DirectPoll;


import static androidx.constraintlayout.motion.widget.Debug.getName;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.isNotActivated;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;

import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;


import org.junit.Test;
import org.junit.Rule;

import bunke.DirectPoll.Networking.DirectNetworkManager;
import bunke.DirectPoll.View.MainActivity;
import bunke.DirectPoll.View.PastPollsActivity;
import bunke.DirectPoll.View.RecyclerAdapterPolls;
import bunke.DirectPoll.View.SettingsActivity;

public class ViewUnitTests {



    @Rule // This line is crucial for the test to work, it defines the activity to be tested
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    //@Rule
    //public IntentsTestRule<MainActivity> mainActivityRule = new IntentsTestRule<>(MainActivity.class);


    @Test
    public void pollNotMultipleTimesInRecycler() {

        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.onActivity(activity -> {
            // Access activity instance here
            RecyclerView recyclerView = activity.findViewById(R.id.recViewPolls);
            RecyclerAdapterPolls adapter = (RecyclerAdapterPolls) recyclerView.getAdapter();
            assert adapter != null;
            assert(adapter.getItemCount() == 1);
        });



        onView(withId(R.id.recViewPolls)).check(matches(isDisplayed()));
        onView(withId(R.id.recViewPolls)).check(matches(isEnabled()));


    }



    @Test
    public void subMenuCheck(){
        onView(withId(R.id.fab)).check(matches(isDisplayed()));
        onView(withId(R.id.fab)).check(matches(isClickable()));

        //check that theyre NOT displayed before the button is clicked
        onView(withId(R.id.fabSettings)).check(matches(isNotActivated()));
        onView(withId(R.id.fabPastPolls)).check(matches(isNotActivated()));
        onView(withId(R.id.dimmingView)).check(matches(isNotActivated()));


        //click the button and then check if theyre displayed and clickable
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.fabSettings)).check(matches(isDisplayed()));
        onView(withId(R.id.fabPastPolls)).check(matches(isDisplayed()));
        onView(withId(R.id.dimmingView)).check(matches(isDisplayed()));
        onView(withId(R.id.fabSettings)).check(matches(isClickable()));
        onView(withId(R.id.fabPastPolls)).check(matches(isClickable()));
        onView(withId(R.id.dimmingView)).check(matches(isClickable()));
    }


    @Test
    public void VotePollDialogFragmentTest(){

        onView(withId(R.id.recViewPolls)) .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withText("Red")).inRoot(isDialog()).check(matches(isDisplayed()));

        onData(allOf(is(instanceOf(String.class)), is("Red")))
                .perform(click());

        onView(withText("Cast Vote"))
                .perform(click());


        assert(DirectNetworkManager.getSendingVote());




    }

    @Test
    public void pastPollsTest() {
            onView(withId(R.id.fab)).perform(click());
            onView(withId(R.id.fabPastPolls)).perform(click());

            Intents.init();


            ActivityScenario<PastPollsActivity> pastPollsScenario =
                    ActivityScenario.launch(PastPollsActivity.class);
            pastPollsScenario.onActivity(pastPollsActivity -> {
                assertEquals(Lifecycle.State.RESUMED, pastPollsActivity.getLifecycle().getCurrentState());
            });

            Intents.release();

    }
    @Test
    public void settingsTest() {
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.fabSettings)).perform(click());

        Intents.init();


        ActivityScenario<SettingsActivity> settingsScenario =
                ActivityScenario.launch(SettingsActivity.class);
        settingsScenario.onActivity(settingsActivity -> {
            assertEquals(Lifecycle.State.RESUMED, settingsActivity.getLifecycle().getCurrentState());
        });

        Intents.release();

    }




    @Test
    public void createPollDialogFragmentTest(){
            onView(withId(R.id.createPollButton))
                    .check(matches(isDisplayed()));
            onView(withId(R.id.createPollButton))
                    .check(matches(isEnabled()));


            onView(withId(R.id.createPollButton))
                    .perform(click());

                onView(withId(android.R.id.content))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        }




    }





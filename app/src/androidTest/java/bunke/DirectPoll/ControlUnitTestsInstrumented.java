package bunke.DirectPoll;

import static org.junit.Assert.fail;

import android.util.Log;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.Objects;

import bunke.DirectPoll.Control.PollExporter;
import bunke.DirectPoll.Control.SavePollToDisk;
import bunke.DirectPoll.Model.Poll;
import bunke.DirectPoll.View.MainActivity;
import bunke.DirectPoll.View.PastPollsActivity;

public class ControlUnitTestsInstrumented {

    @Rule // This line is crucial for the test to work, it defines the activity to be tested
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public ActivityScenarioRule<PastPollsActivity> pastPollsScenario = new ActivityScenarioRule<>(PastPollsActivity.class);

    @Test
    public void savePollToDiskAndLoadItTest(){
        Poll poll = new Poll("test poll", 5, new String[]{"option1", "option2", "option3"});

        activityScenarioRule.getScenario().onActivity(activity -> {
            try {
                activity.writePoll(poll);
            } catch (FileNotFoundException e) {
                fail("File not found exception thrown");
            }


            Poll loadedPoll = activity.readPoll();
            if (loadedPoll == null){
                fail("Poll not loaded");
            }
            assert(Objects.equals(loadedPoll.getQuestion(), poll.getQuestion()));
            assert(loadedPoll.getOptions().length == poll.getOptions().length);
            for(int i = 0; i < poll.getOptions().length; i++){
                assert(Objects.equals(loadedPoll.getOptions()[i], poll.getOptions()[i]));
            }


        });
    }


    @Test
    public void exportPollTest(){

        activityScenarioRule.getScenario().onActivity(activity -> {
            Poll poll = new Poll("test poll", 3, new String[]{"option1", "option2", "option3"});


            try (FileOutputStream fos = activity.openFileOutput("exportedPoll", 0)){
                activity.openFileOutput("exportedPoll", 0);
                PollExporter exporter = new PollExporter();
                exporter.exportPoll(poll, fos);
            } catch (FileNotFoundException e) {
                fail("File not found exception thrown");
            } catch (IOException e) {
                fail("IO exception thrown");
            }

            String[] filenames = activity.fileList();
            for (String filename : filenames) {
                if (filename.startsWith("exported")) {
                    File file = new File(activity.getFilesDir(), filename);
                    assert (file.exists());
                    assert (file.length() > 0);
                }
                }
        });


    }
    }


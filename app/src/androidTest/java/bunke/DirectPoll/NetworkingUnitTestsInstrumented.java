package bunke.DirectPoll;

import android.Manifest;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Rule;
import org.junit.Test;

import bunke.DirectPoll.Control.PollManager;
import bunke.DirectPoll.Networking.DirectNetworkManager;
import bunke.DirectPoll.View.MainActivity;

public class NetworkingUnitTestsInstrumented {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Rule public GrantPermissionRule permissionRuleLocation = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);
    @Rule public GrantPermissionRule permissionRuleWifi = GrantPermissionRule.grant(Manifest.permission.NEARBY_WIFI_DEVICES);


    @Test
    public void networkStartTestAndGroupTest(){
        activityScenarioRule.getScenario().onActivity(activity -> {
            DirectNetworkManager networkManager = activity.getNetworkManager();
            networkManager.startOfferingService();

            PollManager pollManager = PollManager.getPollManager();
            pollManager.createPoll("test poll", 3, new String[]{"option1", "option2", "option3"});


            //need to wait for the network to start and set everything it needs to
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            assert(DirectNetworkManager.getOfferStatus());
            assert(!DirectNetworkManager.getSearchStatus());

            //can often fail because group creation can spontaneously fail for no reason
            assert(DirectNetworkManager.getGroup() != null);
        });


    }


}

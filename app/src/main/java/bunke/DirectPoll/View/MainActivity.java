package bunke.DirectPoll.View;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import bunke.DirectPoll.Control.PollExporter;
import bunke.DirectPoll.Control.PollManager;
import bunke.DirectPoll.Control.SavePollToDisk;
import bunke.DirectPoll.Model.Poll;
import bunke.DirectPoll.Networking.DirectNetworkManager;
import bunke.DirectPoll.R;
import bunke.DirectPoll.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements CreatePollDialogFragment.NoticeDialogListener
        , VotePollDialogFragment.NoticeDialogListener,
        RecyclerAdapterPolls.ItemClickListener,
        CurrentPollDialogFragment.NoticeDialogListener{

    private AppBarConfiguration appBarConfiguration;

    WifiP2pManager manager;
    WifiP2pManager.Channel channel;

    LinkedHashMap<String, String> pollHostInfo = new LinkedHashMap<>();

    DirectNetworkManager networkManager;
    PollManager pollManager;


    WifiP2pDnsSdServiceRequest serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();

    private FloatingActionButton fabMain, fabSettings, fabPastPolls;

    private View dimmingView;

    private boolean isFabOpen = false;
    RecyclerAdapterPolls adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);


        networkManager = DirectNetworkManager.getNetworkManager(manager, channel, this); //new DirectNetworkManager(manager, channel);
        pollManager = PollManager.getPollManager();



        bunke.DirectPoll.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        RecyclerView recyclerView = findViewById(R.id.recViewPolls);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerAdapterPolls(this, pollHostInfo);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);





        //Floating action buttons
        fabMain = findViewById(R.id.fab);
        fabPastPolls = findViewById(R.id.fabPastPolls);
        fabSettings = findViewById(R.id.fabSettings);
        dimmingView = findViewById(R.id.dimmingView);
        //setSupportActionBar(binding.toolbar);

        insertTestData(null);
        insertTestData(null);
        insertTestData(null);
        insertTestData(null);
        insertTestData(null);

        try {
            writeTestData();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        //pollHostInfo.put("question", "What is your favorite color?");







        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        //appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        fabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFabOpen){
                    closeFabMenu();
                }else{
                    openFabMenu();
                }

            }
        });

        fabPastPolls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, PastPollsActivity.class);
                myIntent.putExtra("key", "value"); //Optional parameters
                MainActivity.this.startActivity(myIntent);
            }
        });

        dimmingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFabMenu();
            }
        });

        fabSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, SettingsActivity.class);
                myIntent.putExtra("key", "value"); //Optional parameters
                MainActivity.this.startActivity(myIntent);
            }
        });
    }

    private void writeTestData() throws FileNotFoundException {
        Poll poll = new Poll("Soll ich Döner gönnen?", 2,new String[]{"jaaaa man", "nah lass stecken"});
        Poll poll2 = new Poll("Which AI chatbot is your bestie?", 3,new String[]{"ChatGPT", "Gemini", "Claude"});
        Poll poll3 = new Poll("Do you have Autism?", 3,new String[]{"Yes (Diagnosed) ", "No", "Undiagnosed, but im pretty sure"});
        Poll poll4 = new Poll("Would you Lose?", 2,new String[]{"Nah", "I´d Win"});
        writePoll(poll2);
        writePoll(poll);
        writePoll(poll3);
        writePoll(poll4);

    }


    private void closeFabMenu(){
        isFabOpen = false;
        fabSettings.setVisibility(View.GONE);
        fabPastPolls.setVisibility(View.GONE);
        dimmingView.setVisibility(View.GONE);

    }

    private void openFabMenu(){
        isFabOpen = true;
            fabSettings.setVisibility(View.VISIBLE);
            fabPastPolls.setVisibility(View.VISIBLE);
            dimmingView.setVisibility(View.VISIBLE);



    }


    public void onCurrentPoll(View view){
        CurrentPollDialogFragment dialog = new CurrentPollDialogFragment(pollManager.getPoll());
        dialog.show(getSupportFragmentManager(), "CurrentPollDialog");
    }


    public void onCreateNewPoll(View view) {
         if (!checkPermissions()){
             return;
         }
        if (!isWifiEnabled()){
            wifiNotEnabledDialog();
            return;
        }
        if (!isLocationEnabled()){
            locationNotEnabledDialog();
            return;
        }
        CreatePollDialogFragment dialog = new CreatePollDialogFragment();
        dialog.show(getSupportFragmentManager(), "createPollDialog");

    }
    public void onstartService(View view) {
        Log.d("MainActivity", "Starting service");
        networkManager.startOfferingService();
    }

    public void onDiscoverService(View view) {
        if (!checkPermissions()){
            return;
        }
        Log.d("MainActivity", "Discovering services");
        networkManager.startPrepDiscoveringService();
        //prepareDiscService3();
        //startDiscService3();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

/*
    public void exportPoll(Poll poll){
        try (FileOutputStream fos = openFileOutput("exported_" + poll.getQuestion() +".txt", Context.MODE_PRIVATE)){
            PollExporter exporter = new PollExporter();
            exporter.exportPoll(poll, fos);
        }catch (FileNotFoundException e){
            Log.d("MainActivity", "File not found" + e.getMessage());
        } catch (IOException e) {
            Log.d("MainActivity", "Exception in poll writing" + e.getMessage());
        }
    }


 */


    public void writePoll(Poll poll) throws FileNotFoundException {
        try (FileOutputStream fos = openFileOutput("poll" + poll.getQuestion(), Context.MODE_PRIVATE) ){
            SavePollToDisk savePollToDisk = new SavePollToDisk();
            savePollToDisk.savePollToDisk(poll, fos);
        }catch (FileNotFoundException e){
           Log.d("MainActivity", "File not found" + e.getMessage());
        } catch (IOException e) {
            Log.d("MainActivity", "Exception in poll writing" + e.getMessage());
        }
    }

    public  Poll readPoll(){

        String[] filenames = fileList();
        for (String filename : filenames) {
            if (filename.startsWith("poll")) {
                try (FileInputStream fis = openFileInput(filename) ) {
                    SavePollToDisk savePollToDisk = new SavePollToDisk();
                    return savePollToDisk.loadPollFromDisk(fis);
                } catch (IOException e) {
                    Log.d("MainActivity", "File not found" + e.getMessage());

                }
            }
        }

        return null;
    }

    @Override
    public void onDialogPositiveClick(CreatePollDialogFragment dialog) {
        CreatePollDialogFragment createPollDialogFragment = (CreatePollDialogFragment) dialog;
        String question = createPollDialogFragment.getQuestion();
        String option1 = createPollDialogFragment.getOption1();
        String option2 = createPollDialogFragment.getOption2();
        String option3 = createPollDialogFragment.getOption3();
        String[] options;
        if (option3 == null || option3.isEmpty()){
            options = new String[]{option1, option2};
        }else {
            options = new String[]{option1, option2, option3};
        }

        try {
            pollManager.createPoll(question, options.length, options);
        }catch(IllegalArgumentException e){
            Snackbar.make(findViewById(R.id.mainActivity), Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_LONG).show();
            return;
        }
        fabSettings.setVisibility(View.GONE);
        fabPastPolls.setVisibility(View.GONE);
        //TODO: make one button vis the other invisible
        findViewById(R.id.createPollButton).setVisibility(View.GONE);
        findViewById(R.id.currentPollButton).setVisibility(View.VISIBLE);
        networkManager.startOfferingService();



    }

    @Override
    public void onDialogNegativeClick(CreatePollDialogFragment dialog) {

    }



    //when the user makes a choice in the dialog
    @Override
    public void onDialogPositiveClick(VotePollDialogFragment dialog, String choice, String host) {
            Log.d("Voting on Poll", "User chose: " + choice);
            adapter.deleteEntry(host);
            networkManager.castVote(dialog.getHostAddress(), choice);

    }

    @Override
    public void onDialogNegativeClick(VotePollDialogFragment dialog) {

    }

    public void updateRecyclerAdapter(LinkedHashMap<String, String> data) {
        adapter.updateData(data);
    }

    @Override
    public void onItemClick(View view, int position) {
        String options[] = adapter.getItem(position);
        String hostAddress = adapter.getHost(position);
        Log.d("MainActivity", "Options are " + Arrays.toString(options));
        if (options[0] == null || options[0].isEmpty()) {
            Log.d("MainActivity", "Options are null");
            return;
        }
        VotePollDialogFragment dialog = new VotePollDialogFragment(options, hostAddress);
        dialog.show(getSupportFragmentManager(), "VotePollDialog");
    }


    private void insertTestData(LinkedHashMap<String, String> pollHostInfo2){
        if (pollHostInfo2 != null){
            adapter.updateData(pollHostInfo2);
        }
        pollHostInfo2 = new LinkedHashMap<>();
        pollHostInfo2.put("question", "What is your favorite color?");
        pollHostInfo2.put("option1", "Red");
        pollHostInfo2.put("option2", "Green");
        pollHostInfo2.put("option3", "Blue");
        pollHostInfo2.put("hostAddress", "test");
        adapter.updateData(pollHostInfo2);
    }

    public DirectNetworkManager getNetworkManager(){
        return networkManager;
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions();
            return false;
        }
        return true;
    }

    private void requestPermissions(){


        //if the build is larger than 33, then we need to request the NEARBY_WIFI_DEVICES permission otherwise just location

        if (Build.VERSION.SDK_INT >= 33){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.NEARBY_WIFI_DEVICES},
                    1);
        }else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
    }

    private Boolean isWifiEnabled() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return wifiManager != null && wifiManager.isWifiEnabled();

    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null){
            return false;
        }
        return  locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void wifiNotEnabledDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This app requires Wi-Fi to function. Please enable Wi-Fi in settings.")
                .setCancelable(false)
                .setPositiveButton("Enable", (dialog, id) -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)))
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void locationNotEnabledDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This app requires Location services to function. Please enable Location services in settings.")
                .setCancelable(false)
                .setPositiveButton("Enable", (dialog, id) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onDialogPositiveClick(CurrentPollDialogFragment dialog, Poll poll)  {
        //dialog thats supposed to close the poll
        pollManager.closePoll();
        try {
            writePoll(poll);
        }catch(FileNotFoundException e){
            Log.d("MainActivity", "File not found" + e.getMessage());
            Toast.makeText(this, "Poll couldnt be saved", Toast.LENGTH_LONG).show();
        }
        findViewById(R.id.createPollButton).setVisibility(View.VISIBLE);
        findViewById(R.id.currentPollButton).setVisibility(View.GONE);

    }

    @Override
    public void onDialogNegativeClick(CurrentPollDialogFragment dialog) {

    }
}
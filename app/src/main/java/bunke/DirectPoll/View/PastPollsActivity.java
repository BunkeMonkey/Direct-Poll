package bunke.DirectPoll.View;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import bunke.DirectPoll.Control.PollExporter;
import bunke.DirectPoll.Control.SavePollToDisk;
import bunke.DirectPoll.Model.Poll;
import bunke.DirectPoll.R;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays.*;

public class PastPollsActivity extends AppCompatActivity implements RecyclerAdapterPastPolls.ItemClickListener, PastPollDialogFragment.NoticeDialogListener {

    private RecyclerAdapterPastPolls adapter;

    private ArrayList<Poll> polls = new ArrayList<>();

    private final int CREATE_FILE = 1;

    private Poll selectedPoll = null;

     @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_past_polls);

         polls = readPolls();
         RecyclerView recyclerView = findViewById(R.id.recViewPastPolls);
         recyclerView.setLayoutManager(new LinearLayoutManager(this));
         adapter = new RecyclerAdapterPastPolls(this, polls);
         adapter.setClickListener(this::onItemClick);
         recyclerView.setAdapter(adapter);
         adapter.notifyDataSetChanged();


        }


    private ArrayList<Poll> readPolls(){

        String filenames[] = fileList();
        ArrayList<Poll> polls = new ArrayList<>();
        for (String filename : filenames) {
            if (filename.startsWith("poll")) {
                try (FileInputStream fis = openFileInput(filename) ) {
                    SavePollToDisk savePollToDisk = new SavePollToDisk();
                    polls.add(savePollToDisk.loadPollFromDisk(fis));
                } catch (IOException e) {
                    Log.d("MainActivity", "File not found" + e.getMessage());

                }
            }
        }

        return polls;
    }

    @Override
    public void onItemClick(View view, int position) {
        //item click from the recycler
        Poll poll = adapter.getItem(position);
        PastPollDialogFragment dialog = new PastPollDialogFragment(poll);
        dialog.show(getSupportFragmentManager(), "PastPollDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(PastPollDialogFragment dialog, Poll poll) {
        //TODO: this means to export it -> implement logic here
       // exportPoll(poll, Uri.parse("content://bunke.DirectPoll.provider/"));
        createFile(Uri.parse("content://bunke.DirectPoll.provider/"));
        selectedPoll = poll;
    }

    @Override
    public void onDialogNegativeClick(PastPollDialogFragment dialog) {

    }

    private void createFile(Uri pickerInitialUri){

         Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, "exported_poll.txt");
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);
        startActivityForResult(intent, CREATE_FILE);

    }
 /*
    public void exportPoll(Poll poll, Uri uri){
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, "exported_poll.txt");
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
        startActivity(intent);




        try (FileOutputStream fos = openFileOutput("exported_" + poll.getQuestion() + poll.hashCode() + ".txt", Context.MODE_PRIVATE)){
            PollExporter exporter = new PollExporter();
            exporter.exportPoll(poll, fos);
        }catch (FileNotFoundException e){
            Log.d("MainActivity", "File not found" + e.getMessage());
        } catch (IOException e) {
            Log.d("MainActivity", "Exception in poll writing" + e.getMessage());
        }
    }

  */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREATE_FILE && resultCode == RESULT_OK) {
            Uri uri = data.getData();

            if (uri != null) {
                PollExporter exporter = new PollExporter();
                try ( OutputStream os = getContentResolver().openOutputStream(uri)){
                    //actual call to write something
                    exporter.exportPoll(selectedPoll, os);
                    Toast.makeText(this, "Poll exported", Toast.LENGTH_SHORT).show();


                } catch (FileNotFoundException e) {
                    Log.d("PastPollsActivity", "File not found" + e.getMessage());
                } catch (IOException e) {
                    Log.d("PastPollsActivity", "IOException" + e.getMessage());
                }

            } else {
                Log.d("MainActivity", "Uri is null");
            }
        }
    }


    public RecyclerAdapterPastPolls getAdapter() {
        return adapter;
    }
}

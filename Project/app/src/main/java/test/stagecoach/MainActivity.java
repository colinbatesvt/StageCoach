package test.stagecoach;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.media.MediaPlayer.OnCompletionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends ActionBarActivity {

    private MediaRecorder recorder;
    private String fileName;
    private Toast crispyButtery;
    private TextView status;
    private MediaPlayer mPlayer;
    private boolean isRecording;
    private boolean hasRecorded;
    private TextView txtSpeechInput;
    private Button transcriptButton;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtSpeechInput = (TextView) findViewById(R.id.transcript);
        transcriptButton = (Button) findViewById(R.id.transcriptButton);

        transcriptButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        fileName = getFilesDir() + "/test.3gp";
        crispyButtery = new Toast(this);
        status = (TextView) findViewById(R.id.textView2);
        mPlayer = new MediaPlayer();
        isRecording = false;
        hasRecorded = false;

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            recorder.prepare();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
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

    public void start(View v)
    {
        try {
            recorder.start();
            setStatus("Recording...");
            isRecording = true;
        }

        catch (Exception e) {
            crispyButtery.makeText(this, "Oh jeez couldn't start the recorder", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    //when we get around to application exit, release media recorder
    public void stop(View v)
    {
        try {
            if (recorder != null) {
                if (isRecording) {
                    recorder.stop();
                    recorder.reset();
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    recorder.setOutputFile(fileName);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                }
                setStatus("");
                hasRecorded = true;
            }
        }
        catch (Exception e) {
            crispyButtery.makeText(this, "Oh jeez couldn't stop the recorder or save the file or something", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void play(View v) {
        if (!mPlayer.isPlaying())
            try {
                setStatus("Playing back...");
                getWindow().getDecorView().findViewById(android.R.id.content).invalidate();
                mPlayer.setDataSource(fileName);
                mPlayer.prepare();
                mPlayer.start();
                mPlayer.setOnCompletionListener(new OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer media) {
                        mPlayer.reset();
                        setStatus("");
                    }
                });
            } catch (IOException e) {
                crispyButtery.makeText(this, "Can't play recording for some reason. That's freaky yo.", Toast.LENGTH_SHORT).show();
                Log.e("poop", "prepare() failed");
            }
    }

    //if you leave str blank, the default setting is "Waiting for you..."
    private void setStatus(String str)
    {
        if (str.equals(""))
            status.setText("Waiting for you...");
        else
            status.setText(str);
    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtSpeechInput.setText(result.get(0));
                }
                break;
            }

        }
    }
}

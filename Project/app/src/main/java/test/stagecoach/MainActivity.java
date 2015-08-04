package test.stagecoach;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.media.MediaPlayer.OnCompletionListener;

import java.io.IOException;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fileName = getFilesDir() + "/test.3gp";
        crispyButtery = new Toast(this);
        status = (TextView) findViewById(R.id.textView2);
        mPlayer = new MediaPlayer();
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

    MediaRecorder recorder;
    String fileName;
    Toast crispyButtery;
    TextView status;
    MediaPlayer mPlayer;

    public void start(View v)
    {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            recorder.prepare();
            recorder.start();
            setStatus("Recording...");
        }

        catch (Exception e) {
            crispyButtery.makeText(this, "Oh jeez couldn't start the recorder", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void stop(View v)
    {
        try {
            recorder.stop();
            recorder.release();
            setStatus("");
        }
        catch (Exception e) {
            crispyButtery.makeText(this, "Oh jeez couldn't stop the recorder or save the file or something", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void play(View v)
    {
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
                }});
            }
        catch (IOException e) {
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
}

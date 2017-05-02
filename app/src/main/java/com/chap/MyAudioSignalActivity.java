package com.chap;

import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.media.AudioRecord;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MyAudioSignalActivity extends AppCompatActivity implements View.OnClickListener {

    ToggleButton recordButton;
    Boolean ToggleButtonState;
    private AudioRecord AudioRec = null;
    public int BufSize;
    private Thread Record_Thread=null;
    private boolean isRecording = false;
 //   public Q audio_buffer=new Q(20000);
    private static final int AUDIO_SOURCE=android.media.MediaRecorder.AudioSource.MIC;
    public static final int SAMPLE_RATE = 44100;
    private static final int CHANNEL_CONFIG = android.media.AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = android.media.AudioFormat.ENCODING_PCM_16BIT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_audio_signal);

        recordButton = (ToggleButton) findViewById(R.id.toggleButton);
        recordButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    Toast.makeText(MyAudioSignalActivity.this, "TODO", Toast.LENGTH_SHORT).show();
                  /*  BufSize=AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
                    isRecording=true;
                    Record_Thread=new Thread(new Runnable() {
                        public void run() {
                            RecAudio();
                        }
                    },"AudioRecord Thread");
                    Record_Thread.start();*/
                } else {
                    Toast.makeText(MyAudioSignalActivity.this, "OR NOT TODO", Toast.LENGTH_SHORT).show();
                 /*   if (null != AudioRec) {
                        isRecording = false;
                        boolean retry = true;
                        while (retry) {
                            try {
                                Record_Thread.join();
                                retry = false;
                            } catch (InterruptedException e) {}
                        }
                        AudioRec.stop();
                        AudioRec.release();
                        AudioRec = null;
                    }*/

                }
            }
        });
    }
        public void RecAudio(){
            byte[] AudioBytes=new byte[BufSize];
            int[] AudioSamples=new int[BufSize/2];

            AudioRec = new AudioRecord(AUDIO_SOURCE,SAMPLE_RATE,CHANNEL_CONFIG,AUDIO_FORMAT,BufSize);
            AudioRec.startRecording();

            while (isRecording)
            {
                AudioRec.read(AudioBytes, 0, BufSize);
                int r=0;
                for (int i=0; i<AudioBytes.length-2;i+=2)
                {
                    if (AudioBytes[i]<0)
                        AudioSamples[r]=AudioBytes[i]+256;
                    else
                        AudioSamples[r]=AudioBytes[i];
                    AudioSamples[r]=AudioSamples[r]+256*AudioBytes[i+1];
                    r++;
                }
            }

        }
        @Override
        protected void onStop() {
            super.onStop();
        }


    @Override
    public void onClick(View v) {

    }
}

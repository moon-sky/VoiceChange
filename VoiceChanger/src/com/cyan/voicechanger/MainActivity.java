package com.cyan.voicechanger;

import java.io.File;
import java.io.IOException;

import org.tecunhuman.AndroidJNI;
import org.tecunhuman.ExtAudioRecorder;

import mp3.Main;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	private Button start;
	private Button stop;
	private Button change;
	private Button convert;

	private EditText l;
	private EditText h;
	private EditText s;

	ExtAudioRecorder extAudioRecorder = null;
	final String dataPath = "/sdcard/APKS_1/";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		File f=new File(dataPath);
		if(!f.exists())
			f.mkdirs();

		System.loadLibrary("soundtouch");
		System.loadLibrary("soundstretch");

		start = (Button) findViewById(R.id.startRecord);
		stop = (Button) findViewById(R.id.stopRecord);
		change = (Button) findViewById(R.id.changeVoice);
		convert = (Button) findViewById(R.id.convert2Mp3);

		l = (EditText) findViewById(R.id.l);
		h = (EditText) findViewById(R.id.h);
		s = (EditText) findViewById(R.id.s);

		start.setOnClickListener(this);

		stop.setOnClickListener(this);

		change.setOnClickListener(this);

		convert.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.startRecord:
			if (extAudioRecorder == null)
				extAudioRecorder = extAudioRecorder.getInstanse(false);
			extAudioRecorder.setOutputFile(dataPath + "source.wav");
			extAudioRecorder.prepare();
			extAudioRecorder.start();
			break;
		case R.id.stopRecord:
			extAudioRecorder.stop();
			extAudioRecorder.release();
			extAudioRecorder = null;
			break;
		case R.id.changeVoice:
			AndroidJNI.soundStretch.process(dataPath + "source.wav", dataPath
					+ "target.wav", Float.parseFloat(l.getText().toString()),
					Float.parseFloat(h.getText().toString()),
					Float.parseFloat(s.getText().toString()));
			Toast.makeText(MainActivity.this, "change OK", 0).show();
			break;
		case R.id.convert2Mp3:

			mp3.Main main = new mp3.Main();
			try {
				main.convertWAVToMP3(dataPath + "target");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			File mp3File = new File(dataPath + "target.mp3");
			if (mp3File.length() == 0) {
				int retryTimes = 0;
				while (true) {
					// sleep(2000);
					mp3File = new File(dataPath + "target.mp3");
					if (mp3File.length() > 0 || retryTimes == 50)
						break;
					retryTimes++;
				}
				if (mp3File.length() == 0) {
					try {
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				}
			}
			Toast.makeText(MainActivity.this, "convert OK", 0).show();

			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (extAudioRecorder != null) {
			extAudioRecorder.release();
			extAudioRecorder = null;
		}
	}
}

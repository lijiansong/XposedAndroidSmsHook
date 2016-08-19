package com.example.xposed_sms;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

	private SettingsHelper mSettings;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);
		mSettings = new SettingsHelper(this);
		mSettings.setString("flag", "1");
		
	}	
}

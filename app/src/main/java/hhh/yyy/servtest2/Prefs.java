package hhh.yyy.servtest2;


import android.os.Bundle;
import android.preference.PreferenceActivity;

import yyy.hhh.servtest2.R;

public class Prefs extends PreferenceActivity 
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
	    super.onCreate(savedInstanceState);
	    addPreferencesFromResource(R.xml.settings);
	}
}
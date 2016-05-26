package de.jungierek.terracontrol2;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import de.jungierek.terracontrol2.model.TerraInfo;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    // ========================================================================================
    
    private final static String DEBUG_TAG = "SettingsActivity";
    
    private int terrNo;

    @Override
    public void onCreate ( Bundle savedInstanceState ) {

        super.onCreate ( savedInstanceState );
        
        // für welches TerraInfo
        Bundle extras = getIntent ().getExtras ();
        terrNo = extras.getInt ( TerraInfo.BUNDLE_NAME_TERRA_NO );

        addPreferencesFromResource ( R.xml.preferences );

    }

    public void onSharedPreferenceChanged ( SharedPreferences sharedPreferences, String key ) {

        Log.i ( DEBUG_TAG, "pref=" + sharedPreferences + " key=" + key );

        if ( key.equals ( "pref_key_widget_update" )) {
            
        }
        // TODO KOnstanten definieren
        else if (key.startsWith ( "pref_key_arduino_display" )) {
            
            boolean b = sharedPreferences.getBoolean ( key, true );
            String parm = b ? "on" : "off";
            
            if (key.endsWith ( "_temp_hum" )) {
                parm = "temp=" + parm; 
            }
            else if (key.endsWith ( "_date_time" )) {
                parm = "date=" + parm; 
            }
            else if (key.endsWith ( "_light" )) {
                parm = "light=" + parm; 
            }
            else if (key.endsWith ( "_since" )) {
                parm = "since=" + parm; 
            }
            else if (key.endsWith ( "_freemem" )) {
                parm = "mem=" + parm; 
            }
            else if (key.endsWith ( "_debug" )) {
                parm = "debug=" + parm; 
            }
            else {
                Log.i ( DEBUG_TAG, "unknown key=" + key );
            }
            
            new AsyncHttpDisplay ( terrNo, parm ).execute ();

        }

    }

    @Override
    protected void onResume () {

        Log.i ( DEBUG_TAG, "onResume" );
        super.onResume ();
        getPreferenceScreen ().getSharedPreferences ().registerOnSharedPreferenceChangeListener ( this );

    }

    @Override
    protected void onPause () {

        Log.i ( DEBUG_TAG, "onPause" );
        super.onPause ();
        getPreferenceScreen ().getSharedPreferences ().unregisterOnSharedPreferenceChangeListener ( this );
        
    }

    // ========================================================================================

    private class AsyncHttpDisplay extends TerraHttpAsyncTask {
        
        /**
         * @param terrNo
         * @param url
         */
        protected AsyncHttpDisplay ( int terrNo, String parms ) {
            
            super ( terrNo, getString ( R.string.ext_url_display ) + "?" + parms, SettingsActivity.this );

        }

    }

}

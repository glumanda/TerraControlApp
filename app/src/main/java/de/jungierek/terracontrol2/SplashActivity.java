package de.jungierek.terracontrol2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Der Startbildschirm mit Anzeige eines Fotos und einer Versionsinfo.
 * Der Übergang zur Folge-Activity erfolgt nur, wenn Wifi verfügfbar ist.
 * 
 * @author Andreas Jungierek
 * @version 2.0
 */
public class SplashActivity extends Activity {

    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    
    private final static String DEBUG_TAG = "SplashScreen";

    @Override
    public void onCreate ( Bundle savedInstanceState ) {
        
        super.onCreate ( savedInstanceState );
        
        PreferenceManager.setDefaultValues ( this, R.xml.preferences, false );
        
        setContentView ( R.layout.activity_splash );
/*        
        View imageView = findViewById ( R.id.splash_image );
        int alpha = getResources ().getInteger ( R.integer.SPLASH_IMAGE_ALPHA );
        imageView.setAlpha ( alpha / 100.0f );
*/       
        if ( TerraWifi.isHomeWifi ( getApplicationContext () ) ) {
            
            // setup handler to close the splash screen
            new Handler ().postDelayed ( new MainActivityStarter (), getResources ().getInteger ( R.integer.DELAY ) );
            
        }
        else {
            
            Log.i ( DEBUG_TAG, "kein WLAN" );

            AlertDialog.Builder builder = new AlertDialog.Builder ( this );
            
            builder.setMessage ( getString ( R.string.alert_wlan ) )       
            .setCancelable ( false )       
            .setPositiveButton ( getString ( R.string.alert_ok ), new DialogInterface.OnClickListener () {           
                public void onClick ( DialogInterface dialog, int id ) {                
                    SplashActivity.this.finish ();           
                }       
            });       
            
            AlertDialog alert = builder.create ();
            alert.show ();

        }

        
    }

    @Override
    public void onRestoreInstanceState ( Bundle savedInstanceState ) {
        
        if ( savedInstanceState.containsKey ( STATE_SELECTED_NAVIGATION_ITEM ) ) {
            getActionBar ().setSelectedNavigationItem ( savedInstanceState.getInt ( STATE_SELECTED_NAVIGATION_ITEM ) );
        }
        
    }

    @Override
    public void onSaveInstanceState ( Bundle outState ) {
        
        outState.putInt ( STATE_SELECTED_NAVIGATION_ITEM, getActionBar ().getSelectedNavigationIndex () );
        
    }
    
    /**
     * Hilfsklasse f�r einen verz�gerten (Dauer der Anzeige) Start der Folge-Activity.
     *  
     * @author Andreas Jungierek
     * @version 1.0
     */
    private class MainActivityStarter implements Runnable {

        public void run() {

            // start new activity
            startActivity ( new Intent ( getApplicationContext (), TerraControlActivity.class ));
            // close out this activity
            finish (); // remove from history stack!!!
            
            Log.i ( DEBUG_TAG, "Spash-Screen ist geschlossen" );

        }
    }

}

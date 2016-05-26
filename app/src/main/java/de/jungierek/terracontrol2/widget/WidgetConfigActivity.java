package de.jungierek.terracontrol2.widget;

import java.util.ArrayList;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import de.jungierek.terracontrol2.R;
import de.jungierek.terracontrol2.model.TerraInfo;

public class WidgetConfigActivity extends Activity {

    private static final String DEBUG_TAG = "WidgetConfigActivity";

    public static final String PREFS_NAME = "de.jungierek.ics.terracontrol.WidgetConfigActivity";
    public static final String PREF_TERRA_NO = "terranumber_";

    private int widgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private RadioGroup choice;

    public WidgetConfigActivity () {

        super ();

    }
    
    @Override
    public void onCreate ( Bundle savedInstanceState ) {

        super.onCreate ( savedInstanceState );

        // Set the result to CANCELED. This will cause the widget host to cancel
        // out of the widget placement if they press the back button.
        setResult ( RESULT_CANCELED );

        // Set the view layout resource to use.
        setContentView ( R.layout.widget_configure );

        choice = (RadioGroup) findViewById ( R.id.widget_configure_choice );

        String [] terraNames = TerraInfo.getInstance ( this ).getAllNames ();
        for ( int i = 0; i < terraNames.length; i++ ) {
            
            RadioButton button = new RadioButton ( this );
            button.setId ( i );
            button.setText ( terraNames [i] );
            button.setLayoutParams ( new ViewGroup.LayoutParams ( ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT ) );
            button.setTextColor ( getResources ().getColor ( R.color.label ) );
            choice.addView ( button );

        }
        
        

        choice.clearCheck ();
        
        // evtl. Auswahl setzen
        int id = loadTerraNoPref ( this, widgetId );
        if ( id > -1 ) {
            choice.check ( id );
        }
        
        // Button mit ClickListener versorgen
        Button go = (Button) findViewById ( R.id.widget_configure_button );
        go.setOnClickListener ( onClickListener );

        // Find the widget id from the intent.
        Intent intent = getIntent ();
        Bundle extras = intent.getExtras ();
        if ( extras != null ) {
            widgetId = extras.getInt ( AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID );
        }

        // If they gave us an intent without the widget id, just bail.
        if ( widgetId == AppWidgetManager.INVALID_APPWIDGET_ID ) {
            finish ();
        }

    }
    
    private View.OnClickListener onClickListener = new View.OnClickListener () {
        
        public void onClick ( View v ) {
            
            final Context context = WidgetConfigActivity.this;

            int terraNo = choice.getCheckedRadioButtonId ();
             
            if ( terraNo == -1 ) {
                Toast.makeText ( getApplicationContext(), "Terrarium wählen!", Toast.LENGTH_SHORT ).show ();
            }
            else {
                saveWidgetPref ( context, widgetId, terraNo );
                Log.i ( DEBUG_TAG, "widgetId=" + widgetId + " terraNo=" + terraNo );
    
                Intent serviceIntent = new Intent ( context, TerraControlWidgetService.class );
                serviceIntent.putExtra ( TerraControlWidgetService.EXTRA_UPDATE_WIDGET_ID, widgetId );
                context.startService ( serviceIntent );
    
                // Make sure we pass back the original appWidgetId
                Intent resultValue = new Intent ();
                resultValue.putExtra ( AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId );
                setResult ( RESULT_OK, resultValue );
                
                finish ();
                
            }
            
        }
        
    };
    
    // ============================================================================

    private static SharedPreferences getPrefs ( Context context ) {
        
        return context.getSharedPreferences ( PREFS_NAME, Context.MODE_PRIVATE );
        
    }

    private static String getTerraNoPrefKey ( int widgetId ) {

        return PREF_TERRA_NO + widgetId;
    
    }

    // Write terra number to the SharedPreferences object for this widget
    public static void saveWidgetPref ( Context context, int widgetId, int terraNo ) {
        
        SharedPreferences.Editor prefs = getPrefs ( context ).edit ();
        prefs.putInt ( getTerraNoPrefKey ( widgetId ), terraNo );
        prefs.commit ();
        
    }

    // Read terra number from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    public static int loadTerraNoPref ( Context context, int widgetId ) {

        return getPrefs ( context ).getInt ( getTerraNoPrefKey ( widgetId ), -1 );
        
    }
    
    public static void deleteTerraNoPref ( Context context, int widgetId ) {
        
        getPrefs ( context ).edit ().remove ( getTerraNoPrefKey ( widgetId ) ).commit ();

    }

    public static void deleteAllTerraNoPref ( Context context ) {
        
        getPrefs ( context ).edit ().clear ().commit ();

    }

    static void loadAllTerraNoPrefs ( Context context, ArrayList<Integer> widgetIds, ArrayList<Integer> terraNos ) {}

}

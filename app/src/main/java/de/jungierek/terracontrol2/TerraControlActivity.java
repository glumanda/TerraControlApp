package de.jungierek.terracontrol2;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import de.jungierek.terracontrol2.model.HourMinute;
import de.jungierek.terracontrol2.model.TerraInfo;
import de.jungierek.terracontrol2.model.ViewModel;
import de.jungierek.terracontrol2.widget.TerraControlWidgetService;
import de.jungierek.terracontrol2.widget.WidgetConfigActivity;

/**
 * Die Detaildaten eines Terrariums.
 * 
 * @author Andreas Jungierek
 * @version 2.0
 */
public class TerraControlActivity extends Activity implements ActionBar.OnNavigationListener {

    private static final String DEBUG_TAG = "TerraControl";

    private static final String ACTIVITY_STATE_TERRA_MODEL = "de.jungierek.ics.terracontrol.TERRA_MODEL";
    private static final String ACTIVITY_STATE_TERRA_NUMBER = "de.jungierek.ics.terracontrol.TERRA_NUMBER";
    private static final String ACTIVITY_STATE_SELECTED_ITEM = "de.jungierek.ics.terracontrol.SELECTED_ITEM";

    private ViewModel [] terraModels;
    private int terraNo;
    

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void onCreate ( Bundle savedInstanceState ) {

        Log.i ( DEBUG_TAG, "onCreate (obj=" + this + ")" );

        super.onCreate ( savedInstanceState );
        
        // HACK
        if ( false ) WidgetConfigActivity.deleteAllTerraNoPref ( getApplicationContext() );

        setContentView ( R.layout.activity_terra_control );

        // Set up the action bar.
        final ActionBar actionBar = getActionBar ();
        actionBar.setDisplayHomeAsUpEnabled ( false );
        actionBar.setDisplayShowTitleEnabled ( false );
        actionBar.setNavigationMode ( ActionBar.NAVIGATION_MODE_LIST );
        
        
        // Set up the TerraModel Object
        String [] terraNames = TerraInfo.getInstance ( this ).getAllNames ();
        // TODO wenn Namenslist ohne EintrÃ¤ge??? length==0
        
        if ( savedInstanceState != null ) {
            // nach Drehung o.ä.
            terraModels = (ViewModel []) savedInstanceState.getSerializable ( ACTIVITY_STATE_TERRA_MODEL );
            terraNo = savedInstanceState.getInt ( ACTIVITY_STATE_TERRA_NUMBER );
        }
        
        if ( terraModels == null ) {
            terraModels = new ViewModel [terraNames.length];  // erst mal alle Terrarien mit null !!! nur wenn ganz neue Activity
            terraNo = 0;
        }
        
        // Set up the dropdown list navigation in the action bar.
        actionBar.setListNavigationCallbacks (
                // Specify a SpinnerAdapter to populate the dropdown list.
                new ArrayAdapter (
                        actionBar.getThemedContext (),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        terraNames
                        ),
                this );
        
        // Set click-Listener for TimePicker
        findViewById ( R.id.data3 ).setOnClickListener ( new View.OnClickListener() {
            public void onClick ( View v ) {
                Log.i ( DEBUG_TAG, "onClick on data3 -> on_time" );
                new SetTimeDialog ( "on"  ).show ( getFragmentManager(), "timePickerOn" );
            }
        });

        findViewById ( R.id.data4 ).setOnClickListener ( new View.OnClickListener() {
            public void onClick ( View v ) {
                Log.i ( DEBUG_TAG, "onClick on data4 -> on_time" );
                new SetTimeDialog ( "off"  ).show ( getFragmentManager(), "timePickerOff" );
            }
        });

    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume () {
        
        Log.i ( DEBUG_TAG, "onResume" );
        super.onResume ();
        
    }
    
    public boolean onNavigationItemSelected ( int position, long ViewId ) {
        
        // wird auch nach onCreate () mit Index 0 gerufen
        Log.i ( DEBUG_TAG, "pos=" + position );

        // http request, position = terraNumber
        terraNo = position; // fï¿½r refresh
        
        if ( terraModels [terraNo] == null ) {
            // bisher keine Aufruf
            terraModels [terraNo] = new ViewModel ();
            new AsyncHttpRequestValues ( position ).execute ();
            // View wird in onPostExecute versorgt
        }
        else {
            // Daten umschalten
            refreshViewByModel ( terraModels [terraNo] );
        }

        return true;
        
    }

    private static final Object [] [] mapping = new Object [] [] {
        { "temp",    R.id.data1 },
        { "hum",     R.id.data2 },
        { "on",      R.id.data3 },
        { "off",     R.id.data4 },
        { "since",   R.id.data5 },
        { "now",     R.id.data6 },
        { "version", R.id.data7 },
    };

   /**
    * TODO
    *  
    * @param viewModel
    */
    private void refreshViewByModel ( ViewModel model ) {
        
        // TODO Performance verbessern, cachen der Views

        // update data in view
        for ( int i = 0; i < mapping.length; i++ ) {

            String key = (String) mapping [i] [0];
            
            int id = ((Integer) mapping [i] [1]).intValue ();
            View data = (View) findViewById ( id );
            
            TextView label = (TextView) data.findViewById ( R.id.label );
            if ( label != null ) label.setText ( model.getLabel ( key ) );
        
            EditText text = (EditText) data.findViewById ( R.id.value );
            if ( text != null ) text.setText ( model.getDisplayText ( key ) );

        }
        
    }

    @Override
    public void onSaveInstanceState ( Bundle saveState ) {
        
       Log.i ( DEBUG_TAG, "onSaveInstanceState" );

       saveState.putSerializable ( ACTIVITY_STATE_TERRA_MODEL, terraModels );
       saveState.putInt ( ACTIVITY_STATE_TERRA_NUMBER, terraNo );
       
       saveState.putInt ( ACTIVITY_STATE_SELECTED_ITEM, getActionBar ().getSelectedNavigationIndex () );
       
       super.onSaveInstanceState ( saveState );
        
    }

    @Override
    public void onRestoreInstanceState ( Bundle savedInstanceState ) {
        
        Log.i ( DEBUG_TAG, "onRestoreInstanceState" );

        super.onRestoreInstanceState ( savedInstanceState );
        
        if ( savedInstanceState.containsKey ( ACTIVITY_STATE_SELECTED_ITEM ) ) {
            getActionBar ().setSelectedNavigationItem ( savedInstanceState.getInt ( ACTIVITY_STATE_SELECTED_ITEM ) );
        }
  
    }

    @Override
    public boolean onCreateOptionsMenu ( Menu menu ) {
        
        Log.i ( DEBUG_TAG, "onCreateOptionsMenu" );

        getMenuInflater ().inflate ( R.menu.activity_terra_control, menu );
        
        return true;
        
    }

    @Override
    public boolean onOptionsItemSelected ( MenuItem item ) {
        
        Log.i ( DEBUG_TAG, "onOptionsItemSelected: item=" + item );
        
        switch ( item.getItemId ()) {
            
            case android.R.id.home:
                Log.i ( DEBUG_TAG, "HOME" );        
                //NavUtils.navigateUpFromSameTask ( this );
                return true;
                
            case R.id.menu_refresh:
                Log.i ( DEBUG_TAG, "refresh" );        
                // http request
                new AsyncHttpRequestValues ( terraNo ).execute ();
                return true;
                
            case R.id.menu_reboot:
                Log.i ( DEBUG_TAG, "reboot" );        
                new AsyncHttpRequestReboot ( terraNo ).execute ();
                return true;
                
            case R.id.menu_settings:
                Log.i ( DEBUG_TAG, "settings" );
                final Intent intent = new Intent ( getApplicationContext (), SettingsActivity.class );
                Bundle bundle = new Bundle ();
                bundle.putInt ( TerraInfo.BUNDLE_NAME_TERRA_NO, terraNo );
                intent.putExtras ( bundle );
                startActivity ( intent);
                return true;
                
            case R.id.menu_clear_widgets:
                Log.i ( DEBUG_TAG, "clear widget" );
                WidgetConfigActivity.deleteAllTerraNoPref ( getApplicationContext() );
                Toast.makeText ( getApplicationContext(), R.string.menu_clear_widgets_text, Toast.LENGTH_SHORT );
                return true;
                
        }
        
        return super.onOptionsItemSelected ( item );
        
    }
    
    // ========================================================================================
    
    @SuppressLint("ValidFragment")
    private class SetTimeDialog extends DialogFragment implements OnTimeSetListener {
        
        private String key;
        private static final boolean IS_24_HOUR = true; 
        
        /**
         * TODO
         */
        public SetTimeDialog ( String modelKey ) {
            
            key = modelKey;
            
        }
        
        /* (non-Javadoc)
         * @see android.app.DialogFragment#onCreateDialog(android.os.Bundle)
         */
        @Override
        public Dialog onCreateDialog ( Bundle savedInstanceState ) {
            
            HourMinute hm = new HourMinute ( terraModels [terraNo].getValue ( key ) );
            
            final TimePickerDialog d = new TimePickerDialog ( TerraControlActivity.this, this, hm.getHour (), hm.getMinute (), IS_24_HOUR );
            d.setCancelable ( true );
            d.setCanceledOnTouchOutside ( false );

            int title_id;
            if ( "on".equals ( key ) ) {
                title_id = R.string.picker_title_on;
            }
            else {
                title_id = R.string.picker_title_off;
            }
            d.setTitle ( title_id );
            
            d.setIcon ( R.drawable.icon_alarms );
            
            return d;
            
        }
        
        /* (non-Javadoc)
         * @see android.app.TimePickerDialog.OnTimeSetListener#onTimeSet(android.widget.TimePicker, int, int)
         */
        public void onTimeSet ( TimePicker view, int hourOfDay, int minute ) {
            
            Log.i ( DEBUG_TAG, "onTimeSet fï¿½r " + key );
            
            final HourMinute hm = new HourMinute ( hourOfDay, minute );
            terraModels [terraNo].setValue ( key, hm.toString () );
            refreshViewByModel ( terraModels [terraNo] );

            new AsyncHttpSetTime ( terraNo, key + "=" + hm ).execute ();
            //Toast.makeText ( getApplicationContext(), "Zeit " + key + " aktualisiert", Toast.LENGTH_SHORT ).show ();
            
        }
        
        
        
    }
    
    // ========================================================================================
    
    private class AsyncHttpRequestValues extends TerraHttpAsyncTask {

        /**
         * @param terrNo
         * @param url
         */
        protected AsyncHttpRequestValues ( int terrNo ) {
            
            super ( terrNo, getString ( R.string.ext_url_values ), TerraControlActivity.this );

        }

        @Override
        protected void onPostExecute ( Void result ) {
            
            refreshViewByModel ( terraModels [terraNo] );
            
            // Build the intent to call the service and Update the widgets via the service
            Context context = getApplicationContext ();
            
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences ( context );
            boolean updateWidget = pref.getBoolean ( "pref_key_widget_update", false );
            if ( updateWidget ) {
                
                Log.i ( DEBUG_TAG, "widget will be updated terraNo=" + terraNo );
                
                Intent service = new Intent ( context, TerraControlWidgetService.class );
                service.putExtra ( TerraControlWidgetService.EXTRA_UPDATE_WIDGET_TERRA_NO, terraNo );
                service.putExtra ( TerraControlWidgetService.EXTRA_UPDATE_WIDGET_TEMP, terraModels [terraNo].getDisplayText ( "temp" ) );
                service.putExtra ( TerraControlWidgetService.EXTRA_UPDATE_WIDGET_HUM, terraModels [terraNo].getDisplayText ( "hum" ) );
                context.startService ( service );
            }
            
            super.onPostExecute ( result );
            
        }

        @Override
        protected void doInBackgroundHttpOk ( HttpResponse response ) throws IOException {
            
            String result = EntityUtils.toString ( response.getEntity() );
            Log.i ( DEBUG_TAG, "http request is ok" );
            
            ViewModel model = terraModels [terraNo];
            if ( result != null ) {
                Log.i ( DEBUG_TAG, "zerlege den Antwortstring result=" + result );
                
                // HACK, ersetze Trenner wg. regexp
                final char delimiter = '%';
                String [] parts = result.replace ( '|', delimiter ).split ( "" + delimiter );
                
                for ( int i = 0; i < parts.length; i++ ) {
                    String p = parts [i];
                    int pos = p.indexOf ( '=' );
                    if ( pos != -1 ) {
                        String key = p.substring ( 0, pos );
                        String value = p.substring ( pos + 1 );
                        value = value.replace ( '\r', ' ' ).trim ();
                        model.setValue ( key, value );
                    }
                }
                
            }
            
            // einige zusammengesetzte Werte erzeugen lassen
            model.generateComposedValues ();
            
        }
    }
        
    private class AsyncHttpRequestReboot extends TerraHttpAsyncTask {

        /**
         * @param terrNo
         * @param url
         */
        protected AsyncHttpRequestReboot ( int terrNo ) {
            
            super ( terrNo, getString ( R.string.ext_url_reboot ), TerraControlActivity.this );

        }
        
        @Override
        protected void doInBackgroundHttpOk ( HttpResponse response ) throws IOException {
            
            String result = EntityUtils.toString ( response.getEntity() );
            Log.i ( DEBUG_TAG, "http request is ok" );
            Log.i ( DEBUG_TAG, "result=" + result );
            
        }
            
    }    
    
    private class AsyncHttpSetTime extends TerraHttpAsyncTask {
        
        /**
         * @param terrNo
         * @param url
         */
        protected AsyncHttpSetTime ( int terrNo, String parms ) {
            
            super ( terrNo, getString ( R.string.ext_url_set ) + "?" + parms, TerraControlActivity.this );

        }

    }


}


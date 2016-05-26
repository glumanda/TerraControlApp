package de.jungierek.terracontrol2.widget;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.util.Log;
import android.widget.RemoteViews;
import de.jungierek.terracontrol2.R;
import de.jungierek.terracontrol2.TerraWifi;
import de.jungierek.terracontrol2.model.TerraInfo;

public class TerraControlWidgetService extends IntentService {

    private static final String DEBUG_TAG = "TerraControlWidgetServi";
    
    public static String EXTRA_UPDATE_WIDGET_ID = "de.jungierek.ics.terracontrol.TerraControlWidgetService.EXTRA_UPDATE_WIDGET_ID";
    public static String EXTRA_UPDATE_WIDGET_TERRA_NO = "de.jungierek.ics.terracontrol.TerraControlWidgetService.EXTRA_UPDATE_WIDGET_TERRA_NO";
    public static String EXTRA_UPDATE_WIDGET_TEMP = "de.jungierek.ics.terracontrol.TerraControlWidgetService.EXTRA_UPDATE_WIDGET_TEMP";
    public static String EXTRA_UPDATE_WIDGET_HUM = "de.jungierek.ics.terracontrol.TerraControlWidgetService.EXTRA_UPDATE_WIDGET_HUM";
    
    private boolean isHomeWifi = false;
    
    public TerraControlWidgetService () {
        super ( "TerraWidgetService" );
    }
    
    @Override
    protected void onHandleIntent ( Intent intent ) {
        
        // update all widgets
        
        Log.i ( DEBUG_TAG, "onHandleIntent: intent=" + intent );
        
        Context context = getApplicationContext ();

        isHomeWifi = TerraWifi.isHomeWifi ( context ); 
        if ( !isHomeWifi ) {
            Log.i ( DEBUG_TAG, "Nicht im Heimmatnetz" );
        }
        
        int widgetId = intent.getExtras ().getInt ( EXTRA_UPDATE_WIDGET_ID, -1 );
        
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance ( context );
        
        if ( widgetId > -1 ) {
            Log.i ( DEBUG_TAG, "update only one widget id=" + widgetId );
            updateWidget ( context, appWidgetManager, widgetId );
        }
        else {
            
            Log.i ( DEBUG_TAG, "update all widgets" );

            int terraNo = intent.getExtras ().getInt ( EXTRA_UPDATE_WIDGET_TERRA_NO, -1 );
            String temp = intent.getExtras ().getString ( EXTRA_UPDATE_WIDGET_TEMP );
            String hum = intent.getExtras ().getString ( EXTRA_UPDATE_WIDGET_HUM );
            
            // TODO Caching for TerraData

            ComponentName thisWidget = new ComponentName ( context, TerraControlWidget.class );
            int [] allWidgetIds = appWidgetManager.getAppWidgetIds ( thisWidget );
            
            for ( int id : allWidgetIds ) {
                if ( terraNo == -1 ) {
                    updateWidget ( context, appWidgetManager, id );
                }
                else {
                    int widgetTerraNo = WidgetConfigActivity.loadTerraNoPref ( context, id );
                    String terraName = TerraInfo.getInstance ( context ).getName ( terraNo );
                    if ( terraNo == widgetTerraNo ) {
                        Log.i ( DEBUG_TAG, "widget with id=" + id + " will be updated by temp=" + temp );
                        updateView ( context, appWidgetManager, id, terraName, null, temp, hum );
                    }
                }
            }
        }
  
    }
    
    private void updateView ( Context context, AppWidgetManager appWidgetManager, int widgetId, String terraName, String status, String temp, String hum ) {
        
        RemoteViews view = new RemoteViews ( context.getPackageName (), R.layout.widget_terra_control_xz );

        if ( status == null ) status = DateFormat.getDateTimeInstance ().format ( new Date () );

        // view widget id for debugging
        if ( false ) view.setTextViewText ( R.id.widget_status_headline, "" + widgetId );

        if ( terraName != null ) view.setTextViewText ( R.id.widget_terra, terraName );
        if ( temp != null ) view.setTextViewText ( R.id.widget_temp, temp );
        if ( hum != null ) view.setTextViewText ( R.id.widget_hum, hum );
        if ( status != null ) view.setTextViewText ( R.id.widget_status, status );

        appWidgetManager.updateAppWidget ( widgetId, view );

    }
    
    public void updateWidget ( Context context, AppWidgetManager appWidgetManager, int widgetId ) {
        
        Log.i ( DEBUG_TAG, "widgetId=" + widgetId );
        
        int terraNo = WidgetConfigActivity.loadTerraNoPref ( context, widgetId );
        Log.i ( DEBUG_TAG, "terraNo=" + terraNo );
        
        String terraName = "No Terra";
        String temp = null;
        String hum = null;
        String status = null;

        if ( terraNo > -1 ) {
            
            terraName = TerraInfo.getInstance ( context ).getName ( terraNo );
            
            if ( isHomeWifi ) {
            
                DataResult result = dataRequest ( context, terraNo );
                
                if ( result.isRequestOk ) {
                    temp = result.temp + "°C";
                    hum = result.hum + "%";
                }
                else {
                    status = "request failed";
                }

            }
            else {
                
                status = "no home wifi";
                
            }
        
        }
        
        updateView ( context, appWidgetManager, widgetId, terraName, status, temp, hum );
        
    }

    private static class DataResult {
        
        public boolean isRequestOk = false;
        public String temp;
        public String hum;
        
    }
    
    private DataResult dataRequest ( Context context, int terrNo ) {
        
        DataResult httpResult = new DataResult (); // http-request failed
        
        AndroidHttpClient client = null;

        try {

            Log.i ( DEBUG_TAG, "http begins for widget" );

            client = AndroidHttpClient.newInstance ( "Awesome User Agent V/1.0" );
            // TODO Konstanten auslagern
            HttpConnectionParams.setConnectionTimeout ( client.getParams (), 3000 );
            HttpConnectionParams.setSoTimeout ( client.getParams (), 5000 );

            String url = TerraInfo.getInstance ( context ).getUrl ( terrNo ) + context.getString ( R.string.ext_url_values );
            Log.i ( DEBUG_TAG, "url=" + url );
            
            HttpResponse response = client.execute ( new HttpGet ( url ) );
            Log.i ( DEBUG_TAG, "http request executed" );

            if (response.getStatusLine ().getStatusCode () == HttpStatus.SC_OK) {

                Log.i ( DEBUG_TAG, "http request is ok" );
                httpResult.isRequestOk = true;
                
                String result = EntityUtils.toString ( response.getEntity() );
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
                            if ( "temp".equals ( key ) ) {
                                httpResult.temp = value;
                            }
                            else if ( "hum".equals ( key )) {
                                httpResult.hum = value;
                            }
                        }
                    }
                    
                }
            }
            else {
                Log.i ( DEBUG_TAG, "http request is fehlerhaft, http-Code="
                        + response.getStatusLine ().getStatusCode () );
            }

        }
        catch (IOException exc) {

            Log.e ( DEBUG_TAG, "http request has error", exc );
            exc.printStackTrace ();

            // ConnectTimeOutException
        }
        finally {
            client.close ();
        }
        
        return httpResult;

    }
    
}

package de.jungierek.terracontrol2.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TerraControlWidget extends AppWidgetProvider {
    
    private static final String DEBUG_TAG = "TerraControlWidget";
    
    PendingIntent alarmOperation;
    
    
    
    @Override
    public void onReceive ( Context context, Intent intent ) {
        
        Log.i ( DEBUG_TAG, "intend=" + intent );
        super.onReceive ( context, intent );
        
    }
    
    public void onUpdate ( Context context, AppWidgetManager appWidgetManager, int [] appWidgetIds ) {
        
        Log.i ( DEBUG_TAG, "onUpdate: Start AlarmManager" );

        // Build the intent to call the service and Update the widgets via the service
        Intent service = new Intent ( context, TerraControlWidgetService.class );
        //context.startService ( serviceIntent );
        if ( alarmOperation == null ) {
            alarmOperation = PendingIntent.getService ( context, 0 /*requestCode*/, service, PendingIntent.FLAG_CANCEL_CURRENT );
        }
        
        AlarmManager alarm = (AlarmManager) context.getSystemService ( Context.ALARM_SERVICE );
        
        long interval = AlarmManager.INTERVAL_HOUR;
//        interval = 60000L; // jede Minute
        alarm.setInexactRepeating ( AlarmManager.RTC, 1000, interval, alarmOperation ); // jede Stunde

        Log.i ( DEBUG_TAG, "fertig" );
        
    }
    
    @Override
    public void onDeleted ( Context context, int [] widgetIds ) {

        super.onDeleted ( context, widgetIds );
        
        Log.i ( DEBUG_TAG, "len=" + widgetIds.length + " id[0]=" + widgetIds [0] ); 
        for ( int id : widgetIds ) {
            WidgetConfigActivity.deleteTerraNoPref ( context, id );
        }
        
    }
    
}
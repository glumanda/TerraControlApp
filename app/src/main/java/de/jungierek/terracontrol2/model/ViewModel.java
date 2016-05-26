/*
 * Created on 28.12.2011
 */
package de.jungierek.terracontrol2.model;

import java.io.Serializable;
import java.util.HashMap;

// siehe http://eureka.ykyuen.info/2010/01/03/android-simple-listview-using-simpleadapter/

/**
 * Das Model zur Anzeige der Werte eines Terrariums.
 * 
 * @author Andreas Jungierek
 * @version 2.0
 */
public class ViewModel implements Serializable {
    
    /**
     * Eine "Zeile" des Models.
     * 
     * @author Andreas Jungierek
     * @version 2.0
     */
    private class ListViewRow implements Serializable {
       
        /** Interner Schlüssel unter dem der Wert zu finden ist. */
        private String key;
        
        /** Der Wert zu einem Eintrag. */
        private String value;
        
        /** Der Anzeigename. */
        private String label;
        
        /** Eine optionale Maßeinheit. */
        private String unit;
        
        /** Soll Der Wert angezeigt werden. */
        private boolean visible;
        
        /**
         * Erzeuge einen neuen sichtbaren Dateneintrag ohne Maßeinheit.
         * 
         * @param label Anzeigename
         * @param key interner Schlüssel
         */
        public ListViewRow ( String label, String key ) {

            this ( label, key, "", true );
        
        }
        
        /**
         * Erzeuge einen neuen Dateneintrag ohne Maßeinheit.
         * 
         * @param label Anzeigename
         * @param key interner Schlüssel
         * @param visible Sichtbarkeit
         */
        public ListViewRow ( String label, String key, boolean visible ) {

            this ( label, key, "", visible );
        
        }
        
        /**
         * Erzeuge einen neuen sichtbaren Dateneintrag.
         * 
         * @param label Anzeigename
         * @param key interner Schl�ssel
         * @param unit Ma�einheit
         */
        public ListViewRow ( String label, String key, String unit ) {
            
            this ( label, key, unit, true );
            
        }
        
        /**
         * Erzeuge einen neuen Dateneintrag.
         * 
         * @param label Anzeigename
         * @param key interner Schl�ssel
         * @param unit Ma�einheit
         * @param visible Sichtbarkeit
         */
        public ListViewRow ( String label, String key, String unit, boolean visible ) {
            
            this.label = label;
            this.key = key;
            this.unit = unit;
            this.visible = visible;
            
        }
        
        /**
         * @return den Anzeigenamen, wenn keiner hinterlegt ist wird ein Leerstring geliefert.
         */
        public String getDisplayText () {
            
            if ( value == null ) return "";
            
            return value + unit;
            
        }
        
        /**
         * @return liefert den Wert, ggf. auch <code>null</code>
         */
        public String getValue () {
            
            return value;
            
        }

        /**
         * Setzen des Wertes.
         * 
         * @param der neue Wert.
         */
        public void setValue ( String value ) { 
        
            this.value = value; 
        
        }

        /**
         * @return der Schl�ssel.
         */
        public String getKey () { 
            
            return key; 
            
        }

        /**
         * @return der Anzeigename
         */
        public String getLabel () { 
            
            return label; 
            
        }
        
        /**
         * @return dei Sichtbarkeit
         */
        public boolean isVisible () { 
            
            return visible; 
            
        }

    }
    
    /** Die Datenzeilen in der Activity-View zum Suchen. */
    private HashMap<String, ListViewRow> map = new HashMap<String, ListViewRow> ();
    
    /** Die so gekennzeichneten Datenzeilen sollen nicht sichtbar sein. */
    private final static boolean ROW_NOT_VISIBLE = false;
    
    // TODO auslagern in config
    /** 
     * Alle Datenzeilen definieren. Die Werte sind alle <code>null</code>. Die Reihenfolge definiert
     * auch die Anzeigereihenfolge in der Activity-View. Nicht sichtbare Datenelemente sind
     * enstprechend gekennzeichnet. 
     * 
     */
    private ListViewRow [] listViewRows = { // Bezeichnung, key in http-request, Ma�einheit
        new ListViewRow ( "Terrarium", "terr" ),                            // per Definition ist Index 0 immer der Name
        new ListViewRow ( "Temperatur", "temp", "�C" ),         
        new ListViewRow ( "Luftfeuchtigkeit", "hum", "%" ),
        new ListViewRow ( "Licht an", "on", " Uhr", ROW_NOT_VISIBLE ),
        new ListViewRow ( "Licht aus", "off", " Uhr", ROW_NOT_VISIBLE ),
        new ListViewRow ( "Licht ...", "light" ),                           // Kombizeile
        new ListViewRow ( "gestartet", "since", " Uhr" ),
        new ListViewRow ( "aktuelles Datum", "date", ROW_NOT_VISIBLE ),
        new ListViewRow ( "aktuelle Zeit", "time", " Uhr", ROW_NOT_VISIBLE ),
        new ListViewRow ( "aktuell", "now" ),                               // Kombizeile
        new ListViewRow ( "Version", "version" ),
        new ListViewRow ( "Lichtsensor", "sensor" ),
    };
    
    /**
     * Ein neues Model f�r die View mit dem angegeben Namen.
     * 
     * @param terrName Name des Terrariums
     */
    public ViewModel () {

        for ( int i = 0; i < listViewRows.length; i++ ) {
            ListViewRow listRow = listViewRows [i];
            map.put ( listRow.getKey (), listRow );
        }
        
    }
    
    /**
     * Ermittle den "anzeigbaren" Wert zum angegeben Schl�ssel.
     * 
     * @param key der angefragte Schl�ssel
     * @return der Anzeigewert inkl. Ma�einheiten
     */
    public String getDisplayText ( String key ) {

        return map.get ( key ).getDisplayText ();

    }
    
    /**
     * Ermittle das Label f�rd ei Anzeige zum angegebenen Schl�ssel.
     * 
     * @param key der angefragte Schl�ssel
     * @return der Anzeigename
     */
    public String getLabel ( String key ) {
        
        return map.get ( key ).getLabel ();
        
    }
    
    /**
     * Ermittle den Wert zum angegeben Schl�ssel.
     * 
     * @param key der angefragte Schl�ssel
     * @return der Wert
     */
    public String getValue ( String key ) {
        
        return map.get ( key ).getValue ();
        
    }
    
    /**
     * Setze den Wert f�r den angegeben Schl�ssel.
     * 
     * @param key der Schl�ssel
     * @param value der neue Wert
     */
    public void setValue ( String key, String value ) {
    
        ListViewRow lr = map.get ( key );
        if ( lr != null ) {
            lr.setValue ( value );  
        }
        
    }

    /**
     * Ermittle die zusammengesetzen Anzeigewerte. Es werden zwei kombinierte Werte auf Basis vorhandener
     * Werte ermittelt.
     * <light> = "<on> bis <off>"
     * <now> = "<date> <time>"  
     */
    public void generateComposedValues () {
        
        String onValue = map.get ( "on" ).getDisplayText ();
        String offValue = map.get ( "off" ).getDisplayText ();
        if ( onValue != null && offValue != null ) {
            setValue ( "light", onValue + " bis " + offValue );
        }
        else {
            setValue ( "light", "Werte nicht verf�gbar" );
        }
        
        String dateValue = map.get ( "date" ).getDisplayText ();
        String timeValue = map.get ( "time" ).getDisplayText ();
        if ( dateValue != null && timeValue != null ) {
            setValue ( "now", dateValue + " "+ timeValue );
        }
        else {
            setValue ( "now", "Werte nicht verf�gbar" );
        }
        
    }

}

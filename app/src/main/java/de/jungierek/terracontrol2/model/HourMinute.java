/*
 * Created on 10.03.2012
 */
package de.jungierek.terracontrol2.model;

import android.util.Log;

/**
 * Für ein einfaches Handling von Uhrzeiten im Format "hh:mm".
 * 
 * @author Andreas Jungierek
 * @version 1.0
 *
 */
public class HourMinute implements Comparable<HourMinute> {

	private final static String TAG = "HourMinute";
   
    /** Stunden, Minuten */
    private int hh, mm;
    
    /**
     * Die Uhrzeit "00:00".
     */
    public HourMinute () {
        
        hh = mm = 0;
        
    }
    
    /** 
     * Eine konkrete Uhrzeit. Es erfolgt eine Pr�fung auf zul�ssige Werte
     * 
     * @param hour die Stunden
     * @param minute die Minuten
     * @throws IllegalArgumentException wenn die Bedingungen f�r Uhrzeiten nicht zutreffen
     */
    public HourMinute ( int hour, int minute ) {
        
        setHour ( hour );
        setMinute ( minute );
        
    }
    
    /**
     * Baut eine Uhrzeit aus einem String mit dem Format "hh:mm".
     * 
     * @param s die Uhrzeit
     * @throws IllegalArgumentException wenn die Bedingungen f�r Uhrzeiten nicht zutreffen
     */
    public HourMinute ( String s ) {
        
        parse ( s );
        
    }

    /**
     * copy-Konstruktor.
     * 
     * @param hm das "Original"
     * @throws IllegalArgumentException wenn die Bedingungen f�r Uhrzeiten nicht zutreffen
     */
    public HourMinute ( HourMinute hm ) {
        
        set ( hm );
        
    }
    
    /**
     * Setzen der Uhrzeit.
     * 
     * @param hm die Ausgangsuhrzeit
     */
    public void set ( HourMinute hm ) {
        
        hh = hm.getHour ();
        mm = hm.getMinute ();
        
    }
    
    /** 
     * @return die Stunde 
     */
    public int getHour () {
        
        return hh;
        
    }
    
    /**
     * Setze die Stunde.
     * 
     * @param hour die Stunde.
     * @throws IllegalArgumentException wenn die Bedingungen f�r Uhrzeiten nicht zutreffen
     */
    public void setHour ( int hour ) {
        
        if ( hour < 0 || hour > 23 ) throw new IllegalArgumentException ( "hour=" + hour );
        this.hh = hour;
        
    }

    /**
     * @return die Minute
     */
    public int getMinute () {
        
        return mm;
        
    }
    
    /**
     * Setze die Minute.
     * 
     * @param minute die Minute
     * @throws IllegalArgumentException wenn die Bedingungen f�r Uhrzeiten nicht zutreffen
     */
    public void setMinute ( int minute ) {

        if ( minute < 0 || minute > 59 ) throw new IllegalArgumentException ( "minute=" + minute );
        this.mm = minute;
        
    }
    
    /**
     * �bernehme die Uhrzeit aus einem String. Das Format ist zwingend "hh:mm".
     * 
     * @param s die neue Uhrzeit
     * @throws IllegalArgumentException wenn die Bedingungen f�r Uhrzeiten nicht zutreffen
     */
    public void parse ( String s ) {
        
        if ( s.length () == 5 &&
             Character.isDigit ( s.charAt ( 0 ) ) &&
             Character.isDigit ( s.charAt ( 1 ) ) &&
             s.charAt ( 2 ) == ':' &&
             Character.isDigit ( s.charAt ( 3 ) ) &&
             Character.isDigit ( s.charAt ( 4 ) )
        ) {
                setHour ( Integer.parseInt ( s.substring (0, 2 ) ) );
                setMinute ( Integer.parseInt ( s.substring (3, 5 ) ) );
        }
        else {
            throw new IllegalArgumentException ( "s=" + s );
        }
        
    }
    
    /**
     * Repr�sentiere die Uhrzeit als String im Format "hh:mm".
     * @see java.lang.Object#toString()
     * 
     * @return die Uhrzeit als String
     */
    @Override
    public String toString () {
        
        String result = "";
        
        if ( hh < 10 ) {
            result += "0";
        }
        result += hh;
        
        result += ":";

        if ( mm < 10 ) {
            result += "0";
        }
        result += mm;
        
        return result;
        
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    /**
     * Vergleiche diese Uhrzeit mit einer weiteren. Der Wert von <code>another</code> muss ungleich <code>null</code> sein.
     * 
     * @throws NullPointerException wenn <code>another</code> gleich <code>null</code> ist
     * @return 0: beide Uhrzeiten sind gleich, -1: <code>another</code> liegt zeitlich vor der aktuellen Zeit,
     * +1 <code>another</code> liegt zeitlich nach der aktuellen Zeit
     */
    public int compareTo ( HourMinute another ) {
        
        // NullPointerException provozieren
        another.toString ();
        
        int result = hh - another.getHour ();
        if ( result != 0 ) result /= Math.abs ( result );
        Log.i ( TAG, "after compare hh result=" + result );
        
        if ( result == 0 ) {
            result = mm - another.getMinute ();
            if ( result != 0 ) result /= Math.abs ( result );
            Log.i ( TAG, "after compare mm result=" + result );
        }
        
        return result;
        
    }
    
    /**
     * Vergleiche mit einem weiteren Objekt.
     * @see java.lang.Object#equals(java.lang.Object)
     * 
     * @return <code>true</code>, wenn es sich um eine Uhrzeit handelt und {@link #compareTo(HourMinute)} 0 ergibt.
     */
    @Override
    public boolean equals ( Object o ) {
        
        if ( !(o instanceof HourMinute) ) return false;
    
        return compareTo ( (HourMinute ) o) == 0;

    }
    
}
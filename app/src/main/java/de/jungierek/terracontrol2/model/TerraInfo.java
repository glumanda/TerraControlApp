/*
 * Created on 31.03.2012
 */
package de.jungierek.terracontrol2.model;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import de.jungierek.terracontrol2.R;

/**
 * Einige Infos zu den Terrarien als Singleton.
 * 
 * @author Andreas Jungierek
 * @version 1.0
 */
public class TerraInfo {
    
    /** Bundle-Name für die Terrarium-Nummer. */
    public static String BUNDLE_NAME_TERRA_NO = "terrarium";
    
    private static TerraInfo instance;
    
    /** Die Namen der Terrarien. */
    private final String terraNames [];
    
    /** Und die IP-Adressen. */
    private final String terraIp [];
    
    private final String baseUrl;
    
    /**
     * Initialisert die Basisdaten.
     * 
     * @param context der {@link Activity}-{@link Context}
     * @throws IllegalArgumentException
     */
    private TerraInfo ( Context context ) {

        final Resources resources = context.getResources ();
        
        terraNames = resources.getStringArray ( R.array.terra_name );
        terraIp = resources.getStringArray ( R.array.terra_ip );
        baseUrl = resources.getString ( R.string.base_url );
        
        if ( terraNames.length != terraIp.length ) {
            throw new IllegalArgumentException ( "Namen passen nicht zu IP (name.len=" + terraNames.length + " ip.len=" + terraIp.length );
        }
     
    }
    
    /**
     * Der einzige Zugriff auf die Werte führt über diese Methode (Singelton).
     * 
     * @param context TODO
     * @return das Infoobjekt
     */
    public static TerraInfo getInstance ( Context context ) {
        
        if ( instance == null ) {
            instance = new TerraInfo ( context ); 
        }
        
        return instance;
        
    }
    
    /**
     * Anzahl der Tarrarien.
     * 
     * @return die Anzahl
     */
    public int getCount () {
     
        return terraNames.length;
        
    }
    
    /**
     * Ermittelt die URL des Terrariums mit dem genannten Index. Wenn der Index außerhalb des Bereiches
     * liegt, wird eine {@link IllegalArgumentException} geworfen.
     * 
     * @param index des Terrariums
     * @return die URL
     * @throws IllegalArgumentException
     */
    public String getUrl ( int index ) {

        if ( index < 0 || index >= terraNames.length ) throw new IllegalArgumentException ( "falcher TerraInfo-Index" );
        
        return baseUrl + terraIp [index];
        
    }
    
    /**
     * Ermittelt den Terrariennamen mit dem genannten Index. Wenn der Index au�erhalb des Bereiches 
     * liegt, wird eine {@link IllegalArgumentException} geworfen.
     * 
     * @param index des Terrariums
     * @return der Name
     * @throws IllegalArgumentException
     */
    public String getName ( int index ) {
        
        if ( index < 0 || index >= terraNames.length ) throw new IllegalArgumentException ( "falcher TerraInfo-Index" );
        
        return terraNames [index];
        
    }
    
    /**
     * Liefert alle Terrarien-Namen.
     * 
     * @return die Namen
     */
    public String [] getAllNames () {
        
        return terraNames;
        
    }

}

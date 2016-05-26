package de.jungierek.terracontrol2;

import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class TerraWifi {

    private final static String DEBUG_TAG = "TerraWlan";

    /**
     * Prüfung, ob die App im Emulator läuft.
     * 
     * @return <code>true</code>, wenn Runtime-Umgebung der Emulator ist.
     */
    private static boolean isEmulator ( Context context ) {

        return "Android".equals ( ((TelephonyManager) context.getSystemService ( Context.TELEPHONY_SERVICE ))
                .getNetworkOperatorName () );

    }

    /**
     * Prüfung, ob ein Wifi-Netzwerk verfügbar ist.
     * 
     * @param context
     *            TODO
     * @return <code>true</code>, wenn ein Netzwerk da ist
     */
    private static boolean isConnected ( Context context ) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService ( Context.CONNECTIVITY_SERVICE );

        if ( connectivityManager == null ) {
            return false;
        }
        else {
            return connectivityManager.getNetworkInfo ( ConnectivityManager.TYPE_WIFI ).isConnected ();

        }

    }

    /**
     * Ermittle die aktuelle SSID des WLAN. NUr rufen, wenn Verbindung
     * besteht!!!
     * 
     * @param context
     * @return Name der SSID, <code>null</code> falls keine verfügbar
     */
    private static String getCurrentSsid ( Context context ) {

        String ssid = null;

        final WifiManager wifiManager = (WifiManager) context.getSystemService ( Context.WIFI_SERVICE );
        final WifiInfo wifiInfo = wifiManager.getConnectionInfo ();

        if ( wifiInfo != null ) {
            String s = wifiInfo.getSSID ();
            if ( s != null && !"".equals ( s ) ) {
                if ( s.charAt ( 0 ) == '"' ) {
                    Log.i ( DEBUG_TAG, "ssid (" + s + ") with \"" );
                    ssid = s.substring ( 1, s.length () - 1 );
                }
                else {
                    ssid = s;
                }
            }
        }

        return ssid;

    }

    public static boolean isHomeWifi ( Context context ) {

        boolean isHomeWifi = TerraWifi.isEmulator ( context ); // wir sind immer
                                                               // im richtigen
                                                               // Netz im
                                                               // Emulator :-)

        // check Wifi
        if ( TerraWifi.isConnected ( context ) ) { // nur bei echten Androiden

            Log.i ( DEBUG_TAG, "WLAN da" );

            // TerraWlan.listWifis ( context );

            String ssid = TerraWifi.getCurrentSsid ( context );
            Log.i ( DEBUG_TAG, "ssid=" + ssid );

            isHomeWifi = context.getString ( R.string.splash_ssid ).equals ( ssid );

        }

        return isHomeWifi;

    }

    public static void listWifis ( Context context ) {

        final WifiManager wifiManager = (WifiManager) context.getSystemService ( Context.WIFI_SERVICE );
        final WifiInfo wifiInfo = wifiManager.getConnectionInfo ();

        // Get WiFi status MARAKANA
        String textStatus = "\n\nWiFi Status: " + wifiInfo;
        String BSSID = wifiInfo.getBSSID ();
        String MAC = wifiInfo.getMacAddress ();

        List<ScanResult> results = wifiManager.getScanResults ();
        ScanResult bestSignal = null;
        int count = 1;
        String etWifiList = "";
        for ( ScanResult result : results ) {
            etWifiList += count++ + ". " + result.SSID + " : " + result.level + "\n" + result.BSSID + "\n"
                    + result.capabilities + "\n" + "\n=======================\n";
        }
        Log.v ( DEBUG_TAG, "from SO: \n" + etWifiList );

        // List stored networks
        List<WifiConfiguration> configs = wifiManager.getConfiguredNetworks ();
        for ( WifiConfiguration config : configs ) {
            textStatus += "\n\n" + config.toString ();
        }
        Log.v ( DEBUG_TAG, "from marakana: \n" + textStatus );

    }

}

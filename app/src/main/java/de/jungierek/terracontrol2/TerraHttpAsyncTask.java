/*
 * Created on 30.03.2012
 */
package de.jungierek.terracontrol2;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpConnectionParams;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import de.jungierek.terracontrol2.model.TerraInfo;

/**
 * Abstrakte Hilfsklasse zur Bündelung der URl-Ermittlung und Vereinheitlichung
 * des GUI.
 * 
 * @author Andreas Jungierek
 * @version 1.0
 */
// TODO umbauen auf Android-Empfehlung
public abstract class TerraHttpAsyncTask extends AsyncTask<String, Void, Void> {

    private static final String DEBUG_TAG = "TerraHttpAsyncTask";

    private String url;

    private Activity activity;

    protected boolean httpError = false;
    protected StatusLine httpStatusLine;

    protected boolean excError = false;
    protected IOException ioException;

    /**
     * Verbotener Konstruktor.
     */
    @SuppressWarnings("unused")
    private TerraHttpAsyncTask () {
        // forbidden
    }

    /**
     * Baue eine neue asynchrone Task. W�hrend der Arbeit wird ein Dialog
     * "in Arbeit" angezeigt. Von dem angefragten Arduino zur�ck gegebene Daten
     * k�nnen mittels {@link #doInBackgroundHttpOk(HttpResponse)} verarbeitet
     * werden.
     * 
     * @param terrNo
     *            Nummer des Terrariums
     * @param extUrl
     *            spezielle Erweiterung der URL inkl. Parameter (GET)
     * @param activity
     *            aus der die task gerufen wird
     */
    public TerraHttpAsyncTask ( int terrNo, String extUrl, Activity activity ) {

        Log.i ( DEBUG_TAG, "terrNo=" + terrNo + " extUrl=" + extUrl + " activity=" + activity );

        this.activity = activity;
        url = TerraInfo.getInstance ( activity ).getUrl ( terrNo ) + extUrl;

        Log.i ( DEBUG_TAG, "url=" + url );

    }

    /** Der Anzeigedialog in der {@link Activity}. */
    private ProgressDialog dialog;

    /**
     * Wird vor der Hintergrundoperation, also dem http-Request, aufgerufen. Es
     * wird in der Activity ein "Ich bin besch�ftigt"-Dialog gezeigt.
     * 
     * @see android.os.AsyncTask#onPreExecute()
     */
    @Override
    protected void onPreExecute () {
        super.onPreExecute (); // ???
        Log.i ( DEBUG_TAG, "Dialog wird gerufen" );
        dialog = ProgressDialog.show ( activity, null, activity.getString ( R.string.progess_network ), true, false );
    }

    /**
     * Wird nach der Hintergrundoperation, also dem http-Request, aufgerufen. Es
     * wird in der Activity der "Ich bin besch�ftigt"-Dialog geschlossen.
     * 
     * @param result
     *            TODO
     */
    @Override
    protected void onPostExecute ( Void result ) {

        Log.i ( DEBUG_TAG, "Dialog wird geschlossen" );

        dialog.dismiss ();

        Log.i ( DEBUG_TAG, "httpError=" + httpError + " statusLine=" + httpStatusLine + " excError=" + excError
                + " ioExc=" + ioException );

        String msg = null;

        if (httpError) {
            msg = "http-Fehlercode: " + httpStatusLine.getStatusCode () + "\n" + "http-ReasonPhrase: "
                    + httpStatusLine.getReasonPhrase ();
        }

        if (excError) {
            msg = ioException.getMessage ();
        }

        if (msg != null) {

            new AlertDialog.Builder ( activity ).setTitle ( activity.getString ( R.string.alert_network ) )
                    .setMessage ( msg ).setCancelable ( false )
                    .setPositiveButton ( activity.getString ( R.string.alert_ok ), null ).create ().show ();

        }

        super.onPostExecute ( result );

    }

    /**
     * F�hre die Hintergrundoperation aus. In Folge dessen wird
     * {@link #doInBackground(String...)} ausgef�hrt.
     */
    public void execute () {

        super.execute ( url );

    }

    /**
     * Die Hintergrundoperation ist der http-Zugriff.
     */
    @Override
    protected Void doInBackground ( String... urls ) {

        AndroidHttpClient client = null;

        try {

            Log.i ( DEBUG_TAG, "http begins" );

            client = AndroidHttpClient.newInstance ( "Awesome User Agent V/1.0" );
            // TODO Konstanten auslagern
            HttpConnectionParams.setConnectionTimeout ( client.getParams (), 3000 );
            HttpConnectionParams.setSoTimeout ( client.getParams (), 5000 );

            Log.i ( DEBUG_TAG, "url=" + urls[0] );

            HttpResponse response = client.execute ( new HttpGet ( urls[0] ) );
            Log.i ( DEBUG_TAG, "http request executed" );

            if (response.getStatusLine ().getStatusCode () == HttpStatus.SC_OK) {
                Log.i ( DEBUG_TAG, "http request is ok" );
                doInBackgroundHttpOk ( response );
            }
            else {
                Log.i ( DEBUG_TAG, "http request is fehlerhaft, http-Code="
                        + response.getStatusLine ().getStatusCode () );
                httpError = true;
                httpStatusLine = response.getStatusLine ();
            }

        }
        catch (IOException exc) {

            Log.e ( DEBUG_TAG, "http request has error", exc );
            exc.printStackTrace ();

            excError = true;
            ioException = exc;

            // ConnectTimeOutException
        }
        finally {
            client.close ();
        }

        return null;

    }

    /**
     * Hier kann durch Einklinken das Resultat des http-Zugriffs abgegriffen
     * werden.
     */
    protected void doInBackgroundHttpOk ( HttpResponse response ) throws IOException {

        // do nothing
        // empty adapter method

    }

}

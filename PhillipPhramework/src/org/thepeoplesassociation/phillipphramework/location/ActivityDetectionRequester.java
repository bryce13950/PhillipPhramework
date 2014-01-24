package org.thepeoplesassociation.phillipphramework.location;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.ActivityRecognitionClient;

/**
 * Created by Nacho L. on 12/06/13.
 */
public class ActivityDetectionRequester implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

    private Context context;
    private PendingIntent pendingIntent;
    private ActivityRecognitionClient activityRecognitionClient;
    
    private SmartLocationOptions smartLocationOptions;

    public ActivityDetectionRequester(Context context) {
        this.context = context;

        if (smartLocationOptions == null) {
            smartLocationOptions = new SmartLocationOptions();
        }
        activityRecognitionClient = null;
        pendingIntent = null;
    }

    public PendingIntent getRequestPendingIntent() {
        return pendingIntent;
    }

    public void setRequestPendingIntent(PendingIntent pendingIntent) {
        this.pendingIntent = pendingIntent;
    }

    public void requestUpdates() {
        requestConnection();
    }

    private void requestConnection() {
        getActivityRecognitionClient().connect();
    }

    private void continueRequestActivityUpdates() {
        getActivityRecognitionClient().requestActivityUpdates(
                ActivityRecognitionConstants.ACTIVITY_DETECTION_INTERVAL,
                createRequestPendingIntent());
    }

    private PendingIntent createRequestPendingIntent() {
        if (getRequestPendingIntent() != null) {
            return pendingIntent;
        } else {
            Intent intent = new Intent(context, ActivityRecognitionService.class);
            PendingIntent pending = PendingIntent.getService(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            setRequestPendingIntent(pending);
            return pending;
        }
    }

    private ActivityRecognitionClient getActivityRecognitionClient() {
        if (activityRecognitionClient == null) {
            activityRecognitionClient = new ActivityRecognitionClient(context, this, this);
        }
        return activityRecognitionClient;
    }

    @Override
    public void onConnected(Bundle bundle) {
    	if(smartLocationOptions.isDebugging()) {
    		Log.i(getClass().getSimpleName(), "connected");
    	}
        continueRequestActivityUpdates();
    }

    @Override
    public void onDisconnected() {
    	if(smartLocationOptions.isDebugging()) {
    		Log.i(getClass().getSimpleName(), "disconnected");
    	}
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    	if(smartLocationOptions.isDebugging()) {
    		Log.i(getClass().getSimpleName(), "connection failed");
    	}
    }
}
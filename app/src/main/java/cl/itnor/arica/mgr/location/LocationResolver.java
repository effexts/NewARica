package cl.itnor.arica.mgr.location;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import cl.itnor.arica.MixContext;

/**
 * Created by effexts on 1/20/17.
 */

class LocationResolver implements LocationListener {
    private String provider;
    private LocationMgrImpl locationMgr;
    private LocationManager locationManager;

    public LocationResolver(LocationManager locationManager, String provider, LocationMgrImpl locationMgr ) {
        this.provider = provider;
        this.locationMgr = locationMgr;
        this.locationManager = locationManager;
    }

    public LocationResolver(LocationMgrImpl locationMgr) {
        super();
        this.locationMgr = locationMgr;
    }

    @Override
    public void onLocationChanged(Location location) {
        locationManager.removeUpdates(this);
        locationMgr.locationCallback(provider);
        Log.d(MixContext.TAG, "Normal Location Changed: "+ location.getProvider()
                + " lat: " + location.getLatitude()
                + " lng: " + location.getLongitude()
                + " altitude: "+ location.getAltitude()
                + " accuracy: "+ location.getAccuracy());
        try {
            //addWalkingPathPosition(location);
            Log.d(MixContext.TAG, "Location Changed: "+ location.getProvider()
                    + " lat: " + location.getLatitude()
                    + " lng: " + location.getLongitude()
                    + " altitude: "+ location.getAltitude()
                    + " accuracy: "+ location.getAccuracy());
            locationMgr.setCurrentLocation(location);
        }
        catch (Exception ex) { ex.printStackTrace(); }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}

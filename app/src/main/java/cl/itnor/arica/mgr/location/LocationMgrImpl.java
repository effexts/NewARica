package cl.itnor.arica.mgr.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.GeomagneticField;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cl.itnor.arica.MixContext;
import cl.itnor.arica.R;

/**
 * Created by effexts on 1/20/17.
 */

class LocationMgrImpl implements LocationFinder {
    private LocationManager locationManager;
    private String bestLocationProvider;
    private final MixContext mixContext;
    private Location currentLocation;
    private Location lastLocation;
    private LocationFinderState state;
    private final LocationResolver locationResolver;
    private List<LocationResolver> locationResolvers;
    private final long freq = 5000;
    private final float distance = 20;

    public LocationMgrImpl(MixContext mixContext) {
        this.mixContext = mixContext;
        this.state = LocationFinderState.Inactive;
        this.locationResolver = new LocationResolver(this);
        this.locationResolvers = new ArrayList<LocationResolver>();
    }

    @Override
    public void findLocation() {
        try {
            requestBestLocationUpdates();
            //temporary set the current location, until a good provider is found
            if (ActivityCompat.checkSelfPermission(this.mixContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.mixContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            currentLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), true));
            if (currentLocation == null) setHardFix();
        } catch (Exception ex2) {
            setHardFix();
        }
    }

    private void setHardFix() {
        Location hardFix = new Location("reverseGeocoded");

        hardFix.setLatitude(-18.489099);
        hardFix.setLongitude(-70.295190);
        hardFix.setAltitude(100);
        currentLocation = hardFix;
        mixContext.doPopUp(R.string.connection_GPS_dialog_text);
    }

    private void requestBestLocationUpdates() {
        Timer timer = new Timer();
        for (String p : locationManager.getAllProviders()) {
            LocationResolver locationResolver = new LocationResolver(locationManager, p, this);
            locationResolvers.add(locationResolver);
            if (ActivityCompat.checkSelfPermission(this.mixContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.mixContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(p, 0, 0, locationResolver);
        }
        timer.schedule(new LocationTimerTask(), 20 * 1000);
    }

     @Override
    public void locationCallback(String provider) {
        if (ActivityCompat.checkSelfPermission(this.mixContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.mixContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location foundLocation = locationManager.getLastKnownLocation(provider);
        if (bestLocationProvider != null) {
            Location bestLocation = locationManager.getLastKnownLocation(bestLocationProvider);
            if (foundLocation.getAccuracy() < bestLocation.getAccuracy()) {
                currentLocation = foundLocation;
                bestLocationProvider = provider;
            }
        } else {
            currentLocation = foundLocation;
            bestLocationProvider = provider;
        }
        setLastLocation(currentLocation);
    }

    @Override
    public Location getCurrentLocation() {
        if (currentLocation == null) {
            Toast.makeText(mixContext, mixContext.getResources().getString(R.string.location_not_found), Toast.LENGTH_LONG).show();
            throw new RuntimeException("No GPS Found");
        }
        synchronized (currentLocation) {
            return currentLocation;
        }
    }

    @Override
    public void switchOn() {
        if (!LocationFinderState.Active.equals(state)) {
            locationManager = (LocationManager) mixContext.getSystemService(Context.LOCATION_SERVICE);
            state = LocationFinderState.Confused;
        }
    }

    @Override
    public void switchOff() {
        if (locationManager != null) {
            locationManager.removeUpdates(getLocationResolver());
            state = LocationFinderState.Inactive;
        }
    }

    @Override
    public LocationFinderState getStatus() {
        return state;
    }

    @Override
    public GeomagneticField getGeomagneticField() {
        Location location = getCurrentLocation();
        return new GeomagneticField(
                (float) location.getLatitude(),
                (float) location.getLongitude(),
                (float) location.getAltitude(),
                System.currentTimeMillis());

    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public void setLocationManager(LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    public String getBestLocationProvider() {
        return bestLocationProvider;
    }

    public void setBestLocationProvider(String bestLocationProvider) {
        this.bestLocationProvider = bestLocationProvider;
    }

    public MixContext getMixContext() {
        return mixContext;
    }

    public void setCurrentLocation(Location location) {
        synchronized (currentLocation) {
            currentLocation = location;
        }
        mixContext.getActualMixView().refresh();
        Location lastLocation = getLastLocation();
        if (lastLocation == null) setLastLocation(location);
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
    }

    public LocationFinderState getState() {
        return state;
    }

    public void setState(LocationFinderState state) {
        this.state = state;
    }

    public synchronized LocationResolver getLocationResolver() {
        return locationResolver;
    }

    public List<LocationResolver> getLocationResolvers() {
        return locationResolvers;
    }

    public void setLocationResolvers(List<LocationResolver> locationResolvers) {
        this.locationResolvers = locationResolvers;
    }

    public long getFreq() {
        return freq;
    }

    public float getDistance() {
        return distance;
    }

    private class LocationTimerTask extends TimerTask {
        @Override
        public void run() {
            for (LocationResolver locationResolver : locationResolvers)
                locationManager.removeUpdates(locationResolver);
            if (bestLocationProvider != null) {
                locationManager.removeUpdates(getLocationResolver());
                state = LocationFinderState.Confused;
                mixContext.getActualMixView().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (ActivityCompat.checkSelfPermission(mixContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mixContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        locationManager.requestLocationUpdates(bestLocationProvider, freq, distance, getLocationResolver());
                    }
                });
                state = LocationFinderState.Active;
            }
            else {
                mixContext.getActualMixView().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mixContext.getActualMixView(), mixContext.getActualMixView().getResources().getString(R.string.location_not_found), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }
}

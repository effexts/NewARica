package cl.itnor.arica.mgr.location;

import android.hardware.GeomagneticField;
import android.location.Location;

/**
 * Created by effexts on 1/20/17.
 */

public interface LocationFinder {



    public enum LocationFinderState {
        Active,
        Inactive,
        Confused
    }

    void findLocation();
    void locationCallback(String provider);
    Location getCurrentLocation();
    void switchOn();
    void switchOff();
    LocationFinderState getStatus();
    GeomagneticField getGeomagneticField();
    void setLastLocation(Location lastLocation);
}

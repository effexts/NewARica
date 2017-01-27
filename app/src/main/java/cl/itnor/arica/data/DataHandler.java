package cl.itnor.arica.data;

import android.location.Location;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import cl.itnor.arica.MixContext;
import cl.itnor.arica.lib.marker.Marker;

/**
 * Created by effexts on 1/26/17.
 */

public class DataHandler {
    private List<Marker> markerList = new ArrayList<Marker>();
    public void addMarkers(List<Marker> markers) {
        for (Marker ma:markers) {
            if (!markerList.contains(ma))
                markerList.add(ma);
        }
    }
    public void sortMarkerList() { Collections.sort(markerList); }
    public void updateDistances(Location location) {
        for (Marker ma:markerList) {
            float[] dist = new float[3];
            Location.distanceBetween(ma.getLatitude(), ma.getLongitude(), location.getLatitude(), location.getLongitude(), dist);
            ma.setDistance(dist[0]);
        }
    }
    public void updateActivationStatus(MixContext mixContext) {
        Hashtable<Class, Integer> map = new Hashtable<Class, Integer>();
        for (Marker ma:markerList) {
            Class<? extends Marker> aClass = ma.getClass();
            map.put(aClass, (map.get(aClass)!=null)?map.get(aClass)+1:1);
            boolean belowMax = (map.get(aClass) <= ma.getMaxObjects());
            ma.setActive(belowMax);
        }
    }
    public void onLocationChanged(Location location){
        updateDistances(location);
        sortMarkerList();
        for (Marker ma:markerList)
            ma.update(location);
    }
    @Deprecated
    public List<Marker> getMarkerList() {
        return markerList;
    }
    @Deprecated
    public void setMarkerList(List<Marker> markerList) {
        this.markerList = markerList;
    }

    public int getMarkerCount() { return markerList.size(); }
    public Marker getMarker(int index) { return markerList.get(index); }
}

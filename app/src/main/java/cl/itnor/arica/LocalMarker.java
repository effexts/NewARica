package cl.itnor.arica;

import cl.itnor.arica.lib.marker.Marker;
import cl.itnor.arica.lib.reality.PhysicalPlace;
import cl.itnor.arica.lib.render.MixVector;

/**
 * Created by effexts on 1/27/17.
 */

public abstract class LocalMarker implements Marker {
    private String ID;
    protected String title;
    protected boolean underline;
    private String URL;
    protected PhysicalPlace mGeoLoc;
    protected double distance; //Distance from user to mGeoLoc in meters
    private boolean active;

    protected boolean isVisible;
    public MixVector cMarker = new MixVector();
    protected MixVector singMarker = new MixVector();
    protected MixVector locationVector = new MixVector();

}

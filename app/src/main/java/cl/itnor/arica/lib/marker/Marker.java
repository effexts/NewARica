package cl.itnor.arica.lib.marker;

import android.location.Location;

import cl.itnor.arica.MixState;
import cl.itnor.arica.lib.MixContextInterface;
import cl.itnor.arica.lib.MixStateInterface;
import cl.itnor.arica.lib.gui.PaintScreen;
import cl.itnor.arica.lib.render.Camera;
import cl.itnor.arica.lib.render.MixVector;

/**
 * Created by effexts on 1/26/17.
 */

public interface Marker extends Comparable<Marker>{
    String getTitle();
    String getURL();
    String getID();
    void setID();
    boolean isActive();
    void setActive(boolean active);
    double getLatitude();
    double getLongitude();
    double getAltitude();
    double getDistance();
    void setDistance(double distance);
    MixVector getLocationVector();
    void update(Location currentGPSFix);
    void calcPaint(Camera viewCam, float addX, float addY);
    void draw(PaintScreen dw);
    public boolean fClick(float x, float y, MixContextInterface context, MixStateInterface state);
    int getMaxObjects();
}

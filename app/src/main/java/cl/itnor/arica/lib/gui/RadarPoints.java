package cl.itnor.arica.lib.gui;

/**
 * Created by effexts on 1/26/17.
 */
import android.graphics.Color;

import cl.itnor.arica.DataView;
import cl.itnor.arica.data.DataHandler;
import cl.itnor.arica.lib.marker.Marker;

public class RadarPoints implements ScreenObj{
    public DataView view;
    float range;
    public static float RADIUS = 40; //radius in pixel on screen
    static float originX =0, originY = 0; //position on Screen
    static int radarColor = Color.argb(100, 0, 0, 200);

    @Override
    public void paint(PaintScreen paintScreen) {
        range = view.getRadius() * 1000; //radius in km
        paintScreen.setFill(true);
        paintScreen.setColor(radarColor);
        paintScreen.paintCircle(originX + RADIUS, originY + RADIUS, RADIUS);

        float scale = range / RADIUS;
        DataHandler jLayer = view.getDataHandler();

        for (int i = 0; i < jLayer.getMarkerCount(); i++) {
            Marker pm = jLayer.getMarker(i);
            float x = pm.getLocationVector().x / scale;
            float y = pm.getLocationVector().z / scale; //TODO: Check this. y = z ?
            if (pm.isActive() && ((x*x + y*y) < RADIUS*RADIUS)) {
                paintScreen.setFill(true);
                paintScreen.setColor(Color.YELLOW);
                paintScreen.paintRect(x+RADIUS-1, y+RADIUS-1,2,2);
            }
        }
    }

    @Override
    public float getWidth() {
        return RADIUS*2;
    }

    @Override
    public float getHeight() {
        return RADIUS*2;
    }
}

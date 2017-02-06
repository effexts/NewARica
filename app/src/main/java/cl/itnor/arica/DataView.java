package cl.itnor.arica;

import android.graphics.Color;
import android.graphics.Rect;
import android.location.Location;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cl.itnor.arica.data.DataHandler;
import cl.itnor.arica.lib.MixUtils;
import cl.itnor.arica.lib.gui.*;
import cl.itnor.arica.lib.gui.PaintScreen;
import cl.itnor.arica.lib.marker.Marker;
import cl.itnor.arica.lib.render.Camera;

import static android.view.KeyEvent.KEYCODE_CAMERA;
import static android.view.KeyEvent.KEYCODE_DPAD_CENTER;
import static android.view.KeyEvent.KEYCODE_DPAD_DOWN;
import static android.view.KeyEvent.KEYCODE_DPAD_LEFT;
import static android.view.KeyEvent.KEYCODE_DPAD_RIGHT;
import static android.view.KeyEvent.KEYCODE_DPAD_UP;

/**
 * Created by effexts on 1/19/17.
 */

public class DataView {
    private MixContext mixContext;
    DataViewListener listener;
    private MixState state = new MixState();
    private ArrayList<UIEvent> uiEvents = new ArrayList<UIEvent>();

    private boolean frozen; //the view can be frozen. debug
    private boolean isInit; //is the view inited
    private boolean isLauncherStarted;
    public int width, height;

     //* _NOT_ the android camera, the class that takes care of the transformation
    private Camera cam;

    private Location currentFix;
    private DataHandler dataHandler = new DataHandler();
    private float radius = 2;

    //timer to refresh the browser (45s)
    private Timer refresh = null;
    private final long refreshDelay = 45*1000;

    private RadarPoints radarPoints = new RadarPoints();
    private ScreenLine lrl = new ScreenLine();
    private ScreenLine rrl = new ScreenLine();
    private float rx = 10, ry = 20;
    private float addX = 0, addY = 0;

    private List<Marker> markers;

    public DataView(MixContext mixContext) {
        this.mixContext = mixContext;
    }

    public MixContext getMixContext() {
        return mixContext;
    }

    public boolean isLauncherStarted() {
        return isLauncherStarted;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public DataHandler getDataHandler() {
        return dataHandler;
    }

    public void setDataHandler(DataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    public boolean isDetailsView(){ return state.isDetailsView(); }
    public void setDetailsView(boolean detailsView) { state.setDetailsView(detailsView); }

    public void clearEvents() {
        synchronized (uiEvents) {
            uiEvents.clear();
        }
    }
    //Re-downloads the markers, and draw them on the map.
    public void refresh() { state.setNextLStatus(MixState.NOT_STARTED); }
    private void callRefreshToast() {
        mixContext.getActualMixView().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mixContext, mixContext.getResources().getString(R.string.refreshing), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void cancelRefreshTimer() {
        if (refresh != null)
            refresh.cancel();
    }

    public void doStart() {
        state.setNextLStatus(MixState.NOT_STARTED);
        mixContext.getLocationFinder().setLastLocation(currentFix);
    }

    public boolean isInited() {
        return isInit;
    }

    public void setInit(boolean init) {
        isInit = init;
    }

    public void init(int widthInit, int heightInit) {
        try {
            width = widthInit;
            height = heightInit;
            cam = new Camera(width, height, true);
            cam.setViewAngle(Camera.DEFAULT_VIEW_ANGLE);
            lrl.set(0, -RadarPoints.RADIUS);
            lrl.rotate(Camera.DEFAULT_VIEW_ANGLE/2);
            lrl.add(rx + RadarPoints.RADIUS, ry + RadarPoints.RADIUS);
            rrl.set(0, -RadarPoints.RADIUS);
            rrl.rotate(Camera.DEFAULT_VIEW_ANGLE/2);
            rrl.add(rx + RadarPoints.RADIUS, ry + RadarPoints.RADIUS);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        frozen = false;
        isInit = true;
    }

    public void draw(PaintScreen paintScreen) {
        mixContext.getRM(cam.transform);
        currentFix = mixContext.getLocationFinder().getCurrentLocation();
        state.calcPitchBearing(cam.transform);
        //Load Layer
        if (state.getNextLStatus() == MixState.NOT_STARTED && !frozen) {
            loadDrawLayer();
            markers = new ArrayList<Marker>();
        }
        else if (state.getNextLStatus() == MixState.PROCESSING) {
            //add all markers to markers.addAll()
//            DownloadManager dm = mixContext.getDownloadManager();
//            DownloadResult dRes = null;
//
//            markers.addAll(downloadDrawResults(dm, dRes));
            //TODO: Add support for Retrofit2
            if ( state.isDetailsView() /* //TODO: <- Elminar eso. finished downloading pois*/ ) {
                //mixare uses DownloadManager, i don't

                state.setNextLStatus(MixState.DONE);
                //add markers to datahandler
                dataHandler = new DataHandler();
                dataHandler.addMarkers(markers);
                dataHandler.onLocationChanged(currentFix);
                if (refresh == null) {
                    refresh = new Timer(false);
                    Date date = new Date(System.currentTimeMillis() + refreshDelay);
                    refresh.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            callRefreshToast();
                            refresh();
                        }
                    }, date, refreshDelay);
                }
            }
        }

        //Update markers
        dataHandler.updateActivationStatus(mixContext);
        ArrayList<LocalMarker> localMarkers = new ArrayList<>();
        for (int i = dataHandler.getMarkerCount() - 1; i>=0; i--) {
            Marker marker = dataHandler.getMarker(i);
            // if (ma.isActive() && (ma.getDistance() / 1000f < radius || ma
            // instanceof NavigationMarker || ma instanceof SocialMarker)) {
            if (marker.isActive() /*&& (ma.getDistance() / 1000f < radius)*/) {
                // To increase performance don't recalculate position vector
                // for every marker on every draw call, instead do this only
                // after onLocationChanged and after downloading new marker
                // if (!frozen)
                // ma.update(curFix);
                if (!frozen)
                    marker.calcPaint(cam,addX, addY);
                LocalMarker localMarker = (LocalMarker) marker;
                setNewPositionForMarker(localMarker, localMarkers);
                marker.draw(paintScreen);
                if (localMarker.isVisible())
                    localMarkers.add(localMarker);
            }
        }
        //Draw Rada
        drawRadar(paintScreen);
        // Get the next event
        UIEvent evt = null;
        synchronized (evt) {
            if (uiEvents.size() > 0) {
                evt = uiEvents.get(0);
                uiEvents.remove(0);
            }
        }
        if (evt != null)
            switch (evt.type) {
                case UIEvent.KEY:
                    handleKeyEvent((KeyEvent) evt);
                    break;
                case UIEvent.CLICK:
                    handleClickEvent((ClickEvent) evt);
        }
        state.setNextLStatus(MixState.PROCESSING);
    }

    private boolean handleClickEvent(ClickEvent event) {
        boolean eventHandled = false;

        if (state.getNextLStatus() == MixState.DONE)
            for (int i = 0; i < dataHandler.getMarkerCount() && !eventHandled; i++) {
                Marker pm = dataHandler.getMarker(i);
                eventHandled = pm.fClick(event.x, event.y, mixContext, state);
                if (eventHandled) {
                    Mixview.currentInstance.selectedMarker(dataHandler.getMarker(i).getTitle());
                    break;
                }
            }
        return eventHandled;
    }

    private void handleKeyEvent(KeyEvent event) {
        // Adjust marker position with keypad
        final float CONST = 10f;
        switch (event.keyCode) {
            case KEYCODE_DPAD_LEFT:
                addX -= CONST;
                break;
            case KEYCODE_DPAD_RIGHT:
                addX += CONST;
                break;
            case KEYCODE_DPAD_DOWN:
                addY += CONST;
                break;
            case KEYCODE_DPAD_UP:
                addY -= CONST;
                break;
            case KEYCODE_DPAD_CENTER:
                frozen = !frozen;
                break;
            case KEYCODE_CAMERA:
                frozen = !frozen;
                break; // freeze the overlay with the camera button
            default: //if key is set, then ignore event
                break;
        }
    }

    private void drawRadar(PaintScreen paintScreen) {
        String dirTxt = "";
        int bearing = (int) state.getCurrentBearing();
        int range = (int) (state.getCurrentBearing() / (360f/16f));
        if ( range == 15 || range == 0)
            dirTxt = getMixContext().getString(R.string.N);
        else if (range == 1 || range == 2)
            dirTxt = getMixContext().getString(R.string.NE);
        else if (range == 3 || range == 4)
            dirTxt = getMixContext().getString(R.string.E);
        else if (range == 5 || range == 6)
            dirTxt = getMixContext().getString(R.string.SE);
        else if (range == 7 || range == 8)
            dirTxt = getMixContext().getString(R.string.S);
        else if (range == 9 || range == 10)
            dirTxt = getMixContext().getString(R.string.SW);
        else if (range == 11 || range == 12)
            dirTxt = getMixContext().getString(R.string.W);
        else if (range == 13 || range == 14)
            dirTxt = getMixContext().getString(R.string.NW);

        radarPoints.view = this;
        paintScreen.paintObj(radarPoints, rx, ry, -state.getCurrentBearing(), 1);
        paintScreen.setFill(false);
        paintScreen.setColor(Color.argb(150, 0, 0, 220));
        paintScreen.paintLine(rrl.x, rrl.y, rx + RadarPoints.RADIUS, ry + RadarPoints.RADIUS);
        paintScreen.paintLine(lrl.x, lrl.y, rx + RadarPoints.RADIUS, ry + RadarPoints.RADIUS);
        paintScreen.setColor(Color.rgb(255, 255, 255));
        paintScreen.setFontSize(12);

        radarText(paintScreen, MixUtils.formatDist(radius*1000), rx+RadarPoints.RADIUS, ry+RadarPoints.RADIUS*2-10, false);
        radarText(paintScreen, "" + bearing + ((char) 176) + " " + dirTxt, rx+RadarPoints.RADIUS, ry-5, true);
    }

    private void radarText(PaintScreen paintScreen, String txt, float x, float y, boolean bg) {
        float padw = 4, padh = 2;
        float w = paintScreen.getTextWidth(txt) + padw * 2;
        float h = paintScreen.getTextAscent() + paintScreen.getTextDescent() + padh * 2;

        if (bg) {
            paintScreen.setColor(Color.rgb(0,0,0));
            paintScreen.setFill(true);
            paintScreen.paintRect(x-w/2, y-h/2, w, h);
            paintScreen.setColor(Color.rgb(255, 255, 255));
            paintScreen.setFill(false);
            paintScreen.paintRect(x-w/2, y-h/2, w, h);
        }
        paintScreen.paintText(padw + x - w/2, padh + paintScreen.getTextAscent() + y - h/2, txt, false);
    }

    private static final float MARKER_HEIGHT = 126;
    private static final float MARKER_WIDTH = 460;
    private void setNewPositionForMarker(LocalMarker localMarker, ArrayList<LocalMarker> localMarkers) {
        for (LocalMarker marker : localMarkers)
            if (markerIntersects(localMarker, marker))
                localMarker.cMarker.y = marker.cMarker.y - MARKER_HEIGHT;
    }

    private boolean markerIntersects(LocalMarker marker1, LocalMarker marker2) {
        return Rect.intersects( new Rect((int) marker1.cMarker.x, (int) marker1.cMarker.y, (int) (marker1.cMarker.x+MARKER_WIDTH), (int) (marker1.cMarker.y+MARKER_HEIGHT)),
                                new Rect((int) marker2.cMarker.x, (int) marker2.cMarker.y, (int) (marker2.cMarker.x+MARKER_WIDTH), (int) (marker2.cMarker.y+MARKER_HEIGHT)));
    }

    private void loadDrawLayer() {
        if (mixContext.getStartUrl().length() > 0) {
            //requestData(getstarturl) O sea, pedir datos de la url
            isLauncherStarted = true;
        }
        else {
            double lat = currentFix.getLatitude();
            double lon = currentFix.getLongitude();
            double alt = currentFix.getAltitude();
            state.setNextLStatus(MixState.PROCESSING);
            //TODO: Cambiar inicio de descarga de datos a una vez al obtener parámetros gps
            //mixContext.getDataSourceManager().requestDataFromAllActiveDataSource(lat, lon, alt,	radius);
        }
        //if no datasources are activated, means if mixstate == not_started ?
        if (state.getNextLStatus() == MixState.NOT_STARTED)
            state.setNextLStatus(MixState.DONE);
    }



}
class UIEvent {
    public static final int CLICK = 0;
    public static final int KEY = 1;
    public int type;
}

class ClickEvent extends UIEvent{
    float x, y;

    public ClickEvent(float x, float y) {
        this.type = CLICK;
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "ClickEvent{ "+x+","+y+" }";
    }
}

class KeyEvent extends UIEvent {
    public int keyCode;

    public KeyEvent(int keycode) {
        this.type = KEY;
        this.keyCode = keycode;
    }

    @Override
    public String toString() {
        return "KeyEvent{ "+ keyCode + " }";
    }
}
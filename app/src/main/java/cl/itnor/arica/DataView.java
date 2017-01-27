package cl.itnor.arica;

import android.location.Location;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cl.itnor.arica.data.DataHandler;
import cl.itnor.arica.lib.gui.*;
import cl.itnor.arica.lib.gui.PaintScreen;
import cl.itnor.arica.lib.marker.Marker;
import cl.itnor.arica.lib.render.Camera;

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

            //TODO: Add support for Retrofit2
            if ( /*finished downloading pois*/ ) {
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
    }




    private class UIEvent {
        public static final int CLICK = 0;
        public static final int KEY = 1;
        public int type;
    }
}

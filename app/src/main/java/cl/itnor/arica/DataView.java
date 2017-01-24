package cl.itnor.arica;

import android.location.Location;

import java.util.ArrayList;
import java.util.Timer;

/**
 * Created by effexts on 1/19/17.
 */

class DataView {
    private MixContext mixContext;
    DataViewListener listener;
    private MixState state = new MixState();
    private ArrayList<UIEvent> uiEvents = new ArrayList<UIEvent>();
    private Timer refresh = null;

    private Location currentFix;

    private boolean frozen; //the view can be frozen. debug
    private boolean isInit; //is the view inited
    private boolean isLauncherStarted;


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

    public void refresh(){
        state.setNextLStatus(MixState.NOT_STARTED);
    }

    public void clearEvents() {
        synchronized (uiEvents) {
            uiEvents.clear();
        }
    }

    public void cancelRefreshTimer() {
        if (refresh != null)
            refresh.cancel();
    }

    public void doStart() {
        state.setNextLStatus(MixState.NOT_STARTED);
        mixContext.getLocationFinder().setLastLocation(currentFix);
    }





    private class UIEvent {
        public static final int CLICK = 0;
        public static final int KEY = 1;
        public int type;
    }
}

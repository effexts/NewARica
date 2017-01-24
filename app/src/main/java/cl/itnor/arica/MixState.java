package cl.itnor.arica;

import cl.itnor.arica.lib.MixContextInterface;
import cl.itnor.arica.lib.MixUtils;
import cl.itnor.arica.lib.render.Matrix;
import cl.itnor.arica.lib.render.MixVector;

/**
 * Created by effexts on 1/21/17.
 */

public class MixState {
    public static int NOT_STARTED = 0;
    public static int PROCESSING = 1;
    public static int READY = 2;
    public static int DONE = 3;

    private int nextLStatus = MixState.NOT_STARTED;
    private float currentBearing;
    private float currentPitch;
    private boolean detailsView;


    public boolean handleEvent(MixContextInterface ctx, String onPress){
        if (onPress != null && onPress.startsWith("webpage")) {
            try {
                String webpage = MixUtils.parseAction(onPress);
                this.detailsView = true;
                ctx.loadMixViewWebPage(webpage);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return true;
    }

    public void calcPitchBearing(Matrix rotationM) {
        MixVector looking = new MixVector();
        rotationM.transpose();
        looking.set(1,0,0);
        looking.prod(rotationM);
        this.currentBearing = (MixUtils.getAngle(0, 0, looking.x, looking.z) + 360) % 360;

        rotationM.transpose();
        looking.set(0, 1, 0);
        looking.prod(rotationM);
        this.currentPitch = - MixUtils.getAngle(0, 0, looking.y, looking.z);
    }


    public int getNextLStatus() {
        return nextLStatus;
    }

    public void setNextLStatus(int nextLStatus) {
        this.nextLStatus = nextLStatus;
    }

    public float getCurrentBearing() {
        return currentBearing;
    }

    public void setCurrentBearing(float currentBearing) {
        this.currentBearing = currentBearing;
    }

    public float getCurrentPitch() {
        return currentPitch;
    }

    public void setCurrentPitch(float currentPitch) {
        this.currentPitch = currentPitch;
    }

    public boolean isDetailsView() {
        return detailsView;
    }

    public void setDetailsView(boolean detailsView) {
        this.detailsView = detailsView;
    }
}

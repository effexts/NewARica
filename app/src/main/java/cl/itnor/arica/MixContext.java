package cl.itnor.arica;

import android.content.ContentResolver;
import android.content.ContextWrapper;
import android.content.Intent;
import android.widget.Toast;

import cl.itnor.arica.lib.MixContextInterface;
import cl.itnor.arica.lib.render.Matrix;
import cl.itnor.arica.mgr.location.LocationFinder;
import cl.itnor.arica.mgr.location.LocationFinderFactory;
import cl.itnor.arica.mgr.webcontent.WebContentManager;
import cl.itnor.arica.mgr.webcontent.WebContentManagerFactory;

/**
 * Created by effexts on 1/19/17.
 */

public class MixContext extends ContextWrapper implements MixContextInterface {
    public static final String TAG = "Turistear";
    private Mixview mixView;
    private Matrix rotationM = new Matrix();
    private LocationFinder locationFinder;
    private WebContentManager webContentManager;

    public MixContext(Mixview appCtx) {
        super(appCtx);
        mixView = appCtx;
        getLocationFinder().switchOn();
        getLocationFinder().findLocation();
    }

    public String getStartUrl() {
        Intent intent = getActualMixView().getIntent();
        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_VIEW))
            return intent.getData().toString();
        else return "";
    }
    public void getRM(cl.itnor.arica.lib.render.Matrix dest) {
         synchronized (rotationM) {
             dest.set(rotationM);
         }
    }

    public void loadMixViewWebPage(String url) throws Exception {
        getWebContentManager().loadWebPage(url, getActualMixView());
    }
    public void doResume(Mixview mixView){
        setActualMixView(mixView);
    }

    public void updateSmoothRotation(Matrix smoothR) {
        synchronized (rotationM) {
            rotationM.set(smoothR);
        }
    }


    public Mixview getActualMixView() {
        synchronized (mixView)  {
            return this.mixView;
        }
    }
    public void setActualMixView(Mixview mixView){
        synchronized (mixView) { this.mixView = mixView; }
    }

    public ContentResolver getContentResolver() {
        ContentResolver out = super.getContentResolver();
        if (out == null)
            out = getActualMixView().getContentResolver();
        return out;
    }

    public Mixview getMixView() {
        return mixView;
    }
   
    public void setMixView(Mixview mixView) {
        this.mixView = mixView;
    }

    public Matrix getRotationM() {
        return rotationM;
    }

    public void setRotationM(Matrix rotationM) {
        this.rotationM = rotationM;
    }

    public LocationFinder getLocationFinder() {
        if (this.locationFinder != null )
            locationFinder = LocationFinderFactory.makeLocationFinder(this);
        return locationFinder;
    }

    public void setLocationFinder(LocationFinder locationFinder) {
        this.locationFinder = locationFinder;
    }

    public WebContentManager getWebContentManager() {
        if (this.webContentManager == null)
            webContentManager = WebContentManagerFactory.makeWebContentManager(this);
        return webContentManager;
    }

    public void setWebContentManager(WebContentManager webContentManager) {
        this.webContentManager = webContentManager;
    }

    public void doPopUp(final String string) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }
    public void doPopUp(int ridOfString) {
        doPopUp(this.getString(ridOfString));
    }
}

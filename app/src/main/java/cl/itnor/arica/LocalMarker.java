package cl.itnor.arica;

import android.location.Location;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.IllegalFormatCodePointException;

import cl.itnor.arica.lib.MixContextInterface;
import cl.itnor.arica.lib.MixStateInterface;
import cl.itnor.arica.lib.MixUtils;
import cl.itnor.arica.lib.gui.*;
import cl.itnor.arica.lib.gui.PaintScreen;
import cl.itnor.arica.lib.marker.Marker;
import cl.itnor.arica.lib.reality.PhysicalPlace;
import cl.itnor.arica.lib.render.Camera;
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
    private MixVector origin = new MixVector(0,0,0);
    private MixVector upV= new MixVector(0,1,0);
    private ScreenLine pPt = new ScreenLine();

    public Label txtLab = new Label();
    protected TextObj textBlock;

    public LocalMarker(String ID, String title, String link, double latitude, double longitude, double altitude, int type) {
        super();
        this.active = false;
        this.title = title;
        this.mGeoLoc = new PhysicalPlace(latitude,longitude,altitude);
        if (link != null && link.length()>0){
            try {
                URL = "webpage:"+ URLDecoder.decode(link,"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                Log.e("Turistear", e.toString());
                e.printStackTrace();
            }
        }
        this.ID = ID + "##" + type + "##" + title;
    }

    @Override
    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isUnderline() {
        return underline;
    }

    public void setUnderline(boolean underline) {
        this.underline = underline;
    }

    @Override
    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public PhysicalPlace getmGeoLoc() {
        return mGeoLoc;
    }

    public void setmGeoLoc(PhysicalPlace mGeoLoc) {
        this.mGeoLoc = mGeoLoc;
    }

    @Override
    public double getDistance() {
        return distance;
    }

    @Override
    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public MixVector getcMarker() {
        return cMarker;
    }

    public void setcMarker(MixVector cMarker) {
        this.cMarker = cMarker;
    }

    public MixVector getSingMarker() {
        return singMarker;
    }

    public void setSingMarker(MixVector singMarker) {
        this.singMarker = singMarker;
    }

    @Override
    public MixVector getLocationVector() {
        return locationVector;
    }

    public void setLocationVector(MixVector locationVector) {
        this.locationVector = locationVector;
    }

    public MixVector getOrigin() {
        return origin;
    }

    public void setOrigin(MixVector origin) {
        this.origin = origin;
    }

    public MixVector getUpV() {
        return upV;
    }

    public void setUpV(MixVector upV) {
        this.upV = upV;
    }

    public ScreenLine getpPt() {
        return pPt;
    }

    public void setpPt(ScreenLine pPt) {
        this.pPt = pPt;
    }
    public double getLatitude() { return mGeoLoc.getLatitude(); }
    public double getLongitude() { return mGeoLoc.getLongitude(); }
    public double getAltitude() { return mGeoLoc.getAltitude(); }

    private void cMarker(MixVector originalPoint, Camera viewCam, float addX, float addY) {
        MixVector tmpA = new MixVector(originalPoint);
        MixVector tmpC = new MixVector(upV);
        tmpA.add(locationVector);
        tmpC.add(locationVector);
        tmpA.sub(viewCam.lco);
        tmpC.sub(viewCam.lco);
        tmpA.prod(viewCam.transform);
        tmpC.prod(viewCam.transform);

        MixVector tmpB = new MixVector();
        viewCam.projectPoint(tmpA,tmpB, addX, addY);
        cMarker.set(tmpB);
        viewCam.projectPoint(tmpC, tmpB, addX, addY);
        singMarker.set(tmpB);
    }
    private void calcV() {
        isVisible = cMarker.z < -1f;
    }
    public void update(Location currentGPSFix) {
        // An elevation of 0.0 probably means that the elevation of the
        // POI is not known and should be set to the users GPS height
        // Note: this could be improved with calls to
        // http://www.geonames.org/export/web-services.html#astergdem
        // to estimate the correct height with DEM models like SRTM, AGDEM or GTOPO30
        if (mGeoLoc.getAltitude()==0.0)
            mGeoLoc.setAltitude(currentGPSFix.getAltitude());
        PhysicalPlace.convertLocToVec(currentGPSFix,mGeoLoc,locationVector);
    }
    public void calcPaint(Camera viewCam, float addX, float addY) {
        cMarker(origin, viewCam, addX, addY);
        calcV();
    }

    private boolean isClickValid(float x, float y) {
        if (!isActive() && !this.isVisible) return false;
        float currentAngle = MixUtils.getAngle(cMarker.x, cMarker.y, singMarker.x, singMarker.y);

        pPt.x = x - singMarker.x;
        pPt.y = y - singMarker.y;
        pPt.rotate((float) Math.toRadians(-(currentAngle+90)));
        pPt.x += txtLab.getX();
        pPt.y += txtLab.getY();

        float objX = txtLab.getX() - txtLab.getWidth()/2;
        float objY = txtLab.getY() - txtLab.getHeight()/2;
        float objW = txtLab.getWidth();
        float objH = txtLab.getHeight();

        return pPt.x > objX && pPt.x < objX + objW && pPt.y > objY && pPt.y < objY + objH;
    }

    public void draw(PaintScreen paintScreen) {
        drawIcon(paintScreen);
        drawTextBlock(paintScreen);
    }

    private void drawTextBlock(PaintScreen paintScreen) {
        float maxHeight = Math.round(paintScreen.getHeight()/10f)+1;
        String textStr;
        double d = distance;
        DecimalFormat decimalFormat = new DecimalFormat("@#");
        if (d<1000.0)
            textStr = title + " ("+ decimalFormat.format(d) + "m)";
        else {
            d = d/1000.0;
            textStr = title + " ("+ decimalFormat.format(d) + "km)";
        }
        textBlock = new TextObj( textStr, Math.round(maxHeight/2f)+1, 250, paintScreen, underline);
        if (isVisible) {
            float currentAngle = MixUtils.getAngle(cMarker.x, cMarker.y, singMarker.x, singMarker.y);
            txtLab.prepare(textBlock);
            paintScreen.setStrokeWidth(1f);
            paintScreen.setFill(true);
            paintScreen.paintObj(txtLab, singMarker.x - txtLab.getWidth()/2, singMarker.y + maxHeight, currentAngle + 90, 1);
        }
    }

    public boolean fClick(float x, float y, MixContextInterface ctx, MixStateInterface state) {
        boolean eventHandled = false;
        if (!isClickValid(x,y))
            eventHandled = state.handleEvent(ctx, URL);
        return eventHandled;
    }

    private void drawIcon(PaintScreen paintScreen) {
        //		GlobalApplication context = GlobalApplication.getInstance();
//
//		Bitmap poi = BitmapFactory.decodeResource(context.getResources(), R.mipmap.poi);
//		dw.paintBitmap(poi, cMarker.x - poi.getWidth()/2, cMarker.y - poi.getHeight()/2);
    }

    public int compareTo(Marker another) {
        Marker leftPm = this;
        Marker rightPm = another;

        return Double.compare(leftPm.getDistance(), rightPm.getDistance());
    }

    @Override
    public boolean equals(Object marker) {
        return this.ID.equals(((Marker) marker).getID());
    }

    @Override
    public int hashCode() {
        return this.ID.hashCode();
    }

    abstract public int getMaxObjects();

    public Label getTxtLab() {
        return txtLab;
    }

    public void setTxtLab(Label txtLab) {
        this.txtLab = txtLab;
    }

    public TextObj getTextBlock() {
        return textBlock;
    }

    public void setTextBlock(TextObj textBlock) {
        this.textBlock = textBlock;
    }
}

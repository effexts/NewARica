package cl.itnor.arica;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import cl.itnor.arica.lib.render.Matrix;

import static android.view.ViewGroup.*;

/**
 * Created by effexts on 1/19/17.
 */

public class Mixview extends Activity implements SensorEventListener, OnTouchListener, DataViewListener {
    private CameraSurface camScreen;
    private AugmentedView augScreen;

    private boolean isInited;
    private static PaintScreen paintScreen;
    private static DataView dataView;
    private boolean fError;
    public static Mixview currentInstance;
    public static Bitmap marker_arrow;

    private MixViewDataHolder mixViewData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            getMixViewData().setmWakeLock(pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "Turistear"));
            killOnError();
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            maintainCamera();
            maintainAugmentR();
            if (!isInited){
                setPaintScreen(new PaintScreen());
                setDataView(new DataView(getMixViewData().getMixContext()));
                dataView.listener = this;
                isInited = true;
            }
            currentInstance = this;

        }
        catch (Exception ex) { doError(ex); }
    }

    @Override
    protected void onPause() {
        super.onPause();
        currentInstance = null;
        getMixViewData().getSensorMgr().unregisterListener(this, getMixViewData().getSensorGrav());
        getMixViewData().getSensorMgr().unregisterListener(this, getMixViewData().getSensorMag());
        getMixViewData().setSensorMgr(null);
        getMixViewData().getMixContext().getLocationFinder().switchOff();
//        getMixViewData().getMixContent().getDownloadManager().switchOff();
        if (getDataView() != null) getDataView().cancelRefreshTimer();
        if (fError) finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            this.getMixViewData().getmWakeLock().acquire();
            killOnError();
            getMixViewData().getMixContext().doResume(this);
            repaint();
            getDataView().doStart();
            getDataView().clearEvents();

            float angleX, angleY;
            int marker_orientation = -90;
            int rotation = Compatibility.getRotation(this);

            //display text from LTR and keep it horizontal
            angleX = (float) Math.toRadians(marker_orientation);
            getMixViewData().getM1().set(   1f, 0f, 0f,
                                            0f, (float) Math.cos(angleX), (float) -Math.sin(angleX),
                                            0f, (float) Math.sin(angleX), (float) Math.cos(angleX));
            angleX = (float) Math.toRadians(marker_orientation);
            angleY = (float) Math.toRadians(marker_orientation);
            if (rotation == 1){
                getMixViewData().getM2().set(   1f, 0f, 0f,
                                                0f, (float) Math.cos(angleX), (float) -Math.sin(angleX),
                                                0f, (float) Math.sin(angleX), (float) Math.cos(angleX));
                getMixViewData().getM3().set(   (float) Math.cos(angleY), 0f, (float) Math.sin(angleY),
                                                0f, 1f, 0f,
                                                (float) -Math.sin(angleY), 0f, (float) Math.cos(angleY));
            }
            else {
                getMixViewData().getM2().set(   (float) Math.cos(angleX), 0f, (float) Math.sin(angleX),
                                                0f, 1f, 0f,
                                                (float) -Math.sin(angleX), 0f, (float) Math.cos(angleX));
                getMixViewData().getM3().set(   1f, 0f, 0f,
                                                0f, (float) Math.cos(angleY), (float) -Math.sin(angleY),
                                                0f, (float) Math.sin(angleY), (float) Math.cos(angleY));
            }
            getMixViewData().getM4().toIdentity();
            for (int i=0; i < getMixViewData().getHistR().length; i++)
                getMixViewData().getHistR()[i] = new Matrix();

            getMixViewData().setSensorMgr((SensorManager) getSystemService(SENSOR_SERVICE));
            getMixViewData().setSensors(getMixViewData().getSensorMgr().getSensorList(Sensor.TYPE_ACCELEROMETER));
            if (getMixViewData().getSensors().size() > 0)
                getMixViewData().setSensorGrav(getMixViewData().getSensors().get(0));

            getMixViewData().setSensors(getMixViewData().getSensorMgr().getSensorList(Sensor.TYPE_MAGNETIC_FIELD));
            if (getMixViewData().getSensors().size() > 0)
                getMixViewData().setSensorMag(getMixViewData().getSensors().get(0));

            getMixViewData().getSensorMgr().registerListener(this,getMixViewData().getSensorGrav(), SensorManager.SENSOR_DELAY_GAME);
            getMixViewData().getSensorMgr().registerListener(this,getMixViewData().getSensorMag(), SensorManager.SENSOR_DELAY_GAME);

            try {
                GeomagneticField gmf = getMixViewData().getMixContext().getLocationFinder().getGeomagneticField();
                angleY = (float) Math.toRadians(-gmf.getDeclination());
                getMixViewData().getM4().set(   (float) Math.cos(angleY), 0f, (float) Math.sin(angleY),
                                                0f, 1f, 0f,
                                                (float) -Math.sin(angleY), 0f, (float) Math.cos(angleY));
            }
            catch (Exception ex) {
                Log.d("Turistear", "GPS Initialize Error", ex);
                ex.printStackTrace();
            }

            getMixViewData().getMixContext().getLocationFinder().switchOn();
            currentInstance = this;

        }
        catch (Exception ex){
            doError(ex);
            try {
                if (getMixViewData().getSensorMgr() != null) {
                    getMixViewData().getSensorMgr().unregisterListener(this, getMixViewData().getSensorGrav());
                    getMixViewData().getSensorMgr().unregisterListener(this, getMixViewData().getSensorMag());
                    getMixViewData().setSensorMgr(null);
                }
                if (getMixViewData().getMixContext() != null)
                    getMixViewData().getMixContext().getLocationFinder().switchOff();
            }
            catch (Exception ignore) {}
        }

        Log.d("Turistear", "resume");

    }

    @Override
    public void onRestart() {
        super.onRestart();
        maintainCamera();
        maintainAugmentR();
        currentInstance = this;
    }

    public void doError(Exception ex1) {
        if (!fError) {
            fError = true;
            setErrorDialog();
            ex1.printStackTrace();
        }
        try { augScreen.invalidate(); }
        catch (Exception ignore) {}
    }

    private void setErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.connection_error_dialog));
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.connection_error_dialog_button1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                fError=false;
                try {
                    maintainCamera();
                    maintainAugmentR();
                    repaint();
                }
                catch (Exception ignore) {}
            }
        });
        builder.setNeutralButton(R.string.connection_error_dialog_button2, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent1 = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                startActivityForResult(intent1,42);
            }
        });
        builder.setNegativeButton(R.string.connection_error_dialog_button3, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                System.exit(0);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void maintainAugmentR() {
        if (augScreen == null)
            augScreen = new AugmentedView(this);
        addContentView(augScreen, new ActionBar.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    }

    private void maintainCamera() {
        if (camScreen == null)
            camScreen = new CameraSurface(this);
        setContentView(camScreen);
    }

    public void killOnError() throws Exception {
        if (fError) throw new Exception();
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        try {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                getMixViewData().getGrav()[0] = sensorEvent.values[0];
                getMixViewData().getGrav()[1] = sensorEvent.values[1];
                getMixViewData().getGrav()[2] = sensorEvent.values[2];
                augScreen.postInvalidate();
            }
            else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                getMixViewData().getMag()[0] = sensorEvent.values[0];
                getMixViewData().getMag()[1] = sensorEvent.values[1];
                getMixViewData().getMag()[2] = sensorEvent.values[2];
                augScreen.postInvalidate();
            }
            SensorManager.getRotationMatrix(getMixViewData().getRTmp(), getMixViewData().getI(), getMixViewData().getGrav(), getMixViewData().getMag());
            int rotation = Compatibility.getRotation(this);
            if (rotation == 1)
                 SensorManager.remapCoordinateSystem(getMixViewData().getRTmp(), SensorManager.AXIS_X, SensorManager.AXIS_MINUS_Z, getMixViewData().getRot());
            else SensorManager.remapCoordinateSystem(getMixViewData().getRTmp(), SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_Z, getMixViewData().getRot());

            getMixViewData().getTempR().set(
                    getMixViewData().getRot()[0],
                    getMixViewData().getRot()[1],
                    getMixViewData().getRot()[2],
                    getMixViewData().getRot()[3],
                    getMixViewData().getRot()[4],
                    getMixViewData().getRot()[5],
                    getMixViewData().getRot()[6],
                    getMixViewData().getRot()[7],
                    getMixViewData().getRot()[8]);
            getMixViewData().getHistR()[getMixViewData().getrHistIdx()].set(getMixViewData().getFinalR());
            getMixViewData().setrHistIdx(getMixViewData().getrHistIdx()+1);
            if (getMixViewData().getrHistIdx() >= getMixViewData().getHistR().length)
                getMixViewData().setrHistIdx(0);
            getMixViewData().getSmoothR().set(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f);
            for (int i=0; i < getMixViewData().getHistR().length; i++)
                getMixViewData().getSmoothR().add(getMixViewData().getHistR()[i]);
            getMixViewData().getSmoothR().mult(1/(float) getMixViewData().getHistR().length);
            getMixViewData().getMixContext().updateSmoothRotation(getMixViewData().getSmoothR());
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (sensor.getType() ==  Sensor.TYPE_MAGNETIC_FIELD && accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE && getMixViewData().getCompassErrorDisplayed() == 0) {
            for (int i = 0; i < 2; i++)
                Toast.makeText(getMixViewData().getMixContext(), R.string.compass_calibration_unrealiable, Toast.LENGTH_LONG).show();
            getMixViewData().setCompassErrorDisplayed(getMixViewData().getCompassErrorDisplayed()+1);
        }

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        getDataView().setFrozen(false);
        return false;
    }

    @Override
    public void selectedMarker(String title) {

    }

    public void refresh() {
        dataView.refresh();
    }
    public CameraSurface getCamScreen() {
        return camScreen;
    }

    public void setCamScreen(CameraSurface camScreen) {
        this.camScreen = camScreen;
    }

    public AugmentedView getAugScreen() {
        return augScreen;
    }


    public void setAugScreen(AugmentedView augScreen) {
        this.augScreen = augScreen;
    }

    public boolean isInited() {
        return isInited;
    }

    public void setInited(boolean inited) {
        isInited = inited;
    }

    public static PaintScreen getPaintScreen() {
        return paintScreen;
    }

    public static void setPaintScreen(PaintScreen paintScreen) {
        Mixview.paintScreen = paintScreen;
    }

    public static DataView getDataView() {
        return dataView;
    }

    public static void setDataView(DataView dataView) {
        Mixview.dataView = dataView;
    }

    public boolean isfError() {
        return fError;
    }

    public void setfError(boolean fError) {
        this.fError = fError;
    }

    public static Mixview getCurrentInstance() {
        return currentInstance;
    }

    public static void setCurrentInstance(Mixview currentInstance) {
        Mixview.currentInstance = currentInstance;
    }

    public static Bitmap getMarker_arrow() {
        return marker_arrow;
    }

    public static void setMarker_arrow(Bitmap marker_arrow) {
        Mixview.marker_arrow = marker_arrow;
    }
    private MixViewDataHolder getMixViewData() {
        if (mixViewData == null) { mixViewData = new MixViewDataHolder(new MixContext(this)); }
        return mixViewData;
    }

    public void setMixViewData(MixViewDataHolder mixViewData) {
        this.mixViewData = mixViewData;
    }

    public void repaint() {
        getDataView().clearEvents();
        setDataView(null);
        setDataView(new DataView(mixViewData.getMixContext()));
        setPaintScreen(new PaintScreen());
    }
}

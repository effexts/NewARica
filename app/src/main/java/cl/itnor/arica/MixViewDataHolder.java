package cl.itnor.arica;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import java.util.List;

import cl.itnor.arica.lib.render.Matrix;

/**
 * Created by effexts on 1/19/17.
 */

class MixViewDataHolder {
    private final MixContext mixContext;
    private WakeLock mWakeLock;
    private SensorManager sensorMgr;
    private List<Sensor> sensors;
    private Sensor sensorGrav;
    private Sensor sensorMag;
    private int rHistIdx, compassErrorDisplayed;
    private float[] RTmp, Rot, I, grav, mag;
    private Matrix tempR, finalR, smoothR, m1, m2, m3, m4;
    private Matrix[] histR;

    public MixViewDataHolder(MixContext mixContext) {
        this.mixContext = mixContext;
        this.rHistIdx = 0;
        this.compassErrorDisplayed = 0;
        this.RTmp = new float[9];
        this.Rot = new float[9];
        this.I = new float[9];
        this.grav = new float[9];
        this.mag = new float[9];
        this.tempR = new Matrix();
        this.finalR = new Matrix();
        this.smoothR = new Matrix();
        this.m1 = new Matrix();
        this.m2 = new Matrix();
        this.m3 = new Matrix();
        this.m4 = new Matrix();
        this.histR = new Matrix[60];
    }

    public MixContext getMixContext() {
        return mixContext;
    }

    public WakeLock getmWakeLock() {
        return mWakeLock;
    }

    public void setmWakeLock(WakeLock mWakeLock) {
        this.mWakeLock = mWakeLock;
    }

    public SensorManager getSensorMgr() {
        return sensorMgr;
    }

    public void setSensorMgr(SensorManager sensorMgr) {
        this.sensorMgr = sensorMgr;
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(List<Sensor> sensors) {
        this.sensors = sensors;
    }

    public Sensor getSensorGrav() {
        return sensorGrav;
    }

    public void setSensorGrav(Sensor sensorGrav) {
        this.sensorGrav = sensorGrav;
    }

    public Sensor getSensorMag() {
        return sensorMag;
    }

    public void setSensorMag(Sensor sensorMag) {
        this.sensorMag = sensorMag;
    }

    public int getrHistIdx() {
        return rHistIdx;
    }

    public void setrHistIdx(int rHistIdx) {
        this.rHistIdx = rHistIdx;
    }

    public int getCompassErrorDisplayed() {
        return compassErrorDisplayed;
    }

    public void setCompassErrorDisplayed(int compassErrorDisplayed) {
        this.compassErrorDisplayed = compassErrorDisplayed;
    }

    public float[] getRTmp() {
        return RTmp;
    }

    public void setRTmp(float[] RTmp) {
        this.RTmp = RTmp;
    }

    public float[] getRot() {
        return Rot;
    }

    public void setRot(float[] rot) {
        Rot = rot;
    }

    public float[] getI() {
        return I;
    }

    public void setI(float[] i) {
        I = i;
    }

    public float[] getGrav() {
        return grav;
    }

    public void setGrav(float[] grav) {
        this.grav = grav;
    }

    public float[] getMag() {
        return mag;
    }

    public void setMag(float[] mag) {
        this.mag = mag;
    }

    public Matrix getTempR() {
        return tempR;
    }

    public void setTempR(Matrix tempR) {
        this.tempR = tempR;
    }

    public Matrix getFinalR() {
        return finalR;
    }

    public void setFinalR(Matrix finalR) {
        this.finalR = finalR;
    }

    public Matrix getSmoothR() {
        return smoothR;
    }

    public void setSmoothR(Matrix smoothR) {
        this.smoothR = smoothR;
    }

    public Matrix getM1() {
        return m1;
    }

    public void setM1(Matrix m1) {
        this.m1 = m1;
    }

    public Matrix getM2() {
        return m2;
    }

    public void setM2(Matrix m2) {
        this.m2 = m2;
    }

    public Matrix getM3() {
        return m3;
    }

    public void setM3(Matrix m3) {
        this.m3 = m3;
    }

    public Matrix getM4() {
        return m4;
    }

    public void setM4(Matrix m4) {
        this.m4 = m4;
    }

    public Matrix[] getHistR() {
        return histR;
    }

    public void setHistR(Matrix[] histR) {
        this.histR = histR;
    }


}

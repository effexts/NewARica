package cl.itnor.arica.lib.gui;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by effexts on 1/26/17.
 */

public class ScreenLine implements Parcelable {
    public float x, y;

    public ScreenLine() { set(0,0); }

    public ScreenLine(float x, float y) { set(x,y); }

    protected ScreenLine(Parcel in) {
        x = in.readFloat();
        y = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(x);
        dest.writeFloat(y);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ScreenLine> CREATOR = new Creator<ScreenLine>() {
        @Override
        public ScreenLine createFromParcel(Parcel in) {
            return new ScreenLine(in);
        }

        @Override
        public ScreenLine[] newArray(int size) {
            return new ScreenLine[size];
        }
    };

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void rotate(float t) {
        float xp = (float) (Math.cos(t)*x - Math.sin(t)*y);
        float yp = (float) (Math.sin(t)*x + Math.cos(t)*y);
        x = xp;
        y = yp;
    }

    public void add(float x, float y) {
        this.x += x;
        this.y += y;
    }
}

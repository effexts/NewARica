package cl.itnor.arica.lib.gui;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by effexts on 1/27/17.
 */

public class Label implements ScreenObj, Parcelable {
    private float x, y;
    private float width, height;
    private ScreenObj screenObj;

    public Label() {
    }

    public Label(Parcel in) {
        x = in.readFloat();
        y = in.readFloat();
        width = in.readFloat();
        height = in.readFloat();
    }

    public static final Creator<Label> CREATOR = new Creator<Label>() {
        @Override
        public Label createFromParcel(Parcel in) {
            return new Label(in);
        }

        @Override
        public Label[] newArray(int size) {
            return new Label[size];
        }
    };

    public void prepare(ScreenObj drawObj) {
        screenObj = drawObj;
        float w = screenObj.getWidth();
        float h = screenObj.getHeight();

        x = w/2;
        y = 0;

        width = w*2;
        height = h*2;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloat(x);
        parcel.writeFloat(y);
        parcel.writeFloat(width);
        parcel.writeFloat(height);
    }

    @Override
    public void paint(PaintScreen paintScreen) {
        paintScreen.paintObj(screenObj, x, y, 0, 1);
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}

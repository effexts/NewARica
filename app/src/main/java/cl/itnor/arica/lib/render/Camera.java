package cl.itnor.arica.lib.render;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by effexts on 1/25/17.
 */

public class Camera implements Parcelable {
    public static final float DEFAULT_VIEW_ANGLE = (float) Math.toRadians(45);
    public int width, height;
    public Matrix transform = new Matrix();
    public MixVector lco = new MixVector();
    float viewAngle;
    float dist;

    public Camera(int width, int height) { this(width,height, true); }

    public Camera(int width, int height, boolean init) {
        this.width = width;
        this.height = height;
        transform.toIdentity();
        lco.set(0,0,0);
    }

    public Camera(Parcel in) { readFromParcel(in); }

    public static final Creator<Camera> CREATOR = new Creator<Camera>() {
        @Override
        public Camera createFromParcel(Parcel in) {
            return new Camera(in);
        }

        @Override
        public Camera[] newArray(int size) {
            return new Camera[size];
        }
    };

    public float getViewAngle() {
        return viewAngle;
    }

    public void setViewAngle(float viewAngle) {
        this.viewAngle = viewAngle;
        this.dist = (this.width/2) / (float) Math.tan(viewAngle/2);
    }

    public void projectPoint(MixVector originPoint, MixVector projectedPoint, float addX, float addY) {
        projectedPoint.x = dist * originPoint.x / -originPoint.z;
        projectedPoint.y = dist * originPoint.y / -originPoint.z;
        projectedPoint.z = originPoint.z;
        projectedPoint.x = projectedPoint.x +addX + width/2;
        projectedPoint.y = -projectedPoint.y +addY + height/2;
    }

    @Override
    public String toString() {
        return "CAM(" + width + ", " + height + ")";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(width);
        parcel.writeInt(height);
        parcel.writeParcelable(transform,0);
        parcel.writeParcelable(lco,0);
        parcel.writeFloat(viewAngle);
        parcel.writeFloat(dist);
    }

    public void readFromParcel(Parcel in) {
        width = in.readInt();
        height = in.readInt();
        transform = in.readParcelable(Matrix.class.getClassLoader());
        lco = in.readParcelable(Matrix.class.getClassLoader());
        viewAngle = in.readFloat();
        dist = in.readFloat();
    }
}

package cl.itnor.arica.lib.render;

/**
 * Created by effexts on 1/20/17.
 */

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class holds information of a point in a three-dimensional coordinate
 * system. It holds the values for the x-, y- and z-axis, which can be modified
 * through several methods. (for example adding and subtracting points) The
 * distance from the origin of the coordinate system to the point represents the
 * vector. The application uses vectors to describe distances on the map.
 *
 * @author daniele
 *
 */
public class MixVector implements Parcelable {
    public float x, y, z;

    public MixVector() { this(0,0,0); }
    public MixVector(MixVector v) { this( v.x, v.y, v.z); }
    public MixVector(float v[]) { this( v[0], v[1],v[2]); }
    public MixVector(float x, float y, float z) { set(x, y, z); }
    public MixVector(Parcel in) { readParcel(in); }

    public static final Creator<MixVector> CREATOR = new Creator<MixVector>() {
        @Override
        public MixVector createFromParcel(Parcel in) { return new MixVector(in); }

        @Override
        public MixVector[] newArray(int size) { return new MixVector[size]; }
    };

    public boolean equals(Object obj) {
        MixVector v = (MixVector) obj;
        return (v.x == x && v.y == y && v.z == z);
    }
    public int hashCode() {
        Float xf = x;
        Float yf = y;
        Float zf = z;
        return xf.hashCode()+yf.hashCode()+zf.hashCode();
    }

    @Override
    public String toString() { return "<" + x + ", "+ y + ", " + z + ">"; }
    public void set(MixVector v) { set(v.x, v.y, v.z); }
    public void set(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public void add(float x, float y , float z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }
    public void add(MixVector v) { add(v.x, v.y, v.z); }
    public void sub(float x, float y, float z){ add(-x, -y, -z); }
    public void sub(MixVector v) { add( -v.x, -v.y, -v.z); }
    public void mult(float s) {
        x *= s;
        y *= s;
        z *= s;
    }
    public void divide(float s) {
        x /= s;
        y /= s;
        z /= s;
    }
    float lenght() { return (float) Math.sqrt( x*x + y*y + z*z); }
    float lenght2D() { return (float) Math.sqrt( x*x + z*z); }
    public void norm() { divide(lenght()); }
    public float dot(MixVector v){ return x*v.x + y*v.y + z*v.z ; }
    public void cross(MixVector u, MixVector v) {
        float x = u.y*v.z - u.z*v.y;
        float y = u.z*v.x - u.x*v.z;
        float z = u.x*v.y - u.y*v.x;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public void prod(Matrix m){
        float xTemp = m.a1 * x + m.a2 * y +m.a3 * z;
        float yTemp = m.b1 * x + m.b2 * y +m.b3 * z;
        float zTemp = m.c1 * x + m.c2 * y +m.c3 * z;
        this.x = xTemp;
        this.y = yTemp;
        this.z = zTemp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloat(x);
        parcel.writeFloat(y);
        parcel.writeFloat(z);
    }
    protected void readParcel(Parcel in) {
        x = in.readFloat();
        y = in.readFloat();
        z = in.readFloat();
    }
}

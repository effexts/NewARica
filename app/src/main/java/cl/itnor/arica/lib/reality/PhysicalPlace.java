package cl.itnor.arica.lib.reality;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import cl.itnor.arica.lib.render.MixVector;

/**
 * Created by effexts on 1/27/17.
 */

public class PhysicalPlace implements Parcelable {
    double latitude;
    double longitude;
    double altitude;

    public PhysicalPlace() {
        this.latitude = 0;
        this.longitude = 0;
        this.altitude = 0;
    }

    public PhysicalPlace(double latitude, double longitude, double altitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    protected PhysicalPlace(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
        altitude = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeDouble(altitude);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PhysicalPlace> CREATOR = new Creator<PhysicalPlace>() {
        @Override
        public PhysicalPlace createFromParcel(Parcel in) {
            return new PhysicalPlace(in);
        }

        @Override
        public PhysicalPlace[] newArray(int size) {
            return new PhysicalPlace[size];
        }
    };

    @Override
    public String toString() {
        return "(lat="+latitude+", lon="+longitude+", alt="+altitude+")";
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public static void calcDestination(double lat1Deg, double lon1Deg, double bearing, double d, PhysicalPlace dest) {
        //see http://en.wikipedia.org/wiki/Great-circle_distance
        double brng = Math.toRadians(bearing);
        double lat1 = Math.toRadians(lat1Deg);
        double lon1 = Math.toRadians(lon1Deg);
        double R = 6371.0 * 1000.0;
        double lat2 = Math.asin(Math.sin(lat1)*Math.cos(d/R) + Math.cos(lat1)*Math.sin(d/R)*Math.cos(brng));
        double lon2 = lon1 + Math.atan2(Math.sin(brng)*Math.sin(d/R)*Math.cos(lat1), Math.cos(d/R)-Math.sin(lat1)*Math.sin(lat2));
        dest.setLatitude(Math.toDegrees(lat2));
        dest.setLongitude(Math.toDegrees(lon2));
    }
    public static void convertLocToVec(Location origin, PhysicalPlace gp, MixVector v) {
        float[] z = new float[1];
        float[] x = new float[1];
        z[0] = 0;
        Location.distanceBetween(origin.getLatitude(), origin.getLongitude(), gp.getLatitude(), gp.getLongitude(), z);
        Location.distanceBetween(origin.getLatitude(), origin.getLongitude(), gp.getLatitude(), gp.getLongitude(), x);
        double y = gp.getAltitude() - origin.getAltitude();
        if (origin.getLatitude() < gp.getLatitude())
            z[0] *= -1;
        if (origin.getLongitude() > gp.getLongitude())
            x[0] *= -1;
        v.set(x[0], (float) y, z[0]);
    }
    public static void convertVecToLoc(MixVector v, Location origin, Location gp) {
        double brngNS = 0, brgnEW = 0;
        if (v.z > 0)
            brngNS = 180;
        if (v.x < 0)
            brgnEW = 270;
        PhysicalPlace tmp1Loc = new PhysicalPlace();
        PhysicalPlace tmp2Loc = new PhysicalPlace();
        PhysicalPlace.calcDestination(origin.getLatitude(), origin.getLongitude(), brngNS, Math.abs(v.z), tmp1Loc);
        PhysicalPlace.calcDestination(tmp1Loc.getLatitude(), tmp1Loc.getLongitude(), brgnEW, Math.abs(v.x), tmp2Loc);
        gp.setLatitude(tmp2Loc.getLatitude());
        gp.setLongitude(tmp2Loc.getLongitude());
        gp.setAltitude(origin.getAltitude() +v.y);
    }

}

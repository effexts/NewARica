package cl.itnor.arica.lib.gui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by effexts on 1/26/17.
 */

public class PaintScreen implements Parcelable {
    Canvas canvas;
    int width, height;
    Paint paint = new Paint();
    Paint bwPaint = new Paint();

    public PaintScreen() {
        paint.setTextSize(16);
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
    }
    public PaintScreen(Parcel in) {
        readFromParcel(in);
        paint.setTextSize(16);
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
    }

    public static final Creator<PaintScreen> CREATOR = new Creator<PaintScreen>() {
        @Override
        public PaintScreen createFromParcel(Parcel parcel) {
            return new PaintScreen( parcel);
        }

        @Override
        public PaintScreen[] newArray(int i) {
            return new PaintScreen[i];
        }
    };

    public Canvas getCanvas() {
        return canvas;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setFill(boolean fill){
        if (fill)
            paint.setStyle(Paint.Style.FILL);
        else paint.setStyle(Paint.Style.STROKE);
    }
    public void setColor(int color) { paint.setColor(color); }
    public void setStrokeWidth(float width) { paint.setStrokeWidth( width ); }
    public void paintLine(float x1, float y1, float x2, float y2) { canvas.drawLine(x1, y1, x2, y2, paint); }
    public void paintRect(float x, float y, float width, float height) {canvas.drawRect(x,y, x+width, y+height, paint); }
    public void paintRoundRect(float x, float y, float width, float height){
        RectF rectf = new RectF(x, y, x+width, y+height);
        canvas.drawRoundRect(rectf, 15f, 15f, paint);
    }
    public void paintPath(Path path, float x, float y, float width, float height, float rotation, float scale){
        canvas.save();
        canvas.translate(x+width, y+height);
        canvas.rotate(rotation);
        canvas.scale(scale, scale);
        canvas.translate(-(width/2), -(height/2));
        canvas.drawPath(path, paint);
        canvas.restore();
    }
    public void paintCircle(float x, float y, float radius) { canvas.drawCircle(x, y, radius, paint); }
    public void paintText(float x, float y, String text, boolean underline){
        paint.setUnderlineText(underline);
        canvas.drawText(text, x, y, paint);
    }
    public void paintObj(ScreenObj obj, float x, float y, float rotation, float scale) {
        canvas.save();
        canvas.translate(x+obj.getWidth()/2, y+obj.getHeight()/2);
        canvas.rotate(rotation);
        canvas.scale(scale, scale);
        canvas.translate(-obj.getWidth()/2, -obj.getHeight()/2);
        obj.paint(this);
        canvas.restore();
    }
    public float getTextWidth(String text) { return paint.measureText(text); }
    public float getTextAscent() { return paint.ascent(); }
    public float getTextDescent() { return paint.descent(); }
    public float getTextLead() { return 0; }
    public void setFontSize(float size) { paint.setTextSize(size); }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(width);
        parcel.writeInt(height);
    }

    public void readFromParcel(Parcel in) {
        this.height = in.readInt();
        this.width = in.readInt();
        canvas = new Canvas();
    }
}

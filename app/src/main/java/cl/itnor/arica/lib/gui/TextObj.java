package cl.itnor.arica.lib.gui;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.BreakIterator;
import java.util.ArrayList;

/**
 * Created by effexts on 1/27/17. n
 */

public class TextObj implements ScreenObj, Parcelable{
    private String txt;
    private float fontSize;
    private float width, height;
    private float areaWidth, areaHeight;
    private String lines[];
    private float lineWidths[];
    private float lineHeight;
    private float maxLineWidth;
    private float pad;
    private int borderColor, bgColor, textColor, textShadowColor;
    private boolean underline;

    public TextObj(String txtInit, float fontSizeInit, float maxWidth, PaintScreen paintScreen, boolean underline) {
        this(txtInit, fontSizeInit, maxWidth, Color.rgb(255,255,255), Color.argb(128,0,0,0), Color.rgb(255,255,255), Color.argb(64,0,0,0), paintScreen.getTextAscent()/2, paintScreen, underline);
    }

    public TextObj(Parcel in) {
        txt = in.readString();
        fontSize = in.readFloat();
        width = in.readFloat();
        height = in.readFloat();
        areaWidth = in.readFloat();
        areaHeight = in.readFloat();
        lines = in.createStringArray();
        lineWidths = in.createFloatArray();
        lineHeight = in.readFloat();
        maxLineWidth = in.readFloat();
        pad = in.readFloat();
        borderColor = in.readInt();
        bgColor = in.readInt();
        textColor = in.readInt();
        textShadowColor = in.readInt();
        underline = in.readByte() != 0;
    }

    public static final Creator<TextObj> CREATOR = new Creator<TextObj>() {
        @Override
        public TextObj createFromParcel(Parcel in) {
            return new TextObj(in);
        }

        @Override
        public TextObj[] newArray(int size) {
            return new TextObj[size];
        }
    };

    public TextObj(String txtInit, float fontSizeInit, float maxWidth, int borderColor, int bgColor, int textColor, int textShadowColor, float pad, PaintScreen paintScreen, boolean underline) {
        this.borderColor = borderColor;
        this.bgColor = bgColor;
        this.textColor = textColor;
        this.textShadowColor = textShadowColor;
        this.pad = pad;
        this.underline = underline;
        try {
            prepTxt(txtInit, fontSizeInit, maxWidth, paintScreen);
        }
        catch (Exception ex) { ex.printStackTrace(); prepTxt("Text Parse Error", 12, 200, paintScreen); }
    }

    private void prepTxt(String txtInit, float fontSizeInit, float maxWidth, PaintScreen paintScreen) {
        paintScreen.setFontSize(fontSizeInit);
        txt = txtInit;
        fontSize = fontSizeInit;
        areaWidth = maxWidth - pad*2;
        lineHeight = paintScreen.getTextAscent() + paintScreen.getTextDescent() + paintScreen.getTextLead();
        ArrayList<String> lineList = new ArrayList<>();
        BreakIterator boundary = BreakIterator.getWordInstance();
        boundary.setText(txt);

        int start = boundary.first();
        int end = boundary.next();
        int prevEnd = start;
        while (end != BreakIterator.DONE) {
            String line = txt.substring(start, end);
            String prevLine = txt.substring(start, prevEnd);
            float lineWidth = paintScreen.getTextWidth(line);

            if (lineWidth > areaWidth) {
                if (prevLine.length() > 0)
                    lineList.add(prevLine);
                start = prevEnd;
            }
            prevEnd = start;
            end = boundary.next();
        }
        String line = txt.substring(start, prevEnd);
        lineList.add(line);
        lines = new String[lineList.size()];
        lineWidths = new float[lineList.size()];
        lineList.toArray(lines);

        maxLineWidth = 0;
        for (int i = 0; i < lines.length; i++) {
            lineWidths[i] = paintScreen.getTextWidth(lines[i]);
            if (maxLineWidth < lineWidths[i])
                maxLineWidth = lineWidths[i];
        }
        areaWidth = maxLineWidth;
        areaHeight = lineHeight * lines.length;
        width = areaWidth + pad * 2;
        height = areaHeight + pad * 2;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(txt);
        parcel.writeFloat(fontSize);
        parcel.writeFloat(width);
        parcel.writeFloat(height);
        parcel.writeFloat(areaWidth);
        parcel.writeFloat(areaHeight);
        parcel.writeStringArray(lines);
        parcel.writeFloatArray(lineWidths);
        parcel.writeFloat(lineHeight);
        parcel.writeFloat(maxLineWidth);
        parcel.writeFloat(pad);
        parcel.writeInt(borderColor);
        parcel.writeInt(bgColor);
        parcel.writeInt(textColor);
        parcel.writeInt(textShadowColor);
        parcel.writeByte((byte) (underline ? 1 : 0));
    }

    @Override
    public void paint(PaintScreen paintScreen) {
        paintScreen.setFontSize(fontSize);
        paintScreen.setFill(true);
        paintScreen.setColor(bgColor);
        paintScreen.paintRect(0,0,width,height);
        paintScreen.setFill(false);
        paintScreen.setColor(borderColor);
        paintScreen.paintRect(0,0,width,height);

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            paintScreen.setFill(true);
            paintScreen.setColor(textColor);
            paintScreen.setStrokeWidth(0);
            paintScreen.paintText(pad, pad+lineHeight*i+paintScreen.getTextAscent(), line, underline);
        }
    }


    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }
}

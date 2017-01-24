package cl.itnor.arica;

import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by effexts on 1/24/17.
 */

public class Compatibility {
    private static Method mDefaultDisplay_getRotation;

    public static int getRotation(final Activity activity) throws IllegalAccessException {
        int result = 1;
        try {
            Display display = ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            Object retObj = mDefaultDisplay_getRotation.invoke(display);
            if (retObj != null)
                result = (int) retObj;
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return result;
    }
}

package cl.itnor.arica;

import android.app.Activity;
import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.hardware.Camera;

import java.util.Iterator;
import java.util.List;

/**
 * Created by effexts on 1/17/17.
 */

public class CameraSurface extends SurfaceView implements SurfaceHolder.Callback {
    SurfaceHolder holder;
    Camera mCamera;
    Activity mActivity;


    public CameraSurface(Context context) {
        super(context);
        try {
            mActivity = (Activity) context;
            holder = getHolder();
            holder.addCallback(this);
            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        catch (Exception ex) {}
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            if(mCamera != null) {
                try {
                    mCamera.stopPreview();
                }
                catch (Exception ignore) {}
                try {
                    mCamera.release();
                }
                catch (Exception ignore) {}
                mCamera = null;
            }
            mCamera = Camera.open();
            mCamera.setPreviewDisplay(holder);
        }
        catch (Exception ex) {
            try {
                if (mCamera != null){
                    try {
                        mCamera.stopPreview();
                    }
                    catch (Exception ignore) {}
                    try {
                        mCamera.release();
                    }
                    catch (Exception ignore) {}
                    mCamera = null;
                }
            }
            catch (Exception ignore) {}
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            List<Camera.Size> supportedSizes = parameters.getSupportedPreviewSizes();

            float ff = (float) width/height;
            float bff=0;
            int bestw=0;
            int besth=0;
            Iterator<Camera.Size> itr = supportedSizes.iterator();

            while (itr.hasNext()) {
                Camera.Size element = itr.next();
                float cff = (float) element.width / element.height;

                if ( (ff - cff <= ff-bff) && (element.width <= width) && (element.width >= bestw)) {
                    bff = cff;
                    bestw = element.width;
                    besth = element.height;
                }
            }
            parameters.setPreviewSize(bestw, besth);
            for (Camera.Size s : supportedSizes) {
                if( (s.height <= height) && (s.width <= width )) {
                    parameters.setPreviewSize(s.width, s.height);
                    break;
                }
            }
            try { parameters.setPreviewSize(bestw, besth); }
            catch (Exception ex) { parameters.setPreviewSize(480,320); }

            mCamera.setParameters(parameters);
            mCamera.startPreview();
        }
        catch (Exception ex) { ex.printStackTrace(); }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        try {
            if (mCamera != null) {
                try {
                    mCamera.stopPreview();
                }
                catch (Exception ignore) {}
                try {
                    mCamera.release();
                }
                catch (Exception ignore) {}
                mCamera = null;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

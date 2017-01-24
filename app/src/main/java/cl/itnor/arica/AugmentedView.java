package cl.itnor.arica;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

/**
 * Created by effexts on 1/19/17.
 */

public class AugmentedView extends View {
    Mixview app;

    public AugmentedView(Context context) {
        super(context);
        try {
            app = (Mixview) context;
            app.killOnError();
        }
        catch (Exception ex) {
            app.doError(ex);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}

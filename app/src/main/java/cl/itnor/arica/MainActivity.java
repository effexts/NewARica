package cl.itnor.arica;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by effexts on 1/17/17.
 */

public class MainActivity extends Activity {
    private Context ctx;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        startActivity(new Intent(ctx, InicioAR.class));
        finish();


    }
}

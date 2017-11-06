package cl.itnor.arica;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import cl.itnor.arica.Permission;



/**
 * Created by effexts on 1/17/17.
 */

public class MainActivity extends Activity {
    private Context ctx;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;

        if (Permission.hasSelfPermissions(this, new String[]{Permission.CAMERA, Permission.ACCESS_FINE_LOCATION, Permission.ACCESS_COARSE_LOCATION}))
            startActivity(new Intent(ctx, MixView.class));
        else {
            Permission.requestAllPermissions(this, new String[]{Permission.CAMERA, Permission.ACCESS_FINE_LOCATION, Permission.ACCESS_COARSE_LOCATION},23);
            startActivity(new Intent(ctx, MixView.class));
        }
        finish();


    }


}

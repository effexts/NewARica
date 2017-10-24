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


/**
 * Created by effexts on 1/17/17.
 */

public class MainActivity extends Activity {
    private Context ctx;
    List<String> permissionsList = new ArrayList<>();
    boolean askOnceAgain = false;
    Activity activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        activity = this;
        checkPermissions();



        startActivity(new Intent(ctx, MixView.class));
        finish();


    }

    @Override
    protected void onResume() {
        super.onResume();
        permissionsList.clear();
        if (askOnceAgain) {
            askOnceAgain = false;
            checkPermissions();
        }
    }

    private void showCustomDialog(String message, DialogInterface.OnClickListener listener) {
        new android.support.v7.app.AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton("Ok", listener)
                .setCancelable(false)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 99: {
                boolean required = false;
                for (int i=0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                        Log.d("Permissions", "Permissions Granted: " + permissions[i]);
                    else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        Log.d("Permissions", "Permission Denied: "+ permissions[i]);
                        required = true;
                    }
                }
                if (required) {
                    showCustomDialog("La aplicación necesita los siguientes permisos para poder funcionar",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                            Uri.fromParts("package", getPackageName(), null));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    askOnceAgain = true;
                                }
                            });
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    private void checkPermissions() {
        int hasLocationPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasStoragePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
        int hasCameraPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        List<String> permissions = new ArrayList<>();
        if (hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (hasStoragePermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA);
        }
        if (!permissions.isEmpty()) {
            ActivityCompat.requestPermissions(activity, permissions.toArray(new String[permissions.size()]), 99);
        }

    }
}

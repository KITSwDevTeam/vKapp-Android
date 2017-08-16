package com.example.admin.vkclub;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.sip.SipAudioCall;
import android.os.Build;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Dash;

/**
 * Created by admin on 7/12/2017.
 */

public class Dailer extends Fragment{
    Dashboard dashboard;
    EditText numEditor;
    Button num1, num2, num3, num4, num5, num6, num7, num8, num9, num0;
    ImageButton backSpace, callBtn;
    boolean permission_activation = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialer, container, false);
        dashboard = (Dashboard) Dashboard.getAppContext();
        findView(view);
        return view;
    }

    private void findView(View view){
        numEditor = (EditText)view.findViewById(R.id.editNum);
        backSpace = (ImageButton)view.findViewById(R.id.btnBackspace);
        callBtn = (ImageButton) view.findViewById(R.id.btnCall);

        backSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = numEditor.getText().toString();
                if(value.length() != 0){
                    numEditor.setText(value.substring(0, value.length() - 1));
                } else{
                    numEditor.setText("");
                }
            }
        });

        backSpace.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                numEditor.setText("");
                return true;
            }
        });

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dashboard.reg_status == 1){
                    call();
                }else if (dashboard.reg_status == 2){
                    sipSessionError();
                }else if (numEditor.getText().toString().length() == 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Call Failure");
                    builder.setMessage("Please enter phone number.");
                    builder.setCancelable(true);

                    builder.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    if (Build.VERSION.SDK_INT >= 23){
                        requestPermissions(new String[]{
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.RECORD_AUDIO
                        }, 100);
                    }
                }
            }
        });

        num0 = (Button)view.findViewById(R.id.btn0); numPad(num0, 0);
        num1 = (Button)view.findViewById(R.id.btn1); numPad(num1, 1);
        num2 = (Button)view.findViewById(R.id.btn2); numPad(num2, 2);
        num3 = (Button)view.findViewById(R.id.btn3); numPad(num3, 3);
        num4 = (Button)view.findViewById(R.id.btn4); numPad(num4, 4);
        num5 = (Button)view.findViewById(R.id.btn5); numPad(num5, 5);
        num6 = (Button)view.findViewById(R.id.btn6); numPad(num6, 6);
        num7 = (Button)view.findViewById(R.id.btn7); numPad(num7, 7);
        num8 = (Button)view.findViewById(R.id.btn8); numPad(num8, 8);
        num9 = (Button)view.findViewById(R.id.btn9); numPad(num9, 9);
    }

    private void numPad(Button btn, final Integer num){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numEditor.append(num.toString());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
        if (requestCode == 100){
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED && grantResults[1] != PackageManager.PERMISSION_GRANTED){
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Note");
                builder.setMessage("You cannot make a call without these permissions.");
                builder.setCancelable(true);

                builder.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();
                System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
            }else {
                System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                Voip.voipActivity.finish();
            }
        }
    }

    private void call(){
        Intent in = new Intent(getContext(), Calling.class);
        in.putExtra("STATE", "DAILING");
        in.putExtra("CALLEE", numEditor.getText().toString());
        startActivity(in);
        dashboard.initiateCall(numEditor.getText().toString());
    }

    private void sipSessionError(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Error");
        builder.setMessage("Either you are not connected to vKirirom network or server is not responding.\nPlease restart the app or contact the support team.\nThank you for using Vkclub.");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void permissionDenied(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Permission denied");
        builder.setMessage("Please allow phone permission in order to make call.");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Allow",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Build.VERSION.SDK_INT >= 23){
                            requestPermissions(new String[]{
                                    Manifest.permission.READ_PHONE_STATE
                            }, 100);
                        }
                    }
                });

        builder.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }
}

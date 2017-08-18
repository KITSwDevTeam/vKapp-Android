package com.example.admin.vkclub;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.admin.vkclub.R.id.pass;

public class UpdatePassword extends DialogFragment {
    Toolbar toolbar;
    private EditText xCurrentpass, xNewpass, xConfirmnewpass;
    private TextView xCurrentpassvalidate, xNewpassvalidate, xConfirmnewpassvalidate;
    private Button Updatepassword;
    SharedPreferences preference;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_update_password, container, false);
        findView(view);
        return view;
    }

    private void findView(View view) {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        xCurrentpass = (EditText) view.findViewById(R.id.currentpass);
        xNewpass = (EditText) view.findViewById(R.id.newpass);
        xConfirmnewpass = (EditText) view.findViewById(R.id.confirmnewpass);
        xCurrentpassvalidate = (TextView) view.findViewById(R.id.currentpassValidation);
        xNewpassvalidate = (TextView) view.findViewById(R.id.newpassValidation);
        xConfirmnewpassvalidate = (TextView) view.findViewById(R.id.confirmnewpassValidation);
        Updatepassword = (Button) view.findViewById(R.id.updatepass);

        toolbar.setNavigationIcon(R.drawable.ic_cancel);
        toolbar.setTitle("Update Password");
        toolbar.setTitleTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        preference = PreferenceManager.getDefaultSharedPreferences(getContext());
        final String previous = preference.getString("pass",null);
        if(!previous.equals(null)){
            Log.d("AAAAAAAAAAAAAAAAAAAAAAAAAAAA  ", previous);
        }

        Updatepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Currentpass = xCurrentpass.getText().toString();
                String Newpass = xNewpass.getText().toString();
                String Confirmnewpass = xConfirmnewpass.getText().toString();
                boolean currentPass, newPass, confirmnewPass;

                if(Currentpass.isEmpty()){
                    xCurrentpassvalidate.setText("Please enter your current password");
                    currentPass = false;
                }else if(!Currentpass.equals(previous)){
                    Log.i("111111111111111111111111",Currentpass);
                    Log.i("222222222222222222222222",previous);
                    xCurrentpassvalidate.setText("Incorrect Password");
                    currentPass = false;
                }else {
                    xCurrentpassvalidate.setText("");
                    currentPass = true;
                }
                if(Newpass.isEmpty() || Newpass.length() < 6){
                    xNewpassvalidate.setText("Please provide at least 6 characters.");
                    newPass = false;
                }else {
                    xNewpassvalidate.setText("");
                    newPass = true;
                }if(Confirmnewpass.isEmpty()){
                    xConfirmnewpassvalidate.setText("Please provide at least 6 characters.");
                    confirmnewPass = false;
                }else if (!Confirmnewpass.equals(Newpass)) {
                    xConfirmnewpassvalidate.setText("Password does not match!");
                    confirmnewPass = false;
                } else {
                    xConfirmnewpassvalidate.setText("");
                    confirmnewPass = true;
                }

                if(currentPass && newPass && confirmnewPass){
                    updatepassword(Currentpass,Confirmnewpass);
                }
            }
        });
    }

    private void updatepassword(final String getCurrentpass, final String getConfirmpass){
        user = mAuth.getCurrentUser();

        final AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(),getCurrentpass);
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            String newPassword = getConfirmpass;

                            user.updatePassword(newPassword)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Intent intent = new Intent(getContext(),Dashboard.class);
                                                startActivity(intent);
                                                Log.d("Password Update Done.",task.toString());
                                                dismiss();
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("Fail",e.toString());
                                }
                            });
                        }
                        else{
                            if(task.getException() instanceof FirebaseNetworkException){
                                presentDialog("Login fail!", "No Internet connection.");
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("44444444444444444444444444444444",e.toString());
                    }
                });
    }

    public void presentDialog(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);
        builder.setMessage(msg);
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

}

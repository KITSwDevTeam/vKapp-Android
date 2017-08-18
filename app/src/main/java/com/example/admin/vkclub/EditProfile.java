package com.example.admin.vkclub;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import static com.example.admin.vkclub.Calling.context;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.security.PrivateKey;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;
import static com.example.admin.vkclub.Calling.getInstance;
import static com.example.admin.vkclub.R.id.email;
import static com.example.admin.vkclub.R.id.pass;
import static com.example.admin.vkclub.R.id.start;
import static com.facebook.FacebookSdk.getApplicationContext;

public class EditProfile extends DialogFragment {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DialogInterface.OnDismissListener onDismissListener;

    Toolbar toolbar;
    private EditText mName,mEmail,mConfirmpassword,mCurrentpass;
    private TextView mNamevalidation,mEmailvalidation,mConfirmpassvalidation,mUpdatepass;
    private Button Updateprofile;
    FirebaseUser user;
    SharedPreferences preference;
    SharedPreferences.Editor editor;
    private static boolean flag;
    Dashboard dh;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_edit_profile, container, false);
        dh = (Dashboard) Dashboard.getAppContext();
        findView(view);
        return view;
    }

    private void findView(View view) {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mName = (EditText) view.findViewById(R.id.nameprofile);
        mEmail = (EditText) view.findViewById(R.id.email1);
        mConfirmpassword = (EditText) view.findViewById(R.id.confirmpass1);
        Updateprofile = (Button) view.findViewById(R.id.updateprofile);
        mCurrentpass = (EditText) view.findViewById(pass);
        mUpdatepass = (TextView) view.findViewById(R.id.updatepass);

        mNamevalidation = (TextView) view.findViewById(R.id.nameValidation1);
        mEmailvalidation = (TextView) view.findViewById(R.id.emailValidation1);
        mConfirmpassvalidation = (TextView) view.findViewById(R.id.confirmpassValidation);

        //get name and email info of user from firebase


        user = mAuth.getCurrentUser();
        mName.setText(user.getDisplayName());
        mEmail.setText(user.getEmail());

//        DatabaseReference myRef = database.getReference("message");

        preference = PreferenceManager.getDefaultSharedPreferences(getContext());
        final String currentpass = preference.getString("pass",null);
        if(!currentpass.equals(null)){
            Log.d("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH ", currentpass);
        }

        TextWatcher editTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mName.getText().hashCode() == s.hashCode()){
                    if (s.length() == 0){
                        mNamevalidation.setText("Please enter your name.");
                    }else if (!s.toString().matches("[a-zA-Z.? ]*")){
                        mNamevalidation.setText("Special characters not allowed.");
                    }else if (s.length() == 20){
                        mNamevalidation.setText("Allow only 20 characters.");
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        mNamevalidation.setText("");
                                    }
                                }, 3000);
                    }else {
                        mNamevalidation.setText("");
                    }
                }else if (mEmail.getText().hashCode() == s.hashCode()){
                    if (s.toString().indexOf("@") <= 0){
                        mEmailvalidation.setText("Please enter a valid email address.");
                    }else if (s.length() == 0){
                        mEmailvalidation.setText("Please provide your email address.");
                    }else {
                        mEmailvalidation.setText("");
                    }
                }else {
                    if (!s.toString().equals(currentpass)){
                        mConfirmpassvalidation.setText("Password does not match.");
                    }else {
                        mConfirmpassvalidation.setText("");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        mName.addTextChangedListener(editTextWatcher);
        mEmail.addTextChangedListener(editTextWatcher);
        mConfirmpassword.addTextChangedListener(editTextWatcher);

        Updateprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameValue = mName.getText().toString();
                final String emailValue = mEmail.getText().toString();
                String confirmpassValue = mConfirmpassword.getText().toString();
                boolean nameStatus, emailStatus, confirmpassStatus;

                if (!nameValue.matches("[a-zA-Z.? ]*")){
                    mNamevalidation.setText("Special characters not allowed.");
                    nameStatus = false;
                }else if (nameValue.length() == 0){
                    mNamevalidation.setText("Please enter a valid name.");
                    nameStatus = false;
                }else {
                    mNamevalidation.setText("");
                    nameStatus = true;
                }

                if (emailValue.indexOf("@") <= 0) {
                    mEmailvalidation.setText("Please enter a valid email address.");
                    emailStatus = false;
                }else if (emailValue.length() == 0){
                    mEmailvalidation.setText("Please provide your email address.");
                    emailStatus = false;
                } else {
                    mEmailvalidation.setText("");
                    emailStatus = true;
                }

                if (!confirmpassValue.equals(currentpass)) {
                    mConfirmpassvalidation.setText("Passwword does not match!");
                    confirmpassStatus = false;
                } else {
                    mConfirmpassvalidation.setText("");
                    confirmpassStatus = true;
                }

                if(nameStatus && emailStatus && confirmpassStatus){
                   updateaccount(nameValue, emailValue, confirmpassValue);
                }
            }
        });

        toolbar.setNavigationIcon(R.drawable.ic_cancel);
        toolbar.setTitle("Edit Profile");
        toolbar.setTitleTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                dh.mDrawerLayout.openDrawer(Gravity.LEFT, false);
            }
        });

        mUpdatepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }


    private void updateaccount(final String getname, final String getemail, final String getconfirmpass){
        user = mAuth.getCurrentUser();

        final AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(),getconfirmpass);
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            user = mAuth.getCurrentUser();
                            if (user.getEmail().equals(getemail)){
                                user = mAuth.getCurrentUser();
                                UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(getname)
                                        .build();
                                user.updateProfile(profileUpdate);
                                Toast.makeText(getContext(),"Done." ,Toast.LENGTH_LONG).show();
                                Intent intent  = new Intent(getContext(),LoginActivity.class);
                                startActivity(intent);
                                dismiss();

                            }else{
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                                alertDialog.setTitle("Confirm your updating.");
                                alertDialog.setMessage("Please verify your email address! Check email sent to " +getemail+ " for verification link.\nThank you for using Vkclub.");
                                AlertDialog.Builder confirm = alertDialog.setPositiveButton("Confirm",
                                        new DialogInterface.OnClickListener(){
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                if(!getemail.equals(user.getEmail())){
                                                    user = mAuth.getCurrentUser();
                                                    user.updateEmail(getemail)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Log.d("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF",user.getEmail());

                                                                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if(task.isSuccessful()){
                                                                                    Toast.makeText(getContext(), "Sent email done.",Toast.LENGTH_SHORT).show();

                                                                                }else {
                                                                                    if(task.getException() instanceof FirebaseNetworkException){
                                                                                        presentDialog("No Internet Connection","Problem with your network access");
                                                                                    }
                                                                                }
                                                                            }
                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Log.d("WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW",e.toString());
                                                                            }
                                                                        });

                                                                        mAuth.getInstance().signOut();
                                                                        LoginManager.getInstance().logOut();
                                                                        Intent intent = new Intent(getContext(), LoginActivity.class);
                                                                        startActivity(intent);
                                                                    }
                                                                    else{
                                                                        if(task.getException() instanceof FirebaseNetworkException){
                                                                            Toast.makeText(getContext(),"No Internet Connection .", Toast.LENGTH_LONG).show();
                                                                        }else if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                                                            Toast.makeText(getContext(),"This Account is already used.", Toast.LENGTH_LONG).show();
                                                                        }else if(task.getException() instanceof FirebaseAuthRecentLoginRequiredException){
                                                                            Toast.makeText(getContext(),"Require Login.", Toast.LENGTH_LONG).show();
                                                                        }
                                                                    }
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.d("VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV",e.toString());
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                alertDialog.setNegativeButton("Cancel",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Write your code here to execute after dialog
                                                Toast.makeText(getApplicationContext(), "You clicked on Cancel", Toast.LENGTH_SHORT).show();
                                                dialog.cancel();
                                            }
                                        });
                                // Showing Alert Message
                                alertDialog.show();
                            }
                        }else{
                            if(task.getException() instanceof FirebaseNetworkException) {
                                presentDialog("Login fail!", "No Internet connection.");
                            }else if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                presentDialog("Can not find Credential.","");
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("EEEEEEEEEEEEEEEEEEEEEEEEEEEEE", e.toString());
            }
        });
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
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

    public void showDialog() {
        UpdatePassword newFragment = new UpdatePassword();
        FragmentManager fragmentManager = getFragmentManager();
        if ((getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) ==
                Configuration.SCREENLAYOUT_SIZE_LARGE) {
            // The device is using a large layout, so show the fragment as a dialog
            newFragment.show(fragmentManager, "dialog");
        } else {
            // The device is smaller, so show the fragment fullscreen
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            // For a little polish, specify a transition animation
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            // To make it fullscreen, use the 'content' root view as the container
            // for the fragment, which is always the root view for the activity
            transaction.add(R.id.drawerLayout, newFragment)
                    .addToBackStack(null).commit();
        }
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }

}

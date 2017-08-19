package com.example.admin.vkclub;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import static com.facebook.FacebookSdk.getApplicationContext;

public class EditProfile extends DialogFragment {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Toolbar toolbar;
    FirebaseUser user;
    SharedPreferences preference;
    private DialogInterface.OnDismissListener onDismissListener;
    private EditText mName,mEmail,mConfirmpassword;
    private TextView mNamevalidation,mEmailvalidation,mConfirmpassvalidation,mUpdatepass;
    private Button Updateprofile;
    private static boolean nameStatus, emailStatus, confirmpassStatus;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_edit_profile, container, false);
        findView(view);
        return view;
    }
    private void findView(View view) {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mName = (EditText) view.findViewById(R.id.nameprofile);
        mEmail = (EditText) view.findViewById(R.id.email1);
        mConfirmpassword = (EditText) view.findViewById(R.id.confirmpass1);
        Updateprofile = (Button) view.findViewById(R.id.updateprofile);
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
            Log.d("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH  ", currentpass);
        }

        Updateprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameValue = mName.getText().toString();
                final String emailValue = mEmail.getText().toString();
                String confirmpassValue = mConfirmpassword.getText().toString();

                for (int i=0; i<nameValue.length(); i++){
                    if (!((nameValue.charAt(i) > 64 && nameValue.charAt(i) < 91) ||
                            (nameValue.charAt(i) > 96 && nameValue.charAt(i) < 123) || nameValue.charAt(i) == 32)){
                        mNamevalidation.setText("Special characters not allowed.");
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        mNamevalidation.setText("");
                                    }
                                }, 4000);
                        nameStatus = false;
                    }else {
                        nameStatus = true;
                    }
                }

                if(nameValue.isEmpty()){
                    mNamevalidation.setText("Please enter your name.");
                    nameStatus = false;
                }else {
                    mNamevalidation.setText("");
                    nameStatus = true;
                }
                if ((emailValue.indexOf("@") <= 0) || !emailValue.contains(".com") || emailValue.isEmpty()) {
                    mEmailvalidation.setText("Please enter a valid email address.");
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

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }

}

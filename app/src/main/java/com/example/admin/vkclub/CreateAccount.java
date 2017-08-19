package com.example.admin.vkclub;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.regex.Pattern;

import static com.example.admin.vkclub.R.id.emailValidation;
import static com.example.admin.vkclub.R.id.nameValidation;

public class CreateAccount extends AppCompatActivity {
    // Declare Firebase auth
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    // Declare Buttons
    Button signUp, backBtn;

    // Declare EditText
    EditText name, email, pass, confirmPass;

    // Declare TextView
    TextView nameValidate, emailValidate, passValidate, confirmpassValidate, statusText;

    private ProgressBar spinner;
    private static boolean nameStatus, emailStatus, passwordStatus, confirmpassStatus;

    static boolean flag;
    private static ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        if(Build.VERSION.SDK_INT >= 21){
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorStatusBar));
        }

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);

        // Instantiate Buttons
        signUp = (Button)findViewById(R.id.signup);
        backBtn = (Button)findViewById(R.id.backBtn);

        // Instantiate EditText
        name = (EditText)findViewById(R.id.name);
        email = (EditText)findViewById(R.id.email);
        pass = (EditText)findViewById(R.id.pass);
        confirmPass = (EditText)findViewById(R.id.confirmpass);

        // Instantiate TextView
        nameValidate = (TextView)findViewById(R.id.nameValidation);
        emailValidate = (TextView)findViewById(R.id.emailValidation);
        passValidate = (TextView)findViewById(R.id.passValidation);
        confirmpassValidate = (TextView)findViewById(R.id.confirmpassValidation);
        statusText = (TextView)findViewById(R.id.status);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextWatcher editTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (name.getText().hashCode() == s.hashCode()){
                    if (s.length() == 0){
                        nameValidate.setText("Please enter your name.");
                    }else if (!s.toString().matches("[a-zA-Z.? ]*")){
                        nameValidate.setText("Special characters not allowed.");
                    }else if (s.length() >= 30){
                        nameValidate.setText("Allow only 30 characters.");
                    }else {
                        nameValidate.setText("");
                    }
                }else if (email.getText().hashCode() == s.hashCode()){
                    if (s.length() == 0){
                        emailValidate.setText("Please provide your email address.");
                    }else if (!isValidEmaillId(s.toString())){
                        emailValidate.setText("Please enter a valid email address.");
                    }else {
                        emailValidate.setText("");
                    }
                }else if (pass.getText().hashCode() == s.hashCode()){
                    if ((s.length() != 0) && s.length() < 6){
                        passValidate.setText("Please provide at least 6 characters.");
                    }else if (s.length() == 0){
                        passValidate.setText("Please provide your password.");
                    }else {
                        passValidate.setText("");
                    }
                }else {
                    if (!s.toString().equals(pass.getText().toString())){
                        confirmpassValidate.setText("Password does not match.");
                    }else {
                        confirmpassValidate.setText("");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        name.addTextChangedListener(editTextWatcher);
        email.addTextChangedListener(editTextWatcher);
        pass.addTextChangedListener(editTextWatcher);
        confirmPass.addTextChangedListener(editTextWatcher);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameValue = name.getText().toString().trim();
                String emailValue = email.getText().toString().trim();
                String passwordValue = pass.getText().toString().trim();
                String confirmpassValue = confirmPass.getText().toString().trim();

                if (nameValue.length() == 0){
                    nameValidate.setText("Please enter a valid name.");
                    nameStatus = false;
                }else if (!nameValue.matches("[a-zA-Z.? ]*")){
                    nameValidate.setText("Special characters not allowed.");
                    nameStatus = false;
                }else if (nameValue.length() >= 30){
                    nameValidate.setText("Allow only 30 characters.");
                    nameStatus = false;
                }else {
                    nameValidate.setText("");
                    nameStatus = true;
                }

                if (emailValue.length() == 0) {
                    emailValidate.setText("Please provide your email address.");
                    emailStatus = false;
                }else if (!isValidEmaillId(emailValue)){
                    emailValidate.setText("Please enter a valid email address.");
                    emailStatus = false;
                } else {
                    emailValidate.setText("");
                    emailStatus = true;
                }

                if (passwordValue.length() < 6) {
                    passValidate.setText("Please provide at least 6 characters.");
                    passwordStatus = false;
                }else {
                    passValidate.setText("");
                    passwordStatus = true;
                }

                if (confirmpassValue.length() < 6) {
                    confirmpassValidate.setText("Please provide at least 6 characters.");
                    confirmpassStatus = false;
                }else if (!confirmpassValue.equals(passwordValue)) {
                    confirmpassValidate.setText("Password does not match!");
                    confirmpassStatus = false;
                }else {
                    confirmpassValidate.setText("");
                    confirmpassStatus = true;
                }

                if (nameStatus && emailStatus && passwordStatus && confirmpassStatus) {
                    createAccount(emailValue, passwordValue,nameValue);
                }
            }
        });
    }

    private void createAccount(final String successEmail, final String successpassword,final String nameValue) {
        showProgressDialog("Processing...");
        mAuth.createUserWithEmailAndPassword(successEmail,successpassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                            .setDisplayName(nameValue)
                            .build();
                    user.updateProfile(profileUpdate);
                    sendEmailVerification(user);
                    // create account success
                } else {
                    dismissProgressDialog();
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        // thrown if there already exists an account with the given email address
                        presentDialog("SignUp Failed..", "Account is already exists with the given email address.");
                        email.requestFocus();
                    } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        // thrown if the email address is malformed
                        presentDialog("SignUp Failed..", "The email address is malformed.");
                        email.requestFocus();
                    } else if (task.getException() instanceof FirebaseAuthWeakPasswordException) {
                        // thrown if the password is not strong enough
                        presentDialog("SignUp Failed..", "The password is not strong enough.");
                        pass.requestFocus();
                    } else {
                        try {
                            task.getException();
                        } catch (Exception e) {
                            presentDialog("SignUp Failed..", e.getMessage());
                        }
                    }
                }
            }
        });
    }

    //sent email to user verified
    private void sendEmailVerification(FirebaseUser user){
        dismissProgressDialog();
        showProgressDialog("Sending verification email...");
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    afterSignUpDialog("Success", "Please check your email, we have sent a confirmation message.\nThank you for using Vkclub.");
                }else {
                    spinner.setVisibility(View.GONE);
                    statusText.setText("");
                    presentDialog("Error Occur", "Please try again!\nThank you for using Vkclub.");
                }
            }
        });
    }

    private void afterSignUpDialog(String title, String msg){
        dismissProgressDialog();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(true);

        builder.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent in = new Intent(CreateAccount.this, LoginActivity.class);
                        startActivity(in);
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void presentDialog(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    private void showProgressDialog(String message){
        progressDialog = new ProgressDialog(CreateAccount.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void dismissProgressDialog(){
        progressDialog.dismiss();
    }

    private boolean isValidEmaillId(String email){
        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }
}
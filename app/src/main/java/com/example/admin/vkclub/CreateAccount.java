package com.example.admin.vkclub;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameValue = name.getText().toString();
                String emailValue = email.getText().toString();
                String passwordValue = pass.getText().toString();
                String confirmpassValue = confirmPass.getText().toString();

                boolean nameStatus, emailStatus, passwordStatus, confirmpassStatus;

                if (nameValue.isEmpty()) {
                    nameValidate.setText("Please enter your name.");
                    nameStatus = false;
                } else {
                    nameValidate.setText("");
                    nameStatus = true;
                }

                if ((emailValue.indexOf("@") <= 0) || !emailValue.contains(".com") || emailValue.isEmpty()) {
                    emailValidate.setText("Please enter a valid email address.");
                    emailStatus = false;
                } else {
                    emailValidate.setText("");
                    emailStatus = true;
                }

                if (passwordValue.isEmpty() || passwordValue.length() < 6) {
                    passValidate.setText("Please provide at least 6 characters.");
                    passwordStatus = false;
                } else {
                    passValidate.setText("");
                    passwordStatus = true;
                }

                if (confirmpassValue.isEmpty() || confirmpassValue.length() < 6) {
                    confirmpassValidate.setText("Please provide at least 6 characters.");
                    confirmpassStatus = false;
                } else if (!confirmpassValue.equals(passwordValue)) {
                    confirmpassValidate.setText("Passwword does not match!");
                    confirmpassStatus = false;
                } else {
                    confirmpassValidate.setText("");
                    confirmpassStatus = true;
                }

                if (nameStatus && emailStatus && passwordStatus && confirmpassStatus) {
                    createAccount(emailValue, passwordValue);
                }
            }
        });
    }

    private void createAccount(final String email, final String password) {
        spinner.setVisibility(View.VISIBLE);
        statusText.setText("Processing...");
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    sendEmailVerification(user);
                    // create account success
                    // log user in to the dashboard
                    //login(email, password);

                } else {
                    spinner.setVisibility(View.GONE);
                    statusText.setText("");
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        // thrown if there already exists an account with the given email address
                        presentDialog("SignUp Failed..", "Account is already exists with the given email address.");
                    } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        // thrown if the email address is malformed
                        presentDialog("SignUp Failed..", "The email address is malformed.");
                    } else if (task.getException() instanceof FirebaseAuthWeakPasswordException) {
                        // thrown if the password is not strong enough
                        presentDialog("SignUp Failed..", "The password is not strong enough.");
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

    private void login(String email, String password) {
        statusText.setText("Logging in... ");
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // login successful...redirect user to the dashboard
                    Intent intent = new Intent(CreateAccount.this, Dashboard.class);
                    startActivity(intent);
                    finish();
                } else {
                    // handle error
                    spinner.setVisibility(View.GONE);
                    statusText.setText("");
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        // Invalid password
                        presentDialog("Login Failed..", "Invalid Password");
                    } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                        // Invalid Email id
                        presentDialog("Login Failed..", "Invalid Email");
                    } else if (task.getException() instanceof FirebaseNetworkException) {
                        // No internet Connection
                        presentDialog("Login Failed..", "No network coverage");
                    } else {
                        try {
                            throw task.getException();
                        } catch (Exception e) {
                            presentDialog("Login Failed..", e.getMessage());
                        }
                    }
                }
            }
        });
    }

    private void sendEmailVerification(FirebaseUser user){
        statusText.setText("Sending verification email... ");
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
}

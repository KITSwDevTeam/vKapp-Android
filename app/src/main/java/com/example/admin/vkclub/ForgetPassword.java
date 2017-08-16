package com.example.admin.vkclub;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity {

    // Declare Firebase Auth
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    // Declare Button
    Button submitBtn, backBtn;

    // Declare EditText
    EditText email;

    // Declare TextView
    TextView emailValidate;

    TextWatcher emailWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        submitBtn = (Button)findViewById(R.id.submit);
        backBtn = (Button)findViewById(R.id.backBtn);
        email = (EditText)findViewById(R.id.email);
        emailValidate = (TextView)findViewById(R.id.emailValidation);

        if(Build.VERSION.SDK_INT >= 21){
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorStatusBar));
        }

        emailWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().indexOf("@") <= 0){
                    emailValidate.setText("Please enter a valid email address.");
                }else if (s.length() == 0){
                    emailValidate.setText("Please provide your email address.");
                }else {
                    emailValidate.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        email.addTextChangedListener(emailWatcher);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailValue = email.getText().toString();
                boolean emailStatus;

                if ((emailValue.indexOf("@") <= 0)) {
                    emailValidate.setText("Please enter a valid email address.");
                    emailStatus = false;
                }else if (emailValue.length() == 0){
                    emailValidate.setText("Please enter your email address.");
                    emailStatus = false;
                } else {
                    emailValidate.setText("");
                    emailStatus = true;
                }

                if (emailStatus) {
                    forgotpassword(emailValue);
                }
            }
        });
    }

    private void forgotpassword(String emailAddress) {
        mAuth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    afterForgetPasswordDialog("Send Success", "Please check your email address for the confirmation link.");
                } else {
                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        presentDialog("Send Failed..", e.getMessage());
                    }
                }
            }
        });
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

    private void afterForgetPasswordDialog(String title, String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(true);

        builder.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(ForgetPassword.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }
}

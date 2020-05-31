package com.example.whosherejava;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmailET, loginPasswordET;

    private FirebaseAuth authenticator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authenticator = FirebaseAuth.getInstance();

        loginEmailET = findViewById(R.id.loginEmailET);
        loginPasswordET = findViewById(R.id.loginPasswordET);

        Button loginSignUpButt = findViewById(R.id.loginSignUpButt);
        Button loginLoginButt = findViewById(R.id.loginLoginButt);

        loginSignUpButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String emailString= loginEmailET.getText().toString();
//                String passwordString = loginPasswordET.getText().toString();
//                signUp(emailString,passwordString);
                goToSignUp();
            }
        });
        loginLoginButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailString= loginEmailET.getText().toString();
                String passwordString = loginPasswordET.getText().toString();
                login(emailString,passwordString);

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void goToSignUp(){
        Intent intent = new Intent(this, SignUpActivity.class);
        finish();
        startActivityForResult(intent,10);

    }


    public void login(String email, String pw){
        authenticator.signInWithEmailAndPassword(email,pw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d("loginUser?", "signInUserWithEmail: success");
                            FirebaseUser user = authenticator.getCurrentUser();
                            toTheMain();
                        }
                        else{
                            Log.d("loginUser?", "loginUserWithEmail:Failed", task.getException());
                            Toast.makeText(LoginActivity.this,"FAILURE",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void toTheMain(){
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivityForResult(intent,10);

    }
}


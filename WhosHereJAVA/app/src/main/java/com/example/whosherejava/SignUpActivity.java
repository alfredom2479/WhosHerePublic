package com.example.whosherejava;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.auth.UserProfileChangeRequest;

import org.w3c.dom.Text;

public class SignUpActivity extends AppCompatActivity {

    private EditText signUpEmailET, signUpUsernameET, signUpPasswordET;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth =FirebaseAuth.getInstance();

        signUpEmailET = findViewById(R.id.signUpEmailET);
        signUpUsernameET= findViewById(R.id.signUpUsernameET);
        signUpPasswordET= findViewById(R.id.signUpPasswordET);

        Button signUpSignUpButt = findViewById(R.id.signUpSignUpButt);
        Button signUpLoginButt = findViewById(R.id.signUpLoginButt);

        signUpSignUpButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email= (signUpEmailET.getText().toString()).trim();
                String username = (signUpUsernameET.getText().toString()).trim();
                String password = (signUpPasswordET.getText().toString()).trim();
                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)){
                    signUp(email, username, password);
                }
                else{
                    Toast.makeText(SignUpActivity.this, "Missing Fields", Toast.LENGTH_LONG).show();
                }

            }
        });
        signUpLoginButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLogin();
            }
        });

    }

    public void signUp(String email, final String username, String pw){

        firebaseAuth.createUserWithEmailAndPassword(email,pw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d("createUser?", "createUserWithEmail: success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            UserProfileChangeRequest.Builder changeRequest= new UserProfileChangeRequest.Builder().setDisplayName(username);
                            user.updateProfile(changeRequest.build());
                            toTheMain();
                        }
                        else{
                            Log.d("createUser?", "createUserWithEmail:Failed", task.getException());
                            Toast.makeText(SignUpActivity.this,"FAILURE",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void goToLogin(){
        Intent intent= new Intent(this, LoginActivity.class);
        finish();
        startActivityForResult(intent, 10);
    }

    public void toTheMain(){
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivityForResult(intent,10);
    }

}

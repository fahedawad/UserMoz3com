package com.example.usermoz3com;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Verify_phone extends AppCompatActivity {
    EditText editText ;
    Button button;
    private  String verify,name,phonNumber;
    private  FirebaseAuth mauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);
         mauth =FirebaseAuth.getInstance();
         phonNumber =getIntent().getStringExtra("num");
        System.out.println(phonNumber);
         name =getIntent().getStringExtra("name");
        editText =findViewById(R.id.verify);
        button =findViewById(R.id.ok);
        sendVerificationCode(phonNumber);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String code =editText.getText().toString();
               if (code.isEmpty() || code.length()<6){
                   editText.setError("ادخل الرقم");
                   editText.requestFocus();
                   return;
               }
                verifyCode(code);
            }
        });
    }
    private  void sendVerificationCode( String number){
        System.out.println("sdsdsdsdsd"+"       fahed");
        PhoneAuthProvider .getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );

    }
    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verify,code);
        System.out.println("dsadasd");
        sinInWithCredential(credential);
    }

    private void sinInWithCredential(PhoneAuthCredential credential) {
        mauth.signInWithCredential(credential).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        System.out.println("dsadasd");
                            if (task.isSuccessful()){
                                String id =FirebaseAuth.getInstance().getCurrentUser().getUid();
                                HashMap<String,Object>hashMap =new HashMap<>();
                                hashMap.put("name",name);
                                hashMap.put("id",id);
                                hashMap.put("phon",phonNumber);
                                hashMap.put("رصيد السابق",0.0);
                                FirebaseDatabase.getInstance().getReference("user").child(id).setValue(hashMap);
                                Intent intent =new Intent(Verify_phone.this,ListScreen.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }else {
                                System.out.println(task.getException().getMessage());
                                System.out.println("erroe");
                            }
                    }
                });
    }


    private  PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verify =s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                String code = phoneAuthCredential.getSmsCode();
                if (code != null){
                    verifyCode(code);
                }
        }
        @Override
        public void onVerificationFailed(FirebaseException e) {
            System.out.println(e.getMessage());
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() !=null){
            Intent intent =new Intent(Verify_phone.this,ListScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}

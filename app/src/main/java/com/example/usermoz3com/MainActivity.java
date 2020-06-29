package com.example.usermoz3com;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
EditText editText,nametxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText =findViewById(R.id.phon);
        nametxt =findViewById(R.id.nameuser);
        findViewById(R.id.pushdata).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = "+962";
                String number = editText.getText().toString();
                if (number.isEmpty() || number.length()<9){
                    editText.setError("أدخل رقم هاتفك");
                    return;
                }
                String phonNumber = code + number;
                String name =nametxt.getText().toString();
                Intent intent =new Intent(MainActivity.this,Verify_phone.class);
                intent.putExtra("num",phonNumber);
                intent.putExtra("name",name);
                startActivity(intent);
            }
        });
                
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() !=null){
            Intent intent =new Intent(MainActivity.this,ListScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}

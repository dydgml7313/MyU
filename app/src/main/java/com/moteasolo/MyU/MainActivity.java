package com.moteasolo.MyU;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.moteasolo.MyU.ui.StartChatActivity;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab_chat;

    private TextView vEmail, vName, vMyuid;
    private EditText input_MyUID;
    private Button join;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 바인딩 및 생성

        vEmail = findViewById(R.id.View_email);
        vName = findViewById(R.id.View_name);
        vMyuid = findViewById(R.id.View_MyUID);
        fab_chat = findViewById(R.id.fabChat);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            // 사용자의 이메일과 이름을 가져와서 TextView에 설정
            databaseRef.child("users").child(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.exists()) {
                        String email = dataSnapshot.child("email").getValue(String.class);
                        String name = dataSnapshot.child("name").getValue(String.class);

                        vEmail.setText("email: " + email);
                        vName.setText("name: " + name);
                    }
                } else {
                    Toast.makeText(this, "데이터 가져오기 실패", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show();
        }



        fab_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StartChatActivity.class);
                startActivity(intent);
            }
        });

    }
}
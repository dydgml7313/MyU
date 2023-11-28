package com.moteasolo.MyU;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
    private TextInputEditText mEmail, mPassword;
    private Button loginBtn;
    private ProgressBar loadingPB;
    private TextView registerTxt;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 바인딩
        mEmail = findViewById(R.id.editUsername);
        mPassword = findViewById(R.id.editPassword);
        loginBtn = findViewById(R.id.idBtnLogin);
        loadingPB = findViewById(R.id.idPBLoading);
        registerTxt = findViewById(R.id.idTVRegister);
        mAuth = FirebaseAuth.getInstance();

        // 가입 버튼 클릭
        registerTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // 로그인 버튼 클릭
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingPB.setVisibility(View.VISIBLE);
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                // h
                if (!EmailValidator.isValidEmail(email)){
                    Toast.makeText(LoginActivity.this, "이메일 형식이 맞지 않습니다. 다시 입력해주세요", Toast.LENGTH_SHORT).show();
                    loadingPB.setVisibility(View.GONE);
                    return;
                } else if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "아이디 또는 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                    loadingPB.setVisibility(View.GONE); // loadingPB 뷰 off
                    return;
                } else {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        loadingPB.setVisibility(View.GONE);
                                        Toast.makeText(LoginActivity.this, "로그인 완료", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        loadingPB.setVisibility(View.GONE);
                                        Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    /*@Override
    protected void onStart() {
        super.onStart();

        // 자동 로그인 구현
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            //Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            this.finish();
        }
    }*/
}
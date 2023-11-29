package com.moteasolo.MyU.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.moteasolo.MyU.utils.EmailValidator;
import com.moteasolo.MyU.R;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText mEmail, mPassword, mCnfPwd, mName;
    private Button registerBtn;
    private TextView loginTxt;
    private ProgressBar loadingPB;
    private FirebaseAuth nAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 바인딩 및 생성
        mEmail = findViewById(R.id.editEmail);
        mPassword = findViewById(R.id.editPassword);
        mCnfPwd = findViewById(R.id.edit_Checked_Password);
        mName = findViewById(R.id.edit_name);
        registerBtn = findViewById(R.id.idBtnRegister);
        loadingPB = findViewById(R.id.idPBLoading);
        loginTxt = findViewById(R.id.idTVLogin);

        nAuth = FirebaseAuth.getInstance();

        loginTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingPB.setVisibility(View.VISIBLE);
                final String email = String.valueOf(mEmail.getText()).trim().replace("\\s","");
                final String name = String.valueOf(mName.getText()).trim().replace("\\s","");
                String password = String.valueOf(mPassword.getText()).trim().replace("\\s","");
                String cnfPwd = String.valueOf(mCnfPwd.getText()).trim().replace("\\s","");
                if (!password.equals(cnfPwd)){
                    Toast.makeText(RegisterActivity.this, "비밀번호가 일치하지 않습니다. 다시 입력해주세요", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "이메일 또는 비밀번호를 입력하지 않았습니다. 다시 입력해주세요", Toast.LENGTH_SHORT).show();
                    loadingPB.setVisibility(View.GONE);
                } else if (!EmailValidator.isValidEmail(email)) {
                    Toast.makeText(RegisterActivity.this, "이메일 형식이 맞지 않습니다. 다시 입력해주세요", Toast.LENGTH_SHORT).show();
                    loadingPB.setVisibility(View.GONE);
                } else {
                    nAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                loadingPB.setVisibility(View.GONE);

                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                saveUserInfoDatabase(user, email, name);

                                Toast.makeText(RegisterActivity.this, "가입이 완료되었습니다", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                loadingPB.setVisibility(View.GONE);
                                Toast.makeText(RegisterActivity.this,"가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });

    }

    private void saveUserInfoDatabase(FirebaseUser user, String email, String name) {
        if (user != null) {
            String uid = user.getUid();

            // FRD 레퍼런스 설정
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference userRef = database.getReference("users");

            // UserInfo To Map
            Map<String,Object> userInfo = new HashMap<>();
            userInfo.put("email", email);
            userInfo.put("name",name);

            // UID를 키값으로 데이터 저장
            userRef.child(uid).setValue(userInfo);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View focusView = getCurrentFocus();
        if (focusView != null) {
            Rect rect = new Rect();
            focusView.getGlobalVisibleRect(rect);
            int x = (int) ev.getX(), y = (int) ev.getY();
            if (!rect.contains(x, y)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                focusView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}
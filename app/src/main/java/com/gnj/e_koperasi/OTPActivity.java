package com.gnj.e_koperasi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;
public class OTPActivity extends AppCompatActivity {
    EditText[] otpEditTexts = new EditText[6];
    Button submit;
    String otp,phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        otpEditTexts[0] = findViewById(R.id.inputotp1);
        otpEditTexts[1] = findViewById(R.id.inputotp2);
        otpEditTexts[2] = findViewById(R.id.inputotp3);
        otpEditTexts[3] = findViewById(R.id.inputotp4);
        otpEditTexts[4] = findViewById(R.id.inputotp5);
        otpEditTexts[5] = findViewById(R.id.inputotp6);

        submit = findViewById(R.id.btnSend);
        otp = getIntent().getStringExtra("otp");
        phone = getIntent().getStringExtra("mobile");

        setupOtpTextWatchers();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOtpEmpty()) {
                    Toast.makeText(OTPActivity.this, "Please enter your OTP.", Toast.LENGTH_SHORT).show();
                } else {
                    StringBuilder inputOtpBuilder = new StringBuilder();

                    for (EditText otpEditText : otpEditTexts) {
                        inputOtpBuilder.append(otpEditText.getText().toString());
                    }

                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(otp, inputOtpBuilder.toString());
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Intent intent = new Intent(OTPActivity.this, PasswordChange.class);
                                intent.putExtra("mobile", phone);
                                intent.putExtra("frompage", "OTP");
                                startActivity(intent);
                            }else{
                                Toast.makeText(OTPActivity.this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private boolean isOtpEmpty() {
        for (EditText editText : otpEditTexts) {
            if (editText.getText().toString().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void setupOtpTextWatchers() {
        for (int i = 0; i < otpEditTexts.length; i++) {
            final EditText currentEditText = otpEditTexts[i];
            final EditText nextEditText = (i < otpEditTexts.length - 1) ? otpEditTexts[i + 1] : null;

            currentEditText.addTextChangedListener(new OtpTextWatcher(currentEditText, nextEditText));
            currentEditText.setOnKeyListener(new OtpKeyListener(currentEditText, (i > 0) ? otpEditTexts[i - 1] : null));
        }
    }

    // TextWatcher implementation
    private class OtpTextWatcher implements TextWatcher {

        private EditText currentEditText;
        private EditText nextEditText;

        public OtpTextWatcher(EditText currentEditText, EditText nextEditText) {
            this.currentEditText = currentEditText;
            this.nextEditText = nextEditText;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 1 && nextEditText != null) {
                nextEditText.requestFocus();
            }
        }
    }

    private class OtpKeyListener implements View.OnKeyListener {

        private EditText currentEditText;
        private EditText previousEditText;

        public OtpKeyListener(EditText currentEditText, EditText previousEditText) {
            this.currentEditText = currentEditText;
            this.previousEditText = previousEditText;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                if (currentEditText.getText().length() == 0 && previousEditText != null) {
                    previousEditText.requestFocus();
                }
            }
            return false;
        }


    }
}
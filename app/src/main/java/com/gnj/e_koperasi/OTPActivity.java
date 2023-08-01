package com.gnj.e_koperasi;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {
    EditText[] otpEditTexts = new EditText[6];
    Button submit;
    String otp, phone;

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
        setupOtpPasteListener();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOtpEmpty()) {
                    Toast.makeText(OTPActivity.this, "Please enter your OTP", Toast.LENGTH_SHORT).show();
                } else {
                    StringBuilder inputOtpBuilder = new StringBuilder();

                    for (EditText otpEditText : otpEditTexts) {
                        inputOtpBuilder.append(otpEditText.getText().toString());
                    }

                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(otp, inputOtpBuilder.toString());
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(OTPActivity.this, PasswordChange.class);
                                intent.putExtra("mobile", phone);
                                intent.putExtra("frompage", "OTP");
                                startActivity(intent);
                            } else {
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

    private void setupOtpPasteListener() {
        for (EditText otpEditText : otpEditTexts) {
            otpEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        handleClipboardPaste(otpEditText);
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    private void handleClipboardPaste(EditText editText) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clipData = clipboard.getPrimaryClip();
            if (clipData != null && clipData.getItemCount() > 0) {
                CharSequence clipboardText = clipData.getItemAt(0).coerceToText(this);
                if (clipboardText != null) {
                    String otpFromClipboard = clipboardText.toString().trim();

                    if (otpFromClipboard.length() == 6) {
                        editText.setText(otpFromClipboard);
                    } else {
                        // Show an error message if the OTP length is incorrect
                        Toast.makeText(OTPActivity.this, "Incorrect OTP format.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
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

    @Override
    public void onBackPressed() {
        showConfirmationDialog();
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to return to the login page?");
        builder.setPositiveButton("No", (dialog, which) -> {
            // Do nothing, dismiss the dialog
        });
        builder.setNegativeButton("Yes", (dialog, which) -> {
            navigateToLoginPage();
        });
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void navigateToLoginPage() {
        Intent back = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(back);
        super.onBackPressed();
    }

    private void navigateToForgotPassword() {
        Intent back = new Intent(getApplicationContext(), ForgotPassword.class);
        startActivity(back);
        super.onBackPressed();
    }
}
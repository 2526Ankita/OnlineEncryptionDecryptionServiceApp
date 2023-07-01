package com.example.onlineencryptionserviceapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText plaintextEditText;
    private EditText keyEditText1;
    private EditText keyEditText2;
    private Button encryptButton;
    private EditText ciphertextEditText;
    private Button decryptButton;
    private TextView ciphertextTextView;
    private TextView decryptedTextView;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toast = Toast.makeText(this, "Welcome to my App", Toast.LENGTH_SHORT);
        toast.show();

        plaintextEditText = findViewById(R.id.plaintextEditText);
        keyEditText1 = findViewById(R.id.keyEditText1);
        keyEditText2 = findViewById(R.id.keyEditText2);
        encryptButton = findViewById(R.id.encryptButton);
        ciphertextEditText = findViewById(R.id.ciphertextEditText);
        decryptButton = findViewById(R.id.decryptButton);
        ciphertextTextView = findViewById(R.id.textView8);
        decryptedTextView = findViewById(R.id.textView9);

        encryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String plaintext = plaintextEditText.getText().toString();
                String encryptionKeyString = keyEditText1.getText().toString();

                if (TextUtils.isEmpty(plaintext) || TextUtils.isEmpty(encryptionKeyString)) {
                    showToast("Please enter the plaintext and encryption key");
                    return;
                }

                try {
                    int key = Integer.parseInt(encryptionKeyString);
                    String ciphertext = encryptRailFenceCipher(plaintext, key);
                    ciphertextTextView.setText(ciphertext);
                    showToast("Encryption successful!");
                } catch (NumberFormatException e) {
                    showToast("Invalid key!");
                }
            }
        });

        ciphertextEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ciphertextTextView.setText("");
                }
            }
        });

        decryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ciphertext = ciphertextEditText.getText().toString();
                String decryptionKeyString = keyEditText2.getText().toString();

                if (TextUtils.isEmpty(ciphertext) || TextUtils.isEmpty(decryptionKeyString)) {
                    showToast("Please enter the ciphertext and decryption key");
                    return;
                }

                try {
                    int key = Integer.parseInt(decryptionKeyString);
                    String decryptedText = decryptRailFenceCipher(ciphertext, key);
                    decryptedTextView.setText(decryptedText);
                    showToast("Decryption successful!");
                } catch (NumberFormatException e) {
                    showToast("Invalid key!");
                }
            }
        });

        plaintextEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    decryptedTextView.setText("");
                }
            }
        });
    }

    private void showToast(String message) {
        toast.setText(message);
        toast.show();
    }

    private String encryptRailFenceCipher(String plaintext, int key) {
        int len = plaintext.length();
        StringBuilder rail = new StringBuilder(len * key);
        for (int i = 0; i < key; i++) {
            boolean down = false;
            int pos = i;
            while (pos < len) {
                rail.append(plaintext.charAt(pos));
                if (i == 0 || i == key - 1) {
                    pos += 2 * (key - 1);
                } else if (down) {
                    pos += 2 * (key - i - 1);
                } else {
                    pos += 2 * i;
                }
                down = !down;
            }
        }
        return rail.toString();
    }

    private String decryptRailFenceCipher(String ciphertext, int key) {
        int len = ciphertext.length();
        char[] rail = new char[len];
        int cycle = 2 * (key - 1);
        int index = 0;

        for (int i = 0; i < key; i++) {
            int pos = i;
            boolean down = false;

            while (pos < len) {
                rail[pos] = ciphertext.charAt(index++);
                if (i == 0 || i == key - 1) {
                    pos += cycle;
                } else if (down) {
                    pos += cycle - 2 * i;
                } else {
                    pos += 2 * i;
                }
                down = !down;
            }
        }

        StringBuilder plaintext = new StringBuilder(len);
        int railIndex = 0;

        for (int i = 0; i < len; i++) {
            if (railIndex < len) {
                plaintext.append(rail[railIndex]);
                railIndex++;
            }
        }

        return plaintext.toString();
    }
}
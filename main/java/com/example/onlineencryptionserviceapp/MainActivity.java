package com.example.onlineencryptionserviceapp;

import android.os.Bundle;
import android.view.View;
import android.text.TextUtils;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(this, "Welcome to my App", Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(MainActivity.this, "Please enter the plaintext and encryption key", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    int key = Integer.parseInt(encryptionKeyString);
                    String ciphertext = encryptRailFenceCipher(plaintext, key);
                    ciphertextTextView.setText(ciphertext);
                    Toast.makeText(MainActivity.this, "Encryption successful!", Toast.LENGTH_SHORT).show();
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Invalid key!", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(MainActivity.this, "Please enter the ciphertext and decryption key", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    int key = Integer.parseInt(decryptionKeyString);
                    String decryptedText = decryptRailFenceCipher(ciphertext, key);
                    decryptedTextView.setText(decryptedText);
                    Toast.makeText(MainActivity.this, "Decryption successful!", Toast.LENGTH_SHORT).show();
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Invalid key!", Toast.LENGTH_SHORT).show();
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

    private String encryptRailFenceCipher(String plaintext, int key) {
        int len = plaintext.length();
        char[][] rail = new char[key][len];
        for (int i = 0; i < key; i++) {
            for (int j = 0; j < len; j++) {
                rail[i][j] = '\n';
            }
        }

        boolean down = false;
        int row = 0, col = 0;
        for (int i = 0; i < len; i++) {
            if (row == 0 || row == key - 1) {
                down = !down;
            }
            if (Character.isLetterOrDigit(plaintext.charAt(i)) || Character.isWhitespace(plaintext.charAt(i))) {
                rail[row][col++] = plaintext.charAt(i);
            }
            if (down) {
                row++;
            } else {
                row--;
            }
        }

        StringBuilder cipherText = new StringBuilder();
        for (int i = 0; i < key; i++) {
            for (int j = 0; j < len; j++) {
                if (rail[i][j] != '\n') {
                    cipherText.append(rail[i][j]);
                }
            }
        }

        return cipherText.toString();
    }

    private String decryptRailFenceCipher(String ciphertext, int key) {
        int len = ciphertext.length();
        char[][] rail = new char[key][len];
        for (int i = 0; i < key; i++) {
            for (int j = 0; j < len; j++) {
                rail[i][j] = '\n';
            }
        }

        boolean down = false;
        int row = 0, col = 0;
        for (int i = 0; i < len; i++) {
            if (row == 0 || row == key - 1) {
                down = !down;
            }
            rail[row][col++] = '*';
            if (down) {
                row++;
            } else {
                row--;
            }
        }

        int index = 0;
        for (int i = 0; i < key; i++) {
            for (int j = 0; j < len; j++) {
                if (rail[i][j] == '*' && index < len) {
                    rail[i][j] = ciphertext.charAt(index++);
                }
            }
        }

        StringBuilder decryptedText = new StringBuilder();
        row = 0;
        col = 0;
        for (int i = 0; i < len; i++) {
            if (row == 0 || row == key - 1) {
                down = !down;
            }
            if (rail[row][col] != '*') {
                decryptedText.append(rail[row][col++]);
            }
            if (down) {
                row++;
            } else {
                row--;
            }
        }
        return decryptedText.toString();
    }
}

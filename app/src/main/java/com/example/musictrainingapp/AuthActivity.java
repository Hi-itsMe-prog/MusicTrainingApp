package com.example.musictrainingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AuthActivity extends AppCompatActivity {
    private EditText etEmail, etPassword, etUsername;
    private TextInputLayout usernameLayout;
    private Button btnLogin, btnRegister;
    private TextView tvSwitchToRegister, tvSwitchToLogin, tvFormTitle;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SessionManager sessionManager;

    private boolean isLoginMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Сначала проверяем сессию
        sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            goToMainActivity();
            return;
        }

        setContentView(R.layout.activity_auth);

        // Инициализация Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        findViews();
        setupClickListeners();
    }

    private void findViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etUsername = findViewById(R.id.etUsername);
        usernameLayout = findViewById(R.id.usernameLayout);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        tvSwitchToRegister = findViewById(R.id.tvSwitchToRegister);
        tvSwitchToLogin = findViewById(R.id.tvSwitchToLogin);
        tvFormTitle = findViewById(R.id.tvFormTitle);

        updateUIForLoginMode();
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> {
            if (validateInputs()) {
                loginUser(etEmail.getText().toString().trim(), etPassword.getText().toString());
            }
        });

        btnRegister.setOnClickListener(v -> {
            if (validateInputs()) {
                String username = etUsername.getText().toString().trim();
                registerUser(etEmail.getText().toString().trim(), etPassword.getText().toString(), username);
            }
        });

        tvSwitchToRegister.setOnClickListener(v -> switchToRegisterMode());
        tvSwitchToLogin.setOnClickListener(v -> switchToLoginMode());
    }

    private void switchToRegisterMode() {
        isLoginMode = false;
        updateUIForLoginMode();
    }

    private void switchToLoginMode() {
        isLoginMode = true;
        updateUIForLoginMode();
    }

    private void updateUIForLoginMode() {
        if (isLoginMode) {
            tvFormTitle.setText("Вход в аккаунт");
            usernameLayout.setVisibility(View.GONE);
            btnLogin.setVisibility(View.VISIBLE);
            btnRegister.setVisibility(View.GONE);
            tvSwitchToRegister.setVisibility(View.VISIBLE);
            tvSwitchToLogin.setVisibility(View.GONE);

            // Очищаем поле имени при переключении на вход
            etUsername.setText("");
        } else {
            tvFormTitle.setText("Регистрация");
            usernameLayout.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.GONE);
            btnRegister.setVisibility(View.VISIBLE);
            tvSwitchToRegister.setVisibility(View.GONE);
            tvSwitchToLogin.setVisibility(View.VISIBLE);
        }
    }

    private boolean validateInputs() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        // Для регистрации проверяем имя пользователя
        if (!isLoginMode) {
            String username = etUsername.getText().toString().trim();
            if (username.isEmpty()) {
                etUsername.setError("Введите имя пользователя");
                etUsername.requestFocus();
                return false;
            }

            if (username.length() < 2) {
                etUsername.setError("Имя должно быть не менее 2 символов");
                etUsername.requestFocus();
                return false;
            }

            if (username.length() > 20) {
                etUsername.setError("Имя должно быть не более 20 символов");
                etUsername.requestFocus();
                return false;
            }
        }

        if (email.isEmpty()) {
            etEmail.setError("Введите email");
            etEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Введите корректный email");
            etEmail.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Введите пароль");
            etPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError("Пароль должен быть не менее 6 символов");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void loginUser(String email, String password) {
        btnLogin.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Сохраняем сессию и переходим в MainActivity
                            sessionManager.createLoginSession(user.getUid(), user.getEmail());
                            Log.d("AUTH", "Успешный вход: " + user.getEmail());
                            goToMainActivity();
                        }
                    } else {
                        Toast.makeText(AuthActivity.this, "Ошибка входа: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                    btnLogin.setEnabled(true);
                });
    }

    private void registerUser(String email, String password, String username) {
        btnRegister.setEnabled(false);
        Log.d("AUTH", "Начинаем регистрацию для: " + email);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            Log.d("AUTH", "✅ Регистрация в Firebase успешна: " + firebaseUser.getUid());


                            createUserInFirestore(firebaseUser.getUid(), email, username);
                        }
                    } else {
                        Log.e("AUTH", "❌ Ошибка регистрации: " + task.getException().getMessage());
                        Toast.makeText(AuthActivity.this, "Ошибка регистрации: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                        btnRegister.setEnabled(true);
                    }
                });
    }

    private void createUserInFirestore(String userId, String email, String username) {
        User newUser = new User(
                userId,
                username, // Используем введенное имя пользователя
                email,
                0, 0, 0  // начальные значения
        );

        db.collection("users").document(userId)
                .set(newUser)
                .addOnSuccessListener(aVoid -> {
                    Log.d("AUTH", "Пользователь создан в Firestore: " + username);

                    // АВТОМАТИЧЕСКИ ВХОДИМ В СИСТЕМУ
                    // 1. Сохраняем сессию
                    sessionManager.createLoginSession(userId, email);
                    Log.d("AUTH", "Сессия сохранена");

                    // 2. Переходим в главное activity
                    Log.d("AUTH", "Переходим в MainActivity");
                    goToMainActivity();
                })
                .addOnFailureListener(e -> {
                    Log.e("AUTH", "Ошибка создания пользователя в Firestore: " + e.getMessage());
                    // Даже если ошибка с Firestore, все равно входим в аккаунт
                    Toast.makeText(AuthActivity.this, "Профиль создан, но возникла ошибка с данными", Toast.LENGTH_SHORT).show();

                    // Все равно сохраняем сессию и переходим
                    sessionManager.createLoginSession(userId, email);
                    goToMainActivity();
                });
    }

    private void goToMainActivity() {
        Log.d("AUTH", "Переход в MainActivity");
        Intent intent = new Intent(AuthActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
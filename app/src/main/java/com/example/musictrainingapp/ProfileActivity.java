package com.example.musictrainingapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private ImageButton backButton;
    private Button btnLogout, btnDeleteAccount;
    private TextView tvUserName, tvUserEmail, tvTotalScore, tvTotalTime;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        sessionManager = new SessionManager(this);

        findViews();
        setupClickListeners();
        loadProfileData();
    }

    private void findViews() {
        backButton = findViewById(R.id.backButton);
        btnLogout = findViewById(R.id.btnLogout);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvTotalScore = findViewById(R.id.tvTotalScore);
        tvTotalTime = findViewById(R.id.tvTotalTime);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
        btnLogout.setOnClickListener(v -> logoutUser());
        btnDeleteAccount.setOnClickListener(v -> showDeleteAccountConfirmation());
    }

    private void showDeleteAccountConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Удаление аккаунта")
                .setMessage("ВНИМАНИЕ! Это действие нельзя отменить.\n\n" +
                        "Что будет удалено:\n" +
                        "• Все ваши данные прогресса\n" +
                        "• Статистика упражнений\n" +
                        "• История тренировок\n\n" +
                        "Вы уверены, что хотите удалить аккаунт?")
                .setPositiveButton("УДАЛИТЬ АККАУНТ", (dialog, which) -> startAccountDeletion())
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void startAccountDeletion() {
        Log.d("DELETE", "Starting account deletion process");
        Toast.makeText(this, "Начинаем удаление...", Toast.LENGTH_SHORT).show();

        // 1. Помечаем пользователя как удаленного в Firestore
        markUserAsDeleted();
    }

    private void markUserAsDeleted() {
        String userId = sessionManager.getUserId();
        if (userId == null) {
            Log.e("DELETE", "User ID is null");
            forceLogout();
            return;
        }

        // Создаем запись в отдельной коллекции для удаленных пользователей
        Map<String, Object> deletedUser = new HashMap<>();
        deletedUser.put("userId", userId);
        deletedUser.put("email", sessionManager.getUserEmail());
        deletedUser.put("deletedAt", FieldValue.serverTimestamp());
        deletedUser.put("status", "deleted");

        db.collection("deleted_users").document(userId)
                .set(deletedUser)
                .addOnSuccessListener(aVoid -> {
                    Log.d("DELETE", "User marked as deleted in Firestore");
                    // 2. Удаляем основные данные пользователя
                    deleteUserData();
                })
                .addOnFailureListener(e -> {
                    Log.e("DELETE", "Error marking user as deleted: " + e.getMessage());
                    // Все равно продолжаем удаление
                    deleteUserData();
                });
    }

    private void deleteUserData() {
        String userId = sessionManager.getUserId();
        if (userId == null) {
            forceLogout();
            return;
        }

        // Удаляем пользователя из основной коллекции
        db.collection("users").document(userId).delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("DELETE", "User data deleted from Firestore");
                    completeDeletion();
                })
                .addOnFailureListener(e -> {
                    Log.e("DELETE", "Error deleting user data: " + e.getMessage());
                    completeDeletion();
                });
    }

    private void completeDeletion() {
        Log.d("DELETE", "Completing deletion process");

        // 1. Выходим из Firebase
        mAuth.signOut();

        // 2. Очищаем сессию
        sessionManager.logoutUser();

        // 3. Показываем сообщение об успехе
        Toast.makeText(this, "Аккаунт успешно удален!", Toast.LENGTH_LONG).show();

        // 4. Переходим на AuthActivity
        goToAuthActivity();
    }

    private void forceLogout() {
        Log.d("DELETE", "Forcing logout");
        mAuth.signOut();
        sessionManager.logoutUser();
        goToAuthActivity();
    }

    private void goToAuthActivity() {
        Log.d("DELETE", "Going to AuthActivity");
        Intent intent = new Intent(this, AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void loadProfileData() {
        String userId = sessionManager.getUserId();
        if (userId != null) {
            db.collection("users").document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                            User user = task.getResult().toObject(User.class);
                            if (user != null) {
                                updateUIWithUserData(user);
                                return;
                            }
                        }
                        showDefaultData();
                    });
        } else {
            showDefaultData();
        }
    }

    private void updateUIWithUserData(User user) {
        if (tvUserName != null) {
            tvUserName.setText(user.getUsername() != null ? user.getUsername() : "Пользователь");
        }
        if (tvUserEmail != null) {
            tvUserEmail.setText(user.getEmail() != null ? user.getEmail() : "email@example.com");
        }

        int totalScore = user.getProgress() * 10 + user.getAccuracy() * 5;
        int totalTime = user.getTotalExercises() * 2;

        if (tvTotalScore != null) {
            tvTotalScore.setText("Общий счёт: " + totalScore);
        }
        if (tvTotalTime != null) {
            tvTotalTime.setText("Общее время: " + totalTime + " мин");
        }
    }

    private void showDefaultData() {
        String userEmail = sessionManager.getUserEmail();
        String userName = "Пользователь";

        if (userEmail != null && userEmail.contains("@")) {
            userName = userEmail.split("@")[0];
        }

        if (tvUserName != null) {
            tvUserName.setText(userName);
        }
        if (tvUserEmail != null && userEmail != null) {
            tvUserEmail.setText(userEmail);
        }
        if (tvTotalScore != null) {
            tvTotalScore.setText("Общий счёт: 0");
        }
        if (tvTotalTime != null) {
            tvTotalTime.setText("Общее время: 0 мин");
        }
    }

    private void logoutUser() {
        new AlertDialog.Builder(this)
                .setTitle("Выход из аккаунта")
                .setMessage("Вы уверены, что хотите выйти?")
                .setPositiveButton("Выйти", (dialog, which) -> {
                    mAuth.signOut();
                    sessionManager.logoutUser();
                    goToAuthActivity();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }
}
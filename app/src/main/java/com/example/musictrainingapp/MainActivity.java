package com.example.musictrainingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private static final int PROFILE_REQUEST_CODE = 1001;

    private TextView tvWelcome, tvProgress, tvTotalExercises, tvAccuracy;
    private ProgressBar progressBar;
    private ImageButton btnProfile;
    private CustomButt1 cardExercises, cardDictionary;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

        if (sessionManager.isFirstLaunch() || !sessionManager.isLoggedIn()) {
            goToAuthActivity();
            return;
        }

        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        findViews();
        setupClickListeners();
        loadUserData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("MainActivity", "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == PROFILE_REQUEST_CODE) {
            // ВСЕГДА обновляем данные при возвращении из профиля
            Log.d("MainActivity", "Returned from profile, refreshing data...");
            loadUserData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Дополнительная проверка при возвращении в приложение
        if (!sessionManager.isLoggedIn()) {
            goToAuthActivity();
        }
    }

    private void goToAuthActivity() {
        Intent intent = new Intent(this, AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void findViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        tvProgress = findViewById(R.id.tvProgress);
        tvTotalExercises = findViewById(R.id.tvTotalExercises);
        tvAccuracy = findViewById(R.id.tvAccuracy);
        progressBar = findViewById(R.id.progressBar);
        btnProfile = findViewById(R.id.btnProfile);

        cardExercises = findViewById(R.id.cardExercises);
        cardDictionary = findViewById(R.id.cardDictionary);
    }

    private void setupClickListeners() {
        btnProfile.setOnClickListener(v -> {
            Log.d("MainActivity", "Opening profile activity");
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivityForResult(intent, PROFILE_REQUEST_CODE);
        });

        if (cardExercises != null) {
            cardExercises.setOnClickListener(v -> {
                Intent intent = new Intent(this, ExerciseActivity.class);
                startActivity(intent);
            });
        }

        if (cardDictionary != null) {
            cardDictionary.setOnClickListener(v -> {
                Intent intent = new Intent(this, DictionaryActivity.class);
                startActivity(intent);
            });
        }
    }

    private void loadUserData() {
        Log.d("MainActivity", "Loading user data...");

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            loadUserDataFromFirestore();
        } else {
            sessionManager.logoutUser();
            goToAuthActivity();
        }
    }

    private void loadUserDataFromFirestore() {
        String userId = sessionManager.getUserId();
        if (userId == null) {
            loadDefaultData();
            return;
        }

        DocumentReference userRef = db.collection("users").document(userId);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    User user = document.toObject(User.class);
                    if (user != null) {
                        Log.d("MainActivity", "User data loaded: " + user.getUsername());
                        updateUIWithUserData(user);
                        return;
                    }
                }
            }
            loadDefaultData();
        });
    }

    private void updateUIWithUserData(User user) {
        String username = user.getUsername();
        if (username == null || username.isEmpty()) {
            // Если имя не установлено, используем email
            String email = sessionManager.getUserEmail();
            if (email != null && email.contains("@")) {
                username = email.split("@")[0];
            } else {
                username = "Пользователь";
            }
        }

        if (tvWelcome != null) {
            tvWelcome.setText("Добро пожаловать, " + username + "!");
        }
        if (tvProgress != null) {
            tvProgress.setText(user.getProgress() + "%");
        }
        if (tvAccuracy != null) {
            tvAccuracy.setText(user.getAccuracy() + "%");
        }
        if (progressBar != null) {
            progressBar.setProgress(user.getProgress());
        }
        if (tvTotalExercises != null) {
            tvTotalExercises.setText(String.valueOf(user.getTotalExercises()));
        }
    }

    private void loadDefaultData() {
        String userEmail = sessionManager.getUserEmail();
        String username = "Пользователь";

        if (userEmail != null && userEmail.contains("@")) {
            username = userEmail.split("@")[0];
        }

        if (tvWelcome != null) {
            tvWelcome.setText("Добро пожаловать, " + username + "!");
        }
        if (tvProgress != null) {
            tvProgress.setText("0%");
        }
        if (tvAccuracy != null) {
            tvAccuracy.setText("0%");
        }
        if (progressBar != null) {
            progressBar.setProgress(0);
        }
        if (tvTotalExercises != null) {
            tvTotalExercises.setText("0");
        }
    }
}
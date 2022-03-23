package com.example.braintrainer;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView textViewScoreMax;
    private TextView textViewScoreLast;
    private TextView textViewGameOver;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.hide();
        }
        textViewScoreMax = findViewById(R.id.textViewScoreMax);
        textViewScoreLast = findViewById(R.id.textViewScoreLast);
        textViewGameOver = findViewById(R.id.textViewGameOver);
        textViewScoreLast.setVisibility(View.INVISIBLE);
        int score = 0;
        Intent intent = getIntent();
        if (intent.hasExtra("score")) {
            score = intent.getIntExtra("score", 0);
            textViewScoreLast.setText(String.format(getString(R.string.your_score), score));
            textViewScoreLast.setVisibility(View.VISIBLE);
        }
        if (intent.hasExtra("game_over"))
            textViewGameOver.setText(intent.getStringExtra("game_over"));
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (score > preferences.getInt("max_score", 0))
            preferences.edit().putInt("max_score", score).apply();
        int max_score = preferences.getInt("max_score", 0);
        textViewScoreMax.setText(String.format(getString(R.string.max_score), max_score));
    }

    public void onClickStartGame(View view) {
        Intent intent = new Intent(MainActivity.this, PlayGame.class);
        startActivity(intent);
        finish();
    }
}
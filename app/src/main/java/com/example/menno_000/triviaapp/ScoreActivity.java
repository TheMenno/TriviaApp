package com.example.menno_000.triviaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ScoreActivity extends AppCompatActivity {

    int score;
    TextView finalscore_view, highscore_view;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        // Receive data from previous screen
        Intent intent = getIntent();
        score = intent.getIntExtra("score", 0);

        // Set the final score into the textview
        finalscore_view = findViewById(R.id.finalscore);
        String final_score = "Your score was " + String.valueOf(score) + " points!";
        finalscore_view.setText(final_score);

        // Save highscores using sharedpreferences
        prefs = getSharedPreferences("settings", MODE_PRIVATE);
        int highscore = prefs.getInt("highscore", 0);

        // Set the high score
        highscore_view = findViewById(R.id.highscore);
        String highscore_message = "Your highscore is: " + highscore + " points!";
        highscore_view.setText(highscore_message);

        // Act on newHighscore when a new highscore has been achieved
        if (score > highscore) {
            newHighscore();
        }
    }


    // Sets the new highscore and a special message
    public void newHighscore() {

        // Set the new high score
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("highscore", score);
        editor.apply();

        // Set high score message message
        String highscore_message = "You achieved a new high score!";
        highscore_view.setText(highscore_message);
    }
}

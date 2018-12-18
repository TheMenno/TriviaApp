package com.example.menno_000.triviaapp;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements QuestionsRequest.Callback {

    // Containers
    ArrayList<ArrayList> questions;
    ArrayList<String> a_question;

    // Views
    TextView question_view, lives_view, score_view;
    Button button1, button2, button3, button4;

    // Game variables
    int lives, question_nr, score, true_button;
    String question, answer, wrong1, wrong2, wrong3, difficulty, right_button, clicked_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Retrieve the questions from the QuestionsRequest function
        QuestionsRequest request = new QuestionsRequest(this);
        request.getQuestions(this);

        // Find views
        question_view = findViewById(R.id.question);
        lives_view = findViewById(R.id.lives);
        score_view = findViewById(R.id.score);

        // Find buttons
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);

        // Setting up the listeners
        findViewById(R.id.button1).setOnClickListener(new MainActivity.Listener());
        findViewById(R.id.button2).setOnClickListener(new MainActivity.Listener());
        findViewById(R.id.button3).setOnClickListener(new MainActivity.Listener());
        findViewById(R.id.button4).setOnClickListener(new MainActivity.Listener());

    }


    // Listener for the answer buttons
    public class Listener implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.button1:
                    clicked_button = "button1";
                    break;
                case R.id.button2:
                    clicked_button = "button2";
                    break;
                case R.id.button3:
                    clicked_button = "button3";
                    break;
                case R.id.button4:
                    clicked_button = "button4";
                    break;
            }

            clicked(view);
        }
    }



    // Restarts the quiz when the player presses back on the score screen
    @Override
    public void onRestart() {
        super.onRestart();

        // Re-enable the buttons
        button1.setClickable(true);
        button2.setClickable(true);
        button3.setClickable(true);
        button4.setClickable(true);

        //Refresh your stuff here
        QuestionsRequest request = new QuestionsRequest(this);
        request.getQuestions(this);
    }


    // Create a game when the questions are loaded successfully
    @Override
    public void gotQuestions(ArrayList<ArrayList> response) {

        questions = response;
        createGame();
    }


    // Error message
    @Override
    public void gotQuestionsError(String message) {

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    // Create the game and start it
    public void createGame() {

        lives = 3;
        score = 0;
        question_nr = 0;

        askQuestion();
    }


    // Sets the questions in the activity
    public void askQuestion() {

        // Get the current question
        a_question = questions.get(question_nr);

        // Get the values of the different keys
        difficulty = a_question.get(0);
        question = a_question.get(1);
        answer = a_question.get(2);
        wrong1 = a_question.get(3);
        wrong2 = a_question.get(4);
        wrong3 = a_question.get(5);

        // Randomise the arraylist
        ArrayList<String> answers = new ArrayList<>();
        answers.add(answer);
        answers.add(wrong1);
        answers.add(wrong2);
        answers.add(wrong3);
        Collections.shuffle(answers);

        for (int i = 0; i < answers.size(); i++) {
            if (answers.get(i).equals(answer)) {
                true_button = i;
            }
        }

        // Set the right_button value to the button with the correct answer
        switch (true_button) {
            case 0:
                right_button = "button1";
                break;
            case 1:
                right_button = "button2";
                break;
            case 2:
                right_button = "button3";
                break;
            case 3:
                right_button = "button4";
                break;
        }

        // Set the question fields
        question_view.setText(question);
        //difficulty_view.setText(difficulty + " question");

        // Set the answer fields
        button1.setText(answers.get(0));
        button2.setText(answers.get(1));
        button3.setText(answers.get(2));
        button4.setText(answers.get(3));

        // Set the lives and score
        String lives_text = String.valueOf(lives) + " lives left";
        String score_text = "score: " + String.valueOf(score);

        lives_view.setText(lives_text);
        score_view.setText(score_text);
    }


    // Acts when the user chose an answer
    public void clicked(View view) {

        if (clicked_button.equals(right_button)) {
            score = score + 1;

            question_view.setText("string");
        } else {
            lives = lives - 1;

            question_view.setText("Wrong!");
        }

        // Temporarily disable the buttons
        button1.setClickable(false);
        button2.setClickable(false);
        button3.setClickable(false);
        button4.setClickable(false);

        // Set a small delay
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                // Player lost
                if (lives == 0) {
                    gameOver();
                }
                // Player is still playing
                else {
                    question_nr += 1;

                    // Enable the buttons again
                    button1.setClickable(true);
                    button2.setClickable(true);
                    button3.setClickable(true);
                    button4.setClickable(true);

                    // Ask a new question
                    askQuestion();
                }
            }
        }, 1000);
    }


    // Acts when the user chose the wrong answer three times
    public void gameOver() {

        // Go to the score screen
        Intent intent = new Intent(this, ScoreActivity.class);
        intent.putExtra("score", score);

        this.startActivity(intent);
    }
}

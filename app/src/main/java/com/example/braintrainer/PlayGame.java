package com.example.braintrainer;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class PlayGame extends AppCompatActivity {

    private TextView textViewScore;
    private TextView textViewMistakes;
    private TextView textViewTimer;
    private TextView textViewExample;
    private TextView textViewIsRight;

    private final Handler handler = new Handler();

    private boolean isNextQuestion = false;
    private boolean isGamePaused = false;
    private boolean isGameEnded = false;

    private int countDownTime = 21000;
    private int level = -1;
    private final int max_level = 14;
    private int score = 0;
    private int mistakes = 0;
    private final int max_mistakes = 5;
    private int countOfQuestions = 0;
    private final int questionsToNextLevel = 6;
    private String question;
    private int rightAnswer;

    private ArrayList<Integer> answers;
    private ArrayList<TextView> options;

    private final char[] operation = {'+', '-', '÷', '×'};

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.hide();
        }
        textViewScore = findViewById(R.id.textViewScore);
        textViewMistakes = findViewById(R.id.textViewMistakes);
        textViewTimer = findViewById(R.id.textViewTimer);
        textViewExample = findViewById(R.id.textViewExample);
        textViewIsRight = findViewById(R.id.textViewIsRight);
        TextView textViewChose0 = findViewById(R.id.textViewChose0);
        TextView textViewChose1 = findViewById(R.id.textViewChose1);
        TextView textViewChose2 = findViewById(R.id.textViewChose2);
        TextView textViewChose3 = findViewById(R.id.textViewChose3);
        options = new ArrayList<>();
        options.add(textViewChose0);
        options.add(textViewChose1);
        options.add(textViewChose2);
        options.add(textViewChose3);
        playGame();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onPause() {
        super.onPause();
        isGamePaused = true;
        countOfQuestions++;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isGameEnded && isGamePaused) {
            textViewIsRight.setText(getString(R.string.game_paused));
            textViewIsRight.setTextColor(getResources().getColor(R.color.red));
        }
    }

    public void generateQuestion() {
        int min = 2;
        int max_a = 0, max_b = 0;
        int operationNum = 0;
        switch (level) { //по уровню задаем границы чисел a и b
            case 0:
            case 1:
            case 2:
                max_a = 50;
                max_b = 50;
                break;
            case 3:
            case 4:
            case 5:
                max_a = 100;
                max_b = 100;
                break;
            case 6:
            case 7:
            case 8:
                max_a = 200;
                max_b = 200;
                break;
            case 9:
            case 10:
            case 11:
                max_a = 500;
                max_b = 500;
                break;
            case 12:
            case 13:
            case 14:
                max_a = 1000;
                max_b = 1000;
                break;
        }
        switch (level){ // по уровню выбираем операцию
            case 0:
            case 3:
            case 6:
            case 9:
            case 12:
                operationNum = 1; // + -
                break;
            case 1:
            case 4:
            case 7:
            case 10:
            case 13:
                operationNum = 2; // ÷
                break;
            case 2:
            case 5:
            case 8:
            case 11:
            case 14:
                operationNum = 3; // *
                break;
        }
        switch (level) { // по уровню выбираем выбираем время для ответа
            case 0:
            case 1:
            case 3:
            case 4:
                countDownTime = 21000;
                break;
            case 6:
            case 7:
                countDownTime = 31000;
                break;
            case 12:
            case 13:
                countDownTime = 51000;
                break;
            case 9:
            case 10:
                // ---
            case 2:
            case 5:
                countDownTime = 41000;
                break;
            case 11:
                countDownTime = 81000;
                break;
            case 14:
                countDownTime = 101000;
                break;
        }
        int a = (int) (Math.random() * (max_a - min + 1) + min);
        int b = (int) (Math.random() * (max_b - min + 1) + min);
        if (operationNum == 1)
            operationNum = (int) (Math.random() * 2); // выбираем операцию либо +, либо -
        if (operation[operationNum] == '÷') {  // синтезируем деление a на b без остатка
            max_b /= 2; // уменьшаем границы числа b в 2 раза при делении a на b
            while (a % b != 0 || a < b || a == b) {
                a = (int) (Math.random() * (max_a - min + 1) + min);
                b = (int) (Math.random() * (max_b - min + 1) + min);
            }
        }
        boolean isNegativeA = ((int) (Math.random() * 2) == 1);
        boolean isNegativeB = ((int) (Math.random() * 2) == 1);
        if (isNegativeA) a *= (-1);
        if (isNegativeB) {
            b *= (-1);
            question = String.format("%d " + operation[operationNum] + " (%d)", a, b);
        } else {
            question = String.format("%d " + operation[operationNum] + " %d", a, b);
        }
        switch (operation[operationNum]) {
            case '+':
                rightAnswer = a + b;
                break;
            case '-':
                rightAnswer = a - b;
                break;
            case '×':
                rightAnswer = a * b;
                break;
            case '÷':
                rightAnswer = a / b;
                break;
        }
    }

    public void generateAnswers(int max_deviation) {
        int rightAnswerPos = (int) (Math.random() * 4);
        int deviation;
        boolean isNegativeTemp;
        answers = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (i != rightAnswerPos) {
                deviation = (int) (Math.random() * max_deviation + 1);
                isNegativeTemp = ((int) (Math.random() * 2) == 1);
                if (isNegativeTemp) deviation *= (-1);
                answers.add(rightAnswer + deviation);
            } else {
                answers.add(rightAnswer);
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private String getTime(long milliseconds) {
        int seconds = (int) (milliseconds / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public void endGame() {
        isNextQuestion = true;
        isGameEnded = true;
        Intent intent = new Intent(PlayGame.this, MainActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("game_over", getString(R.string.game_over));
        startActivity(intent);
        finish();
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    public void playGame() {
        textViewIsRight.setText("");
        textViewScore.setText(String.format(getString(R.string.score_play), score, countOfQuestions));
        textViewMistakes.setText(String.format(getString(R.string.mistakes_play), mistakes, max_mistakes));
        textViewTimer.setTextColor(getResources().getColor(R.color.green));
        if (countOfQuestions % questionsToNextLevel == 0 && level < max_level) level++;
        generateQuestion();
        textViewExample.setText(question + " = ?");
        generateAnswers(50);
        for (int i = 0; i < options.size(); i++) {
            options.get(i).setText(Integer.toString(answers.get(i)));
        }
        isNextQuestion = false;
        CountDownTimer timer = new CountDownTimer(countDownTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (isNextQuestion || isGamePaused)
                    cancel();
                textViewTimer.setText(getTime(millisUntilFinished));
                if (millisUntilFinished <= 12000 && millisUntilFinished >= 11000)
                    textViewTimer.setTextColor(getResources().getColor(R.color.orange));
                if (millisUntilFinished <= 7000 && millisUntilFinished >= 6000)
                    textViewTimer.setTextColor(getResources().getColor(R.color.red));
            }

            @Override
            public void onFinish() {
                if (!isNextQuestion && !isGamePaused) {
                    mistakes++;
                    countOfQuestions++;
                    playGame();
                }
            }
        };
        timer.start();
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n", "DefaultLocale"})
    public void onClickAnswer(View view) {
        if (isGamePaused) {
            isGamePaused = false;
            playGame();
        } else if (!isNextQuestion) {
            TextView textViewChosenAnswer = (TextView) view;
            int chosenAnswer = Integer.parseInt(textViewChosenAnswer.getText().toString());
            textViewExample.setText(question + " = " + chosenAnswer);
            countOfQuestions++;
            if (chosenAnswer == rightAnswer) {
                score++;
                textViewIsRight.setText(R.string.right_answer);
                textViewIsRight.setTextColor(getResources().getColor(R.color.green));
            } else {
                mistakes++;
                textViewIsRight.setText(R.string.wrong_answer);
                textViewIsRight.setTextColor(getResources().getColor(R.color.red));
            }
            isNextQuestion = true;
            if (mistakes <= max_mistakes) {
                handler.postDelayed(this::playGame, 2000);
            } else {
                textViewScore.setText(String.format(getString(R.string.score_play), score, countOfQuestions));
                textViewMistakes.setText(getString(R.string.game_over));
                handler.postDelayed(this::endGame, 4000);
            }
        }
    }
}
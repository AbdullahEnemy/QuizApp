package com.example.quizapplicaionAbdullahFayyaz;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MainActivity extends AppCompatActivity {

    private TextView questionText, timerText;
    private RadioGroup optionsGroup;
    private Button prevButton, nextButton, showAnswerButton, endQuizButton;
    private String[] questions, answers;
    private String[][] options;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int correctAnswerIndex;
    private boolean[] answeredCorrectly;
    private boolean quizEnded = false;
    private Handler handler = new Handler(Looper.getMainLooper());
    private long startTime;
    private static final long EXAM_DURATION_MS = 5 * 60 * 1000; // 5 minutes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        questionText = findViewById(R.id.questionText);
        optionsGroup = findViewById(R.id.optionsGroup);
        timerText = findViewById(R.id.timerText);
        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);
        showAnswerButton = findViewById(R.id.showAnswerButton);
        endQuizButton = findViewById(R.id.endQuizButton);

        // Load questions, answers, and options from strings.xml
        questions = getResources().getStringArray(R.array.questions);
        answers = getResources().getStringArray(R.array.correct_answers);
        options = new String[][]{
                getResources().getStringArray(R.array.q1_options),
                getResources().getStringArray(R.array.q2_options),
                getResources().getStringArray(R.array.q3_options),
                getResources().getStringArray(R.array.q4_options),
                getResources().getStringArray(R.array.q5_options),
                getResources().getStringArray(R.array.q6_options),
                getResources().getStringArray(R.array.q7_options),
                getResources().getStringArray(R.array.q8_options),
                getResources().getStringArray(R.array.q9_options),
                getResources().getStringArray(R.array.q10_options),
                getResources().getStringArray(R.array.q11_options),
                getResources().getStringArray(R.array.q12_options),
                getResources().getStringArray(R.array.q13_options),
                getResources().getStringArray(R.array.q14_options),
                getResources().getStringArray(R.array.q15_options),
                getResources().getStringArray(R.array.q16_options),
                getResources().getStringArray(R.array.q17_options),
                getResources().getStringArray(R.array.q18_options),
                getResources().getStringArray(R.array.q19_options),
                getResources().getStringArray(R.array.q20_options),
        };

        answeredCorrectly = new boolean[questions.length];

        // Display the first question
        displayQuestion();

        // Start the timer
        startTimer();

        // Previous button click listener
        prevButton.setOnClickListener(v -> {
            if (!quizEnded && currentQuestionIndex > 0) {
                currentQuestionIndex--;
                displayQuestion();
            }
        });

        // Next button click listener
        nextButton.setOnClickListener(v -> {
            if (!quizEnded && currentQuestionIndex < questions.length - 1) {
                currentQuestionIndex++;
                displayQuestion();
            }
        });

        // Show Answer button click listener
        showAnswerButton.setOnClickListener(v -> {
            if (!quizEnded) {
                score--;
                highlightCorrectAnswer();
            }
        });

        // End Quiz button click listener
        endQuizButton.setOnClickListener(v -> endQuiz());
    }

    private void startTimer() {
        startTime = System.currentTimeMillis();
        handler.post(timerRunnable);
    }

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (quizEnded) return;

            long elapsedTime = System.currentTimeMillis() - startTime;
            long remainingTime = EXAM_DURATION_MS - elapsedTime;

            if (remainingTime > 0) {
                long minutes = (remainingTime / 1000) / 60;
                long seconds = (remainingTime / 1000) % 60;
                timerText.setText(String.format("%02d:%02d", minutes, seconds));
                handler.postDelayed(this, 1000);
            } else {
                endQuiz();
            }
        }
    };

    private void displayQuestion() {
        if (quizEnded) return;

        // Set the question text
        questionText.setText(questions[currentQuestionIndex]);

        // Clear previous radio group selections
        optionsGroup.clearCheck();

        // Set the options
        for (int i = 0; i < optionsGroup.getChildCount(); i++) {
            ((RadioButton) optionsGroup.getChildAt(i)).setText(options[currentQuestionIndex][i]);
        }

        // Check if already answered and highlight the selected option if answered
        optionsGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (quizEnded) return;

            int selectedOption = optionsGroup.indexOfChild(findViewById(checkedId));
            if (selectedOption == correctAnswerIndex) {
                score += 5;
                answeredCorrectly[currentQuestionIndex] = true;
            } else {
                score -= 1;
                answeredCorrectly[currentQuestionIndex] = false;
            }
        });
    }

    private void highlightCorrectAnswer() {
        correctAnswerIndex = getCorrectAnswerIndex();
        if (correctAnswerIndex != -1) {
            RadioButton correctOption = (RadioButton) optionsGroup.getChildAt(correctAnswerIndex);
            correctOption.setChecked(true); // Highlight the correct option
        }
    }

    private void endQuiz() {
        if (quizEnded) return;

        quizEnded = true;
        // Hide all views
        questionText.setVisibility(View.GONE);
        optionsGroup.setVisibility(View.GONE);
        prevButton.setVisibility(View.GONE);
        nextButton.setVisibility(View.GONE);
        showAnswerButton.setVisibility(View.GONE);
        endQuizButton.setVisibility(View.GONE);
        timerText.setVisibility(View.GONE);

        // Calculate the percentage
        int totalQuestions = questions.length;
        int percentage = (score * 100) / (totalQuestions * 5); // Max score per question is 5

        // Show total score and percentage in the center
        TextView resultTextView = new TextView(this);
        resultTextView.setText("Total Score: " + score + "\nPercentage: " + percentage + "%");
        resultTextView.setTextSize(24);
        resultTextView.setGravity(View.TEXT_ALIGNMENT_CENTER);

        // Add resultTextView to the layout
        ConstraintLayout layout = findViewById(R.id.main);
        layout.removeAllViews(); // Remove other views
        layout.addView(resultTextView);
    }

    private int getCorrectAnswerIndex() {
        String correctAnswer = answers[currentQuestionIndex];
        for (int i = 0; i < options[currentQuestionIndex].length; i++) {
            if (options[currentQuestionIndex][i].equals(correctAnswer)) {
                return i;
            }
        }
        return -1; // Default if not found
    }
}

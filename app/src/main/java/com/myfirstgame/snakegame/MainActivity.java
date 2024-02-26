package com.myfirstgame.snakegame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.myfirstgame.snakegame.controller.SnakeGameController;

/**
 * MainActivity class for the Snake Game.
 * This activity initializes the game and handles the game's lifecycle events.
 */
public class MainActivity extends AppCompatActivity {
    // Controller for the Snake Game, manages game logic and rendering.
    private SnakeGameController snakeGameController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the game controller
        snakeGameController = new SnakeGameController(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        snakeGameController.resume(); // Resume the game when the activity resumes
    }

    @Override
    protected void onPause() {
        super.onPause();
        snakeGameController.pause(); // Pause the game when the activity pauses
    }
}

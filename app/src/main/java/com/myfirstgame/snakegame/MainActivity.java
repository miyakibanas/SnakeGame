package com.myfirstgame.snakegame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.myfirstgame.snakegame.controller.SnakeGameController;

public class MainActivity extends AppCompatActivity {
    private SnakeGameController snakeGameController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        snakeGameController = new SnakeGameController(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        snakeGameController.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        snakeGameController.pause();
    }
}

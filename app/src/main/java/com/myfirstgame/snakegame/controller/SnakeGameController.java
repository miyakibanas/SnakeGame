package com.myfirstgame.snakegame.controller;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.myfirstgame.snakegame.model.SnakeGame;

public class SnakeGameController extends SurfaceView implements Runnable, SurfaceHolder.Callback  {
    private Thread gameThread;
    private SurfaceHolder surfaceHolder;
    private volatile boolean playing;
    private SnakeGame snakeGame;
    private Paint paint;
    private float startX;
    private float startY;

    public SnakeGameController(Context context) {
        super(context);
        init(null, 0);
    }

    // Constructor used when inflating from XML
    public SnakeGameController(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        init(attrs, 0);
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        resume(); // Start the game loop when the surface is created
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Handle changes if necessary
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        pause(); // Stop the game loop when the surface is destroyed
    }
    // Another constructor variation used in some cases
    public SnakeGameController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    // Initialization method
    private void init(AttributeSet attrs, int defStyleAttr) {
        // Perform initialization and setup here (e.g., getting the SurfaceHolder, initializing game objects)
        surfaceHolder = getHolder();
        snakeGame = new SnakeGame();
        paint = new Paint();
    }

    @Override
    public void run() {
        while (playing) {
            if (!surfaceHolder.getSurface().isValid()) {
                continue;
            }
            Canvas canvas = surfaceHolder.lockCanvas();
            if (canvas != null) {
                try {
                    synchronized (surfaceHolder) {
                        if (!snakeGame.isGameOver()) {
                            drawGame(canvas);
                        }
                    }
                } finally {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
            snakeGame.updateGame();
            if (snakeGame.isGameOver()) {
                playing = false;
                post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "Game Over! Tap to restart.", Toast.LENGTH_LONG).show();
                    }
                });
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private void drawGame(Canvas canvas) {
        Log.d("SnakeGameController", "Drawing game");
        canvas.drawColor(Color.BLACK);
        paint.setColor(Color.GREEN);
        for (SnakeGame.Point point : snakeGame.getSnake()) {
            canvas.drawRect(point.getX() * 20, point.getY() * 20,
                    (point.getX() + 1) * 20, (point.getY() + 1) * 20, paint);
        }
        paint.setColor(Color.RED);
        SnakeGame.Point apple = snakeGame.getApple();
        canvas.drawRect(apple.getX() * 20, apple.getY() * 20,
                (apple.getX() + 1) * 20, (apple.getY() + 1) * 20, paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(40);
        canvas.drawText("Score: " + snakeGame.getScore(), 10, 30, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        if (snakeGame.isGameOver()) {
            if (action == MotionEvent.ACTION_DOWN) {
                return true;
            } else if (action == MotionEvent.ACTION_UP) {
                restartGame();
                return true;
            }
        } else {
            if (action == MotionEvent.ACTION_DOWN) {
                startX = event.getX();
                startY = event.getY();
                return true;
            } else if (action == MotionEvent.ACTION_UP) {
                float endX = event.getX();
                float endY = event.getY();

                float deltaX = endX - startX;
                float deltaY = endY - startY;

                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    if (deltaX > 0) {
                        snakeGame.setDirection(SnakeGame.Direction.RIGHT);
                    } else {
                        snakeGame.setDirection(SnakeGame.Direction.LEFT);
                    }
                } else {
                    if (deltaY > 0) {
                        snakeGame.setDirection(SnakeGame.Direction.DOWN);
                    } else {
                        snakeGame.setDirection(SnakeGame.Direction.UP);
                    }
                }
                performClick();
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }
    public void restartGame() {
        playing = true;
        snakeGame.resetScore();
        snakeGame.restartGame(); // Reset the game state
        if (gameThread != null) {
            gameThread.interrupt(); // Interrupt the old thread if it's still running
        }
        gameThread = new Thread(this); // Create a new game thread
        gameThread.start(); // Start the game loop again
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

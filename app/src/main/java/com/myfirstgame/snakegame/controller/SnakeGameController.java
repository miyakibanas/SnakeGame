package com.myfirstgame.snakegame.controller;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.myfirstgame.snakegame.model.SnakeGame;

// Controller class for the Snake game, handling game logic and user input
public class SnakeGameController extends SurfaceView implements Runnable, SurfaceHolder.Callback {
    private Thread gameThread; // Game thread for the main game loop
    private SurfaceHolder surfaceHolder; // Holder for the surface view
    private volatile boolean playing; // Boolean flag to check if the game is playing
    private SnakeGame snakeGame; // Instance of SnakeGame for game logic and state
    private Paint paint; // Paint object for drawing game objects
    private float startX, startY; // Starting points for touch events

    // Default constructor used when instantiated programmatically
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

    // Another constructor variation used in some cases
    public SnakeGameController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    // Initialization method for setting up game components
    private void init(AttributeSet attrs, int defStyleAttr) {
        surfaceHolder = getHolder();
        snakeGame = new SnakeGame();
        paint = new Paint();
    }

    // Lifecycle method called when surface is created, starting the game loop
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        resume(); // Start the game loop when the surface is created
    }

    // Lifecycle method called when surface changes, could be used for handling size changes
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Handle changes if necessary
    }

    // Lifecycle method called when surface is destroyed, pausing the game loop
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        pause(); // Stop the game loop when the surface is destroyed
    }

    // Main game loop
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
                            drawGame(canvas); // Draw game elements
                        }
                    }
                } finally {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
            snakeGame.updateGame(); // Update game state
            if (snakeGame.isGameOver()) {
                playing = false;
                // Show game over message
                post(() -> Toast.makeText(getContext(), "Game Over! Tap to restart.", Toast.LENGTH_LONG).show());
            }
            try {
                Thread.sleep(200); // Control game speed
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Method for drawing game elements on the canvas
    private void drawGame(Canvas canvas) {
        canvas.drawColor(Color.BLACK); // Clear screen
        paint.setColor(Color.GREEN); // Set color for snake
        // Draw each point of the snake
        for (SnakeGame.Point point : snakeGame.getSnake()) {
            canvas.drawRect(point.getX() * 20, point.getY() * 20,
                    (point.getX() + 1) * 20, (point.getY() + 1) * 20, paint);
        }
        paint.setColor(Color.RED); // Set color for apple
        // Draw the apple
        SnakeGame.Point apple = snakeGame.getApple();
        canvas.drawRect(apple.getX() * 20, apple.getY() * 20,
                (apple.getX() + 1) * 20, (apple.getY() + 1) * 20, paint);

        // Draw the score
        paint.setColor(Color.WHITE);
        paint.setTextSize(40);
        canvas.drawText("Score: " + snakeGame.getScore(), 10, 30, paint);
    }

    // Method to handle touch events for game control
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        if (snakeGame.isGameOver()) {
            // Restart the game on tap after game over
            if (action == MotionEvent.ACTION_DOWN) {
                return true;
            } else if (action == MotionEvent.ACTION_UP) {
                restartGame();
                return true;
            }
        } else {
            // Control snake direction based on swipe gestures
            if (action == MotionEvent.ACTION_DOWN) {
                startX = event.getX();
                startY = event.getY();
                return true;
            } else if (action == MotionEvent.ACTION_UP) {
                float endX = event.getX();
                float endY = event.getY();

                float deltaX = endX - startX;
                float deltaY = endY - startY;

                // Determine direction based on the swipe
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

    // Method to restart the game, resetting score and game state
    public void restartGame() {
        playing = true;
        snakeGame.resetScore();
        snakeGame.restartGame();
        if (gameThread != null) {
            gameThread.interrupt(); // Interrupt the old thread if it's still running
        }
        gameThread = new Thread(this); // Create a new game thread
        gameThread.start(); // Start the game loop again
    }

    // Resume the game, starting the game thread
    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    // Pause the game, stopping the game thread
    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


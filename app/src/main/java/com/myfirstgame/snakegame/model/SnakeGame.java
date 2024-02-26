package com.myfirstgame.snakegame.model;

import java.util.ArrayList;
import java.util.Random;

// Main game class for the Snake game
public class SnakeGame {
    // Constants for the width and height of the game area
    public static final int WIDTH = 49;
    public static final int HEIGHT = 100;
    // Current score in the game
    public int score;
    // ArrayList to store the points of the snake's body
    private ArrayList<Point> snake;
    // Position of the apple
    private Point apple;
    // Random number generator for apple placement
    private Random random = new Random();
    // Game over status
    private boolean gameOver = false;
    // Current direction of the snake's movement
    private Direction direction = Direction.RIGHT;

    // Constructor to initialize the game
    public SnakeGame() {
        resetGame();
        resetScore();
    }

    // Method to update the game state
    public void updateGame() {
        try {
            if (!gameOver) {
                moveSnake();
                checkCollision();

                if (checkIfSnakeEatsApple()) {
                    score++;
                    generateApple();
                }
            }
        } catch (Exception e) {
            gameOver = true;
        }
    }

    // Getter for the score
    public int getScore() {
        return score;
    }

    // Method to reset the score
    public void resetScore() {
        score = 0;
    }

    // Method to reset the game to its initial state
    private void resetGame() {
        snake = new ArrayList<>();
        snake.add(new Point(0, 0)); // Starting point of the snake
        generateApple(); // Place the first apple
        gameOver = false; // Reset game over status
        direction = Direction.RIGHT; // Starting direction
    }

    // Method to restart the game
    public void restartGame() {
        resetGame(); // Resets the game to its initial state
    }

    // Method to move the snake
    private void moveSnake() {
        Point newHead = getNextHead(); // Calculate new head position
        snake.add(0, newHead); // Add new head to the snake

        if (newHead.equals(apple)) { // Check if the snake eats the apple
            score++;
            generateApple(); // Generate a new apple
        } else {
            snake.remove(snake.size() - 1); // Remove the tail if not eating an apple
        }
    }

    // Method to get the next head position based on the current direction
    private Point getNextHead() {
        Point head = snake.get(0);
        switch (direction) {
            case UP:
                return new Point(head.getX(), head.getY() - 1);
            case DOWN:
                return new Point(head.getX(), head.getY() + 1);
            case LEFT:
                return new Point(head.getX() - 1, head.getY());
            case RIGHT:
                return new Point(head.getX() + 1, head.getY());
            default:
                return head;
        }
    }

    // Method to check for collisions with the wall or the snake itself
    private void checkCollision() {
        Point head = snake.get(0);

        // Check wall collision
        if (head.getX() < 0 || head.getX() >= WIDTH || head.getY() < 0 || head.getY() >= HEIGHT) {
            gameOver = true;
            return;
        }

        // Check self collision
        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                gameOver = true;
                return;
            }
        }
    }

    // Check if the snake's head is on the apple
    private boolean checkIfSnakeEatsApple() {
        Point head = snake.get(0);
        return head.equals(apple);
    }

    // Generate a new apple position not occupied by the snake
    private void generateApple() {
        int x, y;
        do {
            x = random.nextInt(WIDTH);
            y = random.nextInt(HEIGHT);
            apple = new Point(x, y);
        } while (snake.contains(apple));
    }

    // Setter for the snake's direction with logic to prevent reverse movement
    public void setDirection(Direction newDirection) {
        if ((direction == Direction.UP && newDirection != Direction.DOWN) ||
                (direction == Direction.DOWN && newDirection != Direction.UP) ||
                (direction == Direction.LEFT && newDirection != Direction.RIGHT) ||
                (direction == Direction.RIGHT && newDirection != Direction.LEFT)) {
            direction = newDirection;
        }
    }

    // Getter for the snake's body
    public ArrayList<Point> getSnake() {
        return snake;
    }

    // Getter for the apple's position
    public Point getApple() {
        return apple;
    }

    // Check if the game is over
    public boolean isGameOver() {
        return gameOver;
    }

    // Direction enum for the snake's movement
    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    // Point class to represent positions on the board
    public static class Point {
        private final int x;
        private final int y;

        // Constructor for Point
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        // Getter for x-coordinate
        public int getX() {
            return x;
        }

        // Getter for y-coordinate
        public int getY() {
            return y;
        }

        // Equals method to compare two points
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Point point = (Point) obj;
            return x == point.x && y == point.y;
        }

        // Hashcode method for use in collections
        @Override
        public int hashCode() {
            int result = 17;
            result = 31 * result + x;
            result = 31 * result + y;
            return result;
        }
    }
}

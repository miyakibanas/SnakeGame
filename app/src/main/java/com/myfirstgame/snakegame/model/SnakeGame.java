package com.myfirstgame.snakegame.model;

import java.util.ArrayList;
import java.util.Random;

public class SnakeGame {
    public static final int WIDTH = 49;
    public static final int HEIGHT = 100;
    public int score;
    private ArrayList<Point> snake;
    private Point apple;
    private Random random = new Random();
    private boolean gameOver = false;
    private Direction direction = Direction.RIGHT;

    public SnakeGame() {
        resetGame();
        resetScore();
    }

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

    public int getScore() {
        return score;
    }
    public void resetScore() {
        score = 0;
    }
    private void resetGame() {
        snake = new ArrayList<>();
        snake.add(new Point(0, 0));
        generateApple();
        gameOver = false;
        direction = Direction.RIGHT;
    }
    public void restartGame() {
        resetGame(); // Resets the game to its initial state
    }


    private void moveSnake() {
        Point newHead = getNextHead();
        snake.add(0, newHead);

        if (newHead.equals(apple)) {
            score++;
            generateApple();
        } else {
            snake.remove(snake.size() - 1);
        }
    }

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

    private void checkCollision() {
        Point head = snake.get(0);

        if (head.getX() < 0 || head.getX() >= WIDTH || head.getY() < 0 || head.getY() >= HEIGHT) {
            gameOver = true;
            return;
        }

        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                gameOver = true;
                return;
            }
        }
    }

    private boolean checkIfSnakeEatsApple() {
        Point head = snake.get(0);
        return head.equals(apple);
    }

    private void generateApple() {
        int x, y;
        do {
            x = random.nextInt(WIDTH);
            y = random.nextInt(HEIGHT);
            apple = new Point(x, y);
        } while (snake.contains(apple));
    }

    public void setDirection(Direction newDirection) {
        if ((direction == Direction.UP && newDirection != Direction.DOWN) ||
                (direction == Direction.DOWN && newDirection != Direction.UP) ||
                (direction == Direction.LEFT && newDirection != Direction.RIGHT) ||
                (direction == Direction.RIGHT && newDirection != Direction.LEFT)) {
            direction = newDirection;
        }
    }

    public ArrayList<Point> getSnake() {
        return snake;
    }

    public Point getApple() {
        return apple;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public static class Point {
        private final int x;
        private final int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Point point = (Point) obj;
            return x == point.x && y == point.y;
        }

        @Override
        public int hashCode() {
            return 31 * x + y;
        }
    }
}

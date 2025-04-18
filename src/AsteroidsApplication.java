import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class AsteroidsApplication extends Application {
    public static int WIDTH = 500;
    public static int HEIGHT = 400;
    private long lastShotTime = 0;
    private static final long SHOOT_COOLDOWN = 200_000_000;

    @Override
    public void start(Stage stage)  {
        Pane pane = new Pane();
        pane.setPrefSize(WIDTH, HEIGHT);
        Text text = new Text(10, 20, "Points: 0");
        pane.getChildren().add(text);

        AtomicInteger points = new AtomicInteger();

        Ship ship = new Ship(WIDTH/2, HEIGHT/2);
        List<Asteroid> asteroids = new ArrayList<>();
        List<Projectile> projectiles = new ArrayList<>();

        pane.getChildren().add(ship.getCharacter());
        asteroids.forEach(asteroid -> pane.getChildren().add(asteroid.getCharacter()));


        Scene scene = new Scene(pane);
        stage.setTitle("Asteroids!");
        stage.setScene(scene);
        stage.show();

        Map<KeyCode, Boolean> pressedKeys = new HashMap<>();

        scene.setOnKeyPressed(event -> {
            pressedKeys.put(event.getCode(), Boolean.TRUE);
        });
        scene.setOnKeyReleased(event -> {
            pressedKeys.put(event.getCode(), Boolean.FALSE);
        });


        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (pressedKeys.getOrDefault(KeyCode.LEFT, false)) {
                    ship.turnLeft();
                }
                if (pressedKeys.getOrDefault(KeyCode.RIGHT, false)) {
                    ship.turnRight();
                }
                if (pressedKeys.getOrDefault(KeyCode.UP, false)) {
                    ship.accelerate();
                }
                if (pressedKeys.getOrDefault(KeyCode.DOWN, false)) {
                    ship.decelerate();
                }

                if (pressedKeys.getOrDefault(KeyCode.SPACE, false)) {
                    if (now - lastShotTime > SHOOT_COOLDOWN) {
                        Projectile projectile = new Projectile(
                                (int) ship.getCharacter().getTranslateX(),
                                (int) ship.getCharacter().getTranslateY()
                        );
                        projectile.getCharacter().setRotate(ship.getCharacter().getRotate());
                        projectiles.add(projectile);
                        pane.getChildren().add(projectile.getCharacter());

                        double directionX = Math.cos(Math.toRadians(projectile.getCharacter().getRotate()));
                        double directionY = Math.sin(Math.toRadians(projectile.getCharacter().getRotate()));
                        projectile.setMovement(new Point2D(directionX, directionY).normalize().multiply(5));

                        lastShotTime = now;
                    }
                }

                ship.move();
                asteroids.forEach(Asteroid::move);
                projectiles.forEach(Projectile::move);

                projectiles.forEach(projectile -> {
                    asteroids.forEach(asteroid -> {
                        if (projectile.collide(asteroid)) {
                            projectile.setAlive(false);
                            asteroid.setAlive(false);
                            points.addAndGet(100);
                            text.setText("Points: " + points.get());
                        }
                    });
                });

                projectiles.forEach(projectile -> {
                    double x = projectile.getCharacter().getTranslateX();
                    double y = projectile.getCharacter().getTranslateY();
                    if (x < 0 || x > WIDTH || y < 0 || y > HEIGHT) {
                        projectile.setAlive(false);
                    }
                });

                removeDeadCharacters(projectiles, pane);
                removeDeadCharacters(asteroids, pane);

                if (Math.random() < 0.009) {
                    Asteroid asteroid = new Asteroid(WIDTH, HEIGHT);
                    if (!asteroid.collide(ship)) {
                        asteroids.add(asteroid);
                        pane.getChildren().add(asteroid.getCharacter());
                    }
                }
                asteroids.forEach(asteroid -> {
                    if (ship.collide(asteroid)) {
                        stop();
                    }
                });
            }
        }.start();
    }
    private void removeDeadCharacters(List<? extends Character> characters, Pane pane){
        characters.stream().filter(character -> !character.isAlive())
                .forEach(dead -> pane.getChildren().remove(dead.getCharacter()));
        characters.removeAll(
                characters.stream().filter(character -> !character.isAlive())
                        .collect(Collectors.toList())
        );
    }


    public static void main(String[] args) {
        launch(args);
    }
}
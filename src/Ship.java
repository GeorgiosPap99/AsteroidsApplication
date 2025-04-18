import javafx.scene.shape.Polygon;

public class Ship extends Character {
    public Ship(int x, int y) {
        super(createShipPolygon(), x, y);
    }

    private static Polygon createShipPolygon() {
        Polygon polygon = new Polygon();
        polygon.getPoints().addAll(
                -5.0, -5.0,
                10.0, 0.0,
                -5.0, 5.0
        );
        return polygon;
    }
    @Override
    public void move() {
        super.move();

        double x = this.getCharacter().getTranslateX();
        double y = this.getCharacter().getTranslateY();

        if (x < 0) {
            this.getCharacter().setTranslateX(AsteroidsApplication.WIDTH);
        }
        if (x > AsteroidsApplication.WIDTH) {
            this.getCharacter().setTranslateX(0);
        }
        if (y < 0) {
            this.getCharacter().setTranslateY(AsteroidsApplication.HEIGHT);
        }
        if (y > AsteroidsApplication.HEIGHT) {
            this.getCharacter().setTranslateY(0);
        }
    }
    public void decelerate(){
        double reverseX = Math.cos(Math.toRadians(this.getCharacter().getRotate()));
        double reverseY = Math.sin(Math.toRadians(this.getCharacter().getRotate()));
        reverseX *= -0.05;
        reverseY *= -0.05;
        this.setMovement(this.getMovement().add(reverseX, reverseY));
    }
}

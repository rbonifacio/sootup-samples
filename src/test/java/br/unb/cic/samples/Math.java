package br.unb.cic.samples;

/**
 * Test class that is passed in as input to Driver.execute
 */
public class Math {
    private final double radius;
    public static final double pi = 3.14;

    public Math(double radius) {
        this.radius = radius;
    }

    public double circleArea() {
        if (this.radius < 0) {
            throw new RuntimeException("Radius must be positive");
        }
        return pi * this.radius * this.radius;
    }

    public int div(int x, int y) {
        if (y == 0) {
            throw new RuntimeException("Invalid arguments");
        }
        return x / y;
    }

    public double probability(int p) {
        if (p < 0 || p > 1) {
            throw new RuntimeException("probability is out of bounds");
        }
        return p;
    }
}

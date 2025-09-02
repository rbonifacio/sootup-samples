package br.unb.cic.samples;

public class Math {
    private int c;

    public int div(int x, int y) {
        if(y == 0) {
            throw new RuntimeException("Invalid arguments");
        }
        return x / y;
    }
}

package org.firstinspires.ftc.teamcode;

public class LookupTable {
    double[] speeds;
    int stepSize;
    int offset;

    public LookupTable(double[] speeds, int stepSize, int offset) {
        this.offset = offset;
        this.stepSize = stepSize;
        this.speeds = speeds;
    }

    public double getSpeed(double distance) {
        long index = Math.round((distance - offset) / stepSize);
        return speeds[(int)index];
    }
}

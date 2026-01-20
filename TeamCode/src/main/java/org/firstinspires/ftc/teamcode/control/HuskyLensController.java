package org.firstinspires.ftc.teamcode.control;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class HuskyLensController {
    private static final int HUSKYLENS_WIDTH_PIXELS = 320;
    private static final int HUSKYLENS_HEIGHT_PIXELS = 240;
    private static final double HUSKYLENS_FOV_HORIZONTAL_DEGREES = 49.12;
    private static final double HUSKYLENS_FOV_VERTICAL_DEGREES = 38.69;

    private HuskyLens huskyLens;

    public void init(HardwareMap hardwareMap, Telemetry telemetry) {
        huskyLens = hardwareMap.get(HuskyLens.class, "huskyLens");
        huskyLens.selectAlgorithm(HuskyLens.Algorithm.COLOR_RECOGNITION);
    }

    public HuskyLensResult getTrackingResult() {
        HuskyLens.Block[] blocks = huskyLens.blocks();
        HuskyLens.Block block = null;
        if (blocks != null && blocks.length > 0) {
            int largestArea = 0;
            for (HuskyLens.Block value : blocks) {
                if (value.width * value.height > largestArea) {
                    largestArea = value.width * value.height;
                    block = value;
                }
            }
        }
        return new HuskyLensResult(block);
    }

    public static class HuskyLensResult {
        public boolean isValid;
        public double tx;
        public double ty;
        public double ta;

        public HuskyLensResult(HuskyLens.Block block) {
            if (block != null) {
                isValid = true;
                tx = ((block.x - HUSKYLENS_WIDTH_PIXELS / 2.0) * HUSKYLENS_FOV_HORIZONTAL_DEGREES) / (HUSKYLENS_WIDTH_PIXELS);
                ty = ((block.y - HUSKYLENS_HEIGHT_PIXELS / 2.0) * HUSKYLENS_FOV_VERTICAL_DEGREES) / (HUSKYLENS_HEIGHT_PIXELS);
                ta = (double)(block.x * block.y)/(HUSKYLENS_HEIGHT_PIXELS * HUSKYLENS_HEIGHT_PIXELS);
            }
        }
    }
}

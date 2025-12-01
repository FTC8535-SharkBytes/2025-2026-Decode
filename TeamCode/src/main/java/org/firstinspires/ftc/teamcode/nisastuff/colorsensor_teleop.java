package org.firstinspires.ftc.teamcode.nisastuff;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import android.graphics.Color;

@TeleOp(name = "ColorSensor_TeleOp")
public class colorsensor_teleop extends LinearOpMode {

    private ColorSensor color1;
    // private ColorSensor color2;
    // private ColorSensor color3;

    @Override
    public void runOpMode() {

        // get sensors from Hardware Map
        color1 = hardwareMap.get(ColorSensor.class, "color1");
//        color2 = hardwareMap.get(ColorSensor.class, "color2");
//        color3 = hardwareMap.get(ColorSensor.class, "color3");

        float[] hsv1 = new float[3];
//        float[] hsv2 = new float[3];
//        float[] hsv3 = new float[3];

        waitForStart();

        while (opModeIsActive()) {

            // convert to HSV for each sensor
            Color.RGBToHSV(color1.red() * 8, color1.green() * 8, color1.blue() * 8, hsv1);
//            Color.RGBToHSV(color2.red() * 8, color2.green() * 8, color2.blue() * 8, hsv2);
//            Color.RGBToHSV(color3.red() * 8, color3.green() * 8, color3.blue() * 8, hsv3);

            telemetry.addLine("=== SENSOR 1 ===");
            telemetry.addData("Hsv0", hsv1[0]);
            telemetry.addData("Hsv1", hsv1[1]);
            telemetry.addData("Hsv2", hsv1[2]);

            detectColor(hsv1);

//            telemetry.addLine("=== SENSOR 2 ===");
//            detectColor(hsv2);
//
//            telemetry.addLine("=== SENSOR 3 ===");
//            detectColor(hsv3);

            telemetry.update();
        }
    }

    private void detectColor(float[] hsv) {
        float hue = hsv[0];
        telemetry.addData("Hue", hue);

        if (isPurple(hue)) {
            telemetry.addLine("Detected: PURPLE");
        } else if (isGreen(hue)) {
            telemetry.addLine("Detected: GREEN");
        } else {
            telemetry.addLine("Detected: NONE");
        }
    }

    private boolean isPurple(float hue) {
        // typical purple hue range
        return hue >= 215 && hue <= 345;
    }

    private boolean isGreen(float hue) {
        // typical green hue range
        return hue >= 155 && hue <= 170;
    }
}

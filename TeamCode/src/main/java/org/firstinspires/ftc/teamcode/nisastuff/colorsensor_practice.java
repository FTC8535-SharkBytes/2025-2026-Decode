package org.firstinspires.ftc.teamcode.nisastuff;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import java.util.Locale;

@TeleOp(name="Color Sensor Practice", group = "Sensor")
public class colorsensor_practice extends LinearOpMode {

    NormalizedColorSensor colorSensor;

    @Override
    public void runOpMode() {
        // Initialize the color sensor from the hardware map
        colorSensor = hardwareMap.get(NormalizedColorSensor.class, "colorSensor");

        telemetry.addLine("Color Sensor Initialized");
        telemetry.update();

        // Wait for the driver to press play
        waitForStart();

        while (opModeIsActive()) {
            // Read normalized color values
            NormalizedRGBA colors = colorSensor.getNormalizedColors();

            // Prepare HSV array for hue calculation
            float[] hsvValues = new float[3];
            android.graphics.Color.colorToHSV(colors.toColor(), hsvValues);

            // Display RGB & alpha values
            telemetry.addData("Red", String.format(Locale.US, "%.3f", colors.red));
            telemetry.addData("Green", String.format(Locale.US, "%.3f", colors.green));
            telemetry.addData("Blue", String.format(Locale.US, "%.3f", colors.blue));
            telemetry.addData("Alpha", String.format(Locale.US, "%.3f", colors.alpha));

            // Display hue value
            telemetry.addData("Hue", String.format(Locale.US, "%.1f", hsvValues[0]));

            // Simple color detection
            if (colors.red > colors.green && colors.red > colors.blue && colors.red > 0.3) {
                telemetry.addLine("I see RED!");
            } else if (colors.blue > colors.red && colors.blue > colors.green && colors.blue > 0.3) {
                telemetry.addLine("I see BLUE!");
            } else if (colors.green > colors.red && colors.green > colors.blue && colors.green > 0.3) {
                telemetry.addLine("I see GREEN!");
            } else {
                telemetry.addLine("No strong color detected");
            }

            // Update telemetry
            telemetry.update();
        }
    }
}
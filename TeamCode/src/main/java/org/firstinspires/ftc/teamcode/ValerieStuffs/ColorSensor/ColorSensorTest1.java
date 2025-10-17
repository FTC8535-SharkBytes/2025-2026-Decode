package org.firstinspires.ftc.teamcode.ValerieStuffs.ColorSensor;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@TeleOp(name = "Sensor: REV2mDistance", group = "Sensor")
public class ColorSensorTest1 extends LinearOpMode {
    public void waitForStart() {
        super.waitForStart();
    }

    //private RevColorSensorV3 colorSensorV3;
    NormalizedColorSensor colorSensor;

    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();
    }
    public enum DetectedColor{
        GREEN,
        PURPLE,
        UNKNOWN
    }
    public void init(HardwareMap hardwareMap){
        colorSensor = hardwareMap.get(NormalizedColorSensor.class, "sensor_color");
    }
    public ColorSensorTest2_1.DetectedColor getDetectedColor(Telemetry telemetry){
        NormalizedRGBA colors = colorSensor.getNormalizedColors();
        float normRed = colors.red / colors.alpha;
        float normGreen = colors.green / colors.alpha;
        float normBlue = colors.blue / colors.alpha;
        telemetry.addData("red", normRed);
        telemetry.addData("blue", normBlue);
        telemetry.addData("green", normGreen);
        //Add calibration values for specific colors purple and green
        return ColorSensorTest2_1.DetectedColor.UNKNOWN;
    }
//
//    @Override
//    public void runOpMode() {
//        // you can use this as a regular DistanceSensor.
//        colorSensorV3 = hardwareMap.get(RevColorSensorV3.class, "sensor_color");
//
//        // you can also cast this to a Rev2mDistanceSensor if you want to use added
//        // methods associated with the Rev2mDistanceSensor class
//
//        telemetry.addData(">>", "Press start to continue");
//        telemetry.update();
//
//        waitForStart();
//        while(opModeIsActive()) {
//            // generic DistanceSensor methods.
//            NormalizedRGBA normalizedRGBA = colorSensorV3.getNormalizedColors();
////            telemetry.addData("deviceName", colorSensorV3.getDeviceName() );
////            telemetry.addData("red", String.format("%.01f mm",  normalizedRGBA.red * 10));
////            telemetry.addData("green", String.format("%.01f mm", normalizedRGBA.green * 10));
////            telemetry.addData("blue", String.format("%.01f mm", normalizedRGBA.blue * 10));
//            telemetry.addData("deviceName", colorSensorV3.getDeviceName());
//            telemetry.addData("Red", normalizedRGBA.red);
//            telemetry.addData("Blue", normalizedRGBA.blue);
//            telemetry.addData("Green", normalizedRGBA.green);
//            telemetry.update();
//        }
//    }

}
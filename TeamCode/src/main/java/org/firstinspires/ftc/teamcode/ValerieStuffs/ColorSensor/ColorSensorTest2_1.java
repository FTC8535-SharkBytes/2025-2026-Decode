package org.firstinspires.ftc.teamcode.ValerieStuffs.ColorSensor;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import org.firstinspires.ftc.robotcore.external.Telemetry;
@TeleOp
@Disabled
public class ColorSensorTest2_1 {
    NormalizedColorSensor colorSensor;
    public enum DetectedColor{
        GREEN,
        PURPLE,
        UNKNOWN
    }
    public void init(HardwareMap hardwareMap){
        colorSensor = hardwareMap.get(NormalizedColorSensor.class, "sensor_color");
    }
    public DetectedColor getDetectedColor(Telemetry telemetry){
        NormalizedRGBA colors = colorSensor.getNormalizedColors();
        float normRed = colors.red / colors.alpha;
        float normGreen = colors.green / colors.alpha;
        float normBlue = colors.blue / colors.alpha;
        telemetry.addData("red", normRed);
        telemetry.addData("blue", normBlue);
        telemetry.addData("green", normGreen);
        //Add calibration values for specific colors purple and greend
        return DetectedColor.UNKNOWN;
    }
}

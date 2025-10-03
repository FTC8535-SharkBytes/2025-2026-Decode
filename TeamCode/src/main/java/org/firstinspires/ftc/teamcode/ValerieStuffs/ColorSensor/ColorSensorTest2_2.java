package org.firstinspires.ftc.teamcode.ValerieStuffs.ColorSensor;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class ColorSensorTest2_2 extends LinearOpMode{
    ColorSensorTest2_1 colorSensorTest2_1 = new ColorSensorTest2_1();
    public void init(){
        colorSensorTest2_1.init(hardwareMap);
    }
    public void loop(){
        colorSensorTest2_1.getDetectedColor(telemetry);
    }
}

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Prism.Color;
import org.firstinspires.ftc.teamcode.control.LEDController;

@TeleOp
public class LEDTestOpMode extends LinearOpMode {
    LEDController ledController = new LEDController();
    @Override
    public void runOpMode() throws InterruptedException {
        ledController.init(hardwareMap, telemetry);
        waitForStart();
        ledController.setLedColor(0, Color.ORANGE);

        while (opModeIsActive()){


        }
    }
}

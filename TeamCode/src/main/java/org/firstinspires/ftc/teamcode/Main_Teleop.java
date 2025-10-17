package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.nisaandvalerie.mechanumstuffthings.DrivingControllerThingyYay;

@TeleOp
public class Main_Teleop extends LinearOpMode {

    private final DrivingControllerThingyYay driveControllerThingy = new DrivingControllerThingyYay();

    boolean clawClosed = true;

    @Override
    public void runOpMode() {

        driveControllerThingy.init(hardwareMap, telemetry, false);

        waitForStart();


        while (opModeIsActive()) {


            // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
            double axial   = -gamepad1.left_stick_y;  // Note: pushing stick forward gives negative value
            double lateral =  gamepad1.left_stick_x;
            double yaw     =  gamepad1.right_stick_x;

            boolean isFastMode = (gamepad1.right_trigger != 1);

            driveControllerThingy.updateDriveCommands(axial, lateral, yaw, isFastMode);
        }

    }

}
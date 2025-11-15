/* Copyright (c) 2017 FIRST. All rights reserved. */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="Basic: Linear OpMode", group="Linear OpMode")

public class ShooterOpMode extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotorEx shooterMotor = null;
    private DcMotor bellyMotor = null;
    private double desiredVelocity = 0.9*100*28;


    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        shooterMotor = hardwareMap.get(DcMotorEx.class, "shooter_motor");
        bellyMotor = hardwareMap.get(DcMotor.class, "belly_motor");

        // To drive forward, most robots need the motor on one side to be reversed, because the axles point in opposite directions.
        // Pushing the left stick forward MUST make robot go forward. So adjust these two lines based on your first test drive.
        // Note: The settings here assume direct drive on left and right wheels.  Gear Reduction or 90 Deg drives may require direction flips
        shooterMotor.setDirection(DcMotorEx.Direction.REVERSE);
        shooterMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        bellyMotor.setDirection(DcMotorSimple.Direction.REVERSE);



        // Wait for the game to start (driver presses START)
        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        boolean bIsPressed = false;
        boolean xIsPressed = false;
        while (opModeIsActive()) {



            if (gamepad2.y) {
                shooterMotor.setVelocity(desiredVelocity);
            }
            if (gamepad2.a) {
                shooterMotor.setVelocity(0);
            }
            //increases velocity
            if (gamepad2.x && !xIsPressed){
                xIsPressed = true;
                desiredVelocity += 100;
            }
            if (!gamepad2.x && xIsPressed) {
                xIsPressed = false;
            }
            //decreases velocity
            if (gamepad2.b && !bIsPressed){
                bIsPressed = true;
                desiredVelocity -= 100;
            }
            if (!gamepad2.b && bIsPressed){
                bIsPressed = false;
            }
            if (gamepad2.dpad_up){
                bellyMotor.setPower(1);
            }
            if (gamepad2.dpad_down){
                bellyMotor.setPower(0);
            }




            // Show the elapsed game time and wheel power.
                telemetry.addData("Status", "Run Time: " + runtime.toString());
                telemetry.addData("Velocity", shooterMotor.getVelocity());
                telemetry.update();
        }
    }
}

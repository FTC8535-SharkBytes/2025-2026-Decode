package org.firstinspires.ftc.teamcode.nisastuff;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Servo_Motors")
public class Servo_Motors extends LinearOpMode {

    // declare servos
    private Servo leftIntake;
    private Servo rightIntake;
    private Servo shooterHood;
    private Servo feeder;

    // define servo positions
    private final double LEFTINTAKE_UP = 0.25                       ;
    private final double LEFTINTAKE_DOWN = 0.0;
    private final double RIGHTINTAKE_UP = 0.0;
    private final double RIGHTINTAKE_DOWN = 0.25;
    private final double SHOOTER_HOOD_UP = 1.0;
    private final double SHOOTER_HOOD_DOWN = 0;
    private final double FEEDER_UP = 1.0;
    private final double FEEDER_DOWN = 0.0;

    @Override
    public void runOpMode() {
        // map servos to hardware names from the configuration FIX THISs
        leftIntake = hardwareMap.get(Servo.class, "servo_left_intake");
        rightIntake = hardwareMap.get(Servo.class, "servo_right_intake");
        shooterHood = hardwareMap.get(Servo.class, "servo_left_outtake");
        feeder = hardwareMap.get(Servo.class, "servo_right_outtake");

        leftIntake.setPosition(LEFTINTAKE_DOWN);
        rightIntake.setPosition(RIGHTINTAKE_DOWN);
        shooterHood.setPosition(SHOOTER_HOOD_DOWN);
        feeder.setPosition(FEEDER_DOWN);

        telemetry.addLine("Ready to start");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            // Intake controls:
            if (gamepad1.x) { // X --> move left intake up
                leftIntake.setPosition(LEFTINTAKE_UP);
                rightIntake.setPosition(RIGHTINTAKE_UP);

            }
            else if (gamepad1.y) { // Y --> move left intake down
                leftIntake.setPosition(LEFTINTAKE_DOWN);
                rightIntake.setPosition(RIGHTINTAKE_DOWN);

            }

            if (gamepad1.a) { // A --> move right intake up
                feeder.setPosition(FEEDER_UP);
            }
            else if (gamepad1.b) { // B --> move outtake down
                feeder.setPosition(FEEDER_DOWN);
            }

            if (gamepad1.dpad_up) { // dpad up --> move shooter up
                shooterHood.setPosition(SHOOTER_HOOD_UP);
            }
            else if (gamepad1.dpad_down) { // dpad down --> move shooter down
                shooterHood.setPosition(SHOOTER_HOOD_DOWN);
            }

            // Telemetry for testing
            telemetry.addData("Left Intake", leftIntake.getPosition());
            telemetry.addData("Right Intake", rightIntake.getPosition());
            telemetry.addData("Left Outtake", shooterHood.getPosition());
            telemetry.addData("Right Outtake", feeder.getPosition());
            telemetry.update();
        }
    }
}
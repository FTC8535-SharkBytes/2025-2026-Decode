package org.firstinspires.ftc.teamcode.control;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

public class BellyTurning extends LinearOpMode{

    private DcMotorEx motor;

    // each press =+ 96 ticks/120 degrees
    private static final int INCREMENT = 96;

    // max = 288 ticks (360 degrees)
    private static final int MAX_POSITION = 288;

    private int targetPosition = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        motor = hardwareMap.get(DcMotorEx.class, "idkthemotorname");

        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPower(0.5);

        boolean yPressedLast = false;

        waitForStart();

        while(opModeIsActive()) {
            boolean yPressedNow = gamepad1.y;

            //detect the button press
            if(yPressedNow && !yPressedLast){
                targetPosition += INCREMENT;

                // max position
                if (targetPosition > MAX_POSITION){
                    targetPosition = MAX_POSITION;
                }

                motor.setTargetPosition(targetPosition);
            }

            yPressedLast = yPressedNow;

            telemetry.addData("Target Position ", targetPosition);
            telemetry.addData("Current Position ", motor.getCurrentPosition());
            telemetry.update();
        }
    }
}

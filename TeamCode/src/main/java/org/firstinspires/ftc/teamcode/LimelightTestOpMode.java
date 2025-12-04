package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.control.LimelightController;

@TeleOp
public class LimelightTestOpMode extends LinearOpMode {
    private final LimelightController limelightController = new LimelightController();

    int pipeline = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        limelightController.init(hardwareMap, telemetry);

        waitForStart();
        limelightController.switchPipeline(pipeline);

        while (opModeIsActive()) {

            if (gamepad1.a) {
                pipeline = 0;
                limelightController.switchPipeline(pipeline);
            } else if (gamepad1.x) {
                pipeline = 1;
                limelightController.switchPipeline(pipeline);
            } else if (gamepad1.b) {
                pipeline = 2;
                limelightController.switchPipeline(pipeline);
            }
            if (pipeline == 0) {
                limelightController.detectPattern();
            } else {
                limelightController.getLimelightTrackingResult();
            }
            telemetry.update();
        }

    }
}

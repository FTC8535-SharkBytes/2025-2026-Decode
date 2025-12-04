package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.control.DrivingController;
import org.firstinspires.ftc.teamcode.control.LimelightController;
import org.firstinspires.ftc.teamcode.control.MechanismController;
import org.firstinspires.ftc.teamcode.navigation.DriveToPoint;

/** @noinspection unused*/
@Autonomous(name="AutoRedShootAndMove", group="Autonomous", preselectTeleOp = "Main_Teleop")
public class AutoRedShootAndMove extends LinearOpMode {
    private final DrivingController driveController = new DrivingController();
    private final MechanismController mechanismController = MechanismController.getInstance();
    private final LimelightController limelightController = new LimelightController();
    private final ElapsedTime runtime = new ElapsedTime();

    DriveToPoint nav = new DriveToPoint(this); //OpMode member for the point-to-point navigation class
    boolean navUsingOdometry = false;

    enum StateMachine {
        WAITING_FOR_START,
        DRIVE_TO_SHOOTER_1,
        AIM_SHOOTER_1,
        WAIT_FOR_SHOTS_1,
        DRIVE_TO_PARK,
        AT_TARGET
    }

    static final Pose2D TARGET_SHOOT_1 = new Pose2D(DistanceUnit.MM,10,0,AngleUnit.DEGREES,30);
    static final Pose2D TARGET_PARK = new Pose2D(DistanceUnit.MM,0, 200, AngleUnit.DEGREES, 0);

    @Override
    public void runOpMode() {
        driveController.init(hardwareMap, telemetry, true);
        mechanismController.init(hardwareMap, telemetry, true);
        limelightController.init(hardwareMap, telemetry);

        nav.setDriveType(DriveToPoint.DriveType.MECANUM);

        StateMachine stateMachine;
        stateMachine = StateMachine.WAITING_FOR_START;

        // Wait for the game to start (driver presses START)
        waitForStart();
        resetRuntime();
        limelightController.switchPipeline(0);

        while (opModeIsActive()) {
            driveController.updateOdometry();
            mechanismController.update();

            switch (stateMachine) {
                case WAITING_FOR_START:
                    //the first step in the autonomous
                    stateMachine = StateMachine.DRIVE_TO_SHOOTER_1;
                    mechanismController.startShooter();
                    mechanismController.setHoodUp();
                    runtime.reset();
                    break;
                case DRIVE_TO_SHOOTER_1:
                    navUsingOdometry = true;
                    if (nav.driveTo(driveController.getOdometryPosition(), TARGET_SHOOT_1, 0.45, 0)){
                        //drive to the submersible
                        telemetry.addLine("at position #1!");
                        stateMachine = StateMachine.AIM_SHOOTER_1;
                        runtime.reset();
                        navUsingOdometry = false;
                    }
                    break;
                case AIM_SHOOTER_1:
                    // TODO Add aiming with limelight
                    if (mechanismController.isShooterAtSpeed()) {
                        // Rotate belly 3 times to shoot all artifacts
                        mechanismController.rotateBelly();
                        mechanismController.rotateBelly();
                        mechanismController.rotateBelly();
                        stateMachine = StateMachine.WAIT_FOR_SHOTS_1;
                        runtime.reset();
                    }
                    break;
                case WAIT_FOR_SHOTS_1:
                    if (mechanismController.isBellyAtTarget()) {
                        stateMachine = StateMachine.DRIVE_TO_PARK;
                        runtime.reset();
                    }
                    break;
                case DRIVE_TO_PARK:
                    // wait a half second before moving to let shooting finish
                    if (runtime.seconds() >= 0.5) {
                        mechanismController.stopShooter();
                        mechanismController.setHoodDown();
                        navUsingOdometry = true;
                        if (nav.driveTo(driveController.getOdometryPosition(), TARGET_PARK, 0.7, 0)) {
                            telemetry.addLine("at position #2!");
                            stateMachine = StateMachine.AT_TARGET;
                            navUsingOdometry = false;
                        }
                    }
                    break;

            }

            DrivingController.WheelPower wheelPower;
            if (!navUsingOdometry || stateMachine == StateMachine.AT_TARGET){
                wheelPower = new DrivingController.WheelPower(0, 0, 0, 0);
            } else {
                //nav calculates the power to set to each motor in a mecanum or tank drive. Use nav.getMotorPower to find that value.
                wheelPower = new DrivingController.WheelPower(
                        nav.getMotorPower(DriveToPoint.DriveMotor.LEFT_FRONT),
                        nav.getMotorPower(DriveToPoint.DriveMotor.RIGHT_FRONT),
                        nav.getMotorPower(DriveToPoint.DriveMotor.LEFT_BACK),
                        nav.getMotorPower(DriveToPoint.DriveMotor.RIGHT_BACK));
            }
            driveController.assignWheelPowers(wheelPower);

            telemetry.addData("current state:",stateMachine);

            telemetry.update();

        }
    }
}
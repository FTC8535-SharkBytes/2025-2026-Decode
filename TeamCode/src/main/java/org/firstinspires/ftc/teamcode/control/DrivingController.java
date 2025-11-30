package org.firstinspires.ftc.teamcode.control;



import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

import java.util.Locale;

public class DrivingController {
    private DcMotor leftFrontDrive = null;
    private DcMotor leftBackDrive = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor rightBackDrive = null;
    private void setMotorDirections(boolean zeroEncoders) {

        leftFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);
        if (zeroEncoders) {
            leftFrontDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            leftBackDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            rightFrontDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            rightBackDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        }

        leftFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
    private void updateTelemetry(Telemetry telemetry) {
        telemetry.addData("X offset", odo.getXOffset(DistanceUnit.MM));
        telemetry.addData("Y offset", odo.getYOffset(DistanceUnit.MM));
        telemetry.addData("Device Version Number:", odo.getDeviceVersion());
        telemetry.addData("Device Scalar", odo.getYawScalar());
        Pose2D pos = odo.getPosition();
        String data = String.format(Locale.US, "{X: %.3f, Y: %.3f, H: %.3f}", pos.getX(DistanceUnit.MM), pos.getY(DistanceUnit.MM), pos.getHeading(AngleUnit.DEGREES));
        telemetry.addData("Position", data);

    }

    public void updateDriveCommands(double axial, double lateral,
                                    double yaw, boolean isFastMode) {
        assignWheelPowers(computeWheelPower(axial, lateral, yaw, isFastMode));

        // Show the elapsed game time and wheel power.
        updateTelemetry(telemetry);

    }
    private void connectMotorsToHub(HardwareMap hardwareMap) {

        leftFrontDrive  = hardwareMap.get(DcMotor.class, "left_front_drive");
        leftBackDrive  = hardwareMap.get(DcMotor.class, "left_back_drive");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "right_front_drive");
        rightBackDrive = hardwareMap.get(DcMotor.class, "right_back_drive");
    }
    private Telemetry telemetry;
    GoBildaPinpointDriver odo; // Declare OpMode member for the Odometry Computer
    //idk what we are doing here
    public void init(HardwareMap hardwareMap, Telemetry telemetry, boolean zeroEncoders) {
        this.telemetry = telemetry;
        //Connect motors to irl motors
        connectMotorsToHub(hardwareMap);
        odo = hardwareMap.get(GoBildaPinpointDriver.class,"odo");
        odo.setOffsets(-84.0, -120.0, DistanceUnit.MM); //these are tuned for 3110-0002-0001 Product Insight #1
        odo.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        odo.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.FORWARD);

        if (zeroEncoders) {
            odo.resetPosAndIMU();
        }

        //set motor directions
        setMotorDirections(zeroEncoders);

    }
    private WheelPower computeWheelPower(double axial, double lateral,
                                         double yaw, boolean isFastMode) {
        double max;

        /*
         Combine the joystick requests for each axis-motion to determine each wheel's power.
         Set up a variable for each drive wheel to save the power level for telemetry.
        */
        double leftFrontPower;
        double rightFrontPower;
        double leftBackPower;
        double rightBackPower;
        if (isFastMode) {
            leftFrontPower = (axial + lateral + yaw)*3/4;
            rightFrontPower = (axial - lateral - yaw)*3/4;
            leftBackPower = (axial - lateral + yaw)*3/4;
            rightBackPower = (axial + lateral - yaw)*3/4;
        } else {
            leftFrontPower  = (axial + lateral + yaw)/4;
            rightFrontPower = (axial - lateral - yaw)/4;
            leftBackPower   = (axial - lateral + yaw)/4;
            rightBackPower  = (axial + lateral - yaw)/4;
        }

        // Normalize the values so no wheel power exceeds 100%
        // This ensures that the robot maintains the desired motion.
        max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
        max = Math.max(max, Math.abs(leftBackPower));
        max = Math.max(max, Math.abs(rightBackPower));

        if (max > 1.0) {
            leftFrontPower  /= max;
            rightFrontPower /= max;
            leftBackPower   /= max;
            rightBackPower  /= max;
        }
        return new WheelPower(leftFrontPower, rightFrontPower, leftBackPower, rightBackPower);
    }
    public static class WheelPower {
        public final double leftFrontPower;
        public final double rightFrontPower;
        public final double leftBackPower;
        public final double rightBackPower;

        public WheelPower(double leftFrontPower, double rightFrontPower, double leftBackPower, double rightBackPower) {
            this.leftFrontPower = leftFrontPower;
            this.rightFrontPower = rightFrontPower;
            this.leftBackPower = leftBackPower;
            this.rightBackPower = rightBackPower;
        }
    }
    public void assignWheelPowers(WheelPower wheelPower) {

        leftFrontDrive.setPower(wheelPower.leftFrontPower);
        rightFrontDrive.setPower(wheelPower.rightFrontPower);
        leftBackDrive.setPower(wheelPower.leftBackPower);
        rightBackDrive.setPower(wheelPower.rightBackPower);
    }
}

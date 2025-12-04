package org.firstinspires.ftc.teamcode.control;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.List;
import java.util.Locale;

public class LimelightController {

    private Telemetry telemetry;
    private Limelight3A limelight;

    public void init(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
    }

    /**
     * Select the limelight pipeline
     * 0 = AprilTag ID21 (GPP), ID22 (PGP), or ID23 (PPG)
     * 1 = AprilTag ID20 (Blue Goal)
     * 2 = AprilTag ID24 (Red Goal)
     *
     * @param pipeline int representing pipeline to run
     */
    public void switchPipeline(int pipeline) {
        limelight.stop();
        limelight.pipelineSwitch(pipeline);
        limelight.start();
    }

    public LimelightResult getLimelightTrackingResult() {
        LimelightResult result = new LimelightResult();
        LLResult r = limelight.getLatestResult();
        if (r != null && r.isValid()) {
            result.isValid = true;
            result.tx = r.getTx();
            result.ty = r.getTy();
            result.ta = r.getTa();
        }
        String data = String.format(Locale.US, "{Tx: %.2f, Ty: %.2f, Ta: %.3f}", result.tx, result.ty, result.ta);
        telemetry.addData("Limelight Tracking", data);
        return result;
    }

    public ArtifactSorter.Pattern detectPattern() {
        LLResult r = limelight.getLatestResult();
        ArtifactSorter.Pattern pattern = ArtifactSorter.Pattern.NONE;
        if (r != null && r.isValid()) {
            List<LLResultTypes.FiducialResult> fiducials = r.getFiducialResults();
            if (fiducials != null && !fiducials.isEmpty()) {
                int id = fiducials.get(0).getFiducialId();
                switch (id) {
                    case 21:
                        pattern = ArtifactSorter.Pattern.GPP;
                        break;
                    case 22:
                        pattern = ArtifactSorter.Pattern.PGP;
                        break;
                    case 23:
                        pattern = ArtifactSorter.Pattern.PPG;
                        break;
                }
            }
        }
        telemetry.addData("Limelight Pattern", pattern);
        return pattern;
    }

    public void shutdown() {
        limelight.shutdown();
    }

    public static class LimelightResult {
        public boolean isValid;
        public double tx;
        public double ty;
        public double ta;
    }
}

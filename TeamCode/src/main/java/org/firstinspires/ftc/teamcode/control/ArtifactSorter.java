package org.firstinspires.ftc.teamcode.control;

import android.graphics.Color;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.Locale;

public class ArtifactSorter {

    public enum Pattern {
        PPG,
        PGP,
        GPP,
        NONE
    }

    private Telemetry telemetry;

    // artifact pattern: 0 - unknown, 1 - PPG, 2 - PGP, 3 - GPP
    private Pattern desiredPattern = Pattern.NONE;

    private ColorSensor color1;
    private ColorSensor color2;
    private ColorSensor color3;

    private float[] hsv1 = new float[3];
    private float[] hsv2 = new float[3];
    private float[] hsv3 = new float[3];

    private ArtifactColor artifactColor1 = ArtifactColor.UNKNOWN;
    private ArtifactColor artifactColor2 = ArtifactColor.UNKNOWN;
    private ArtifactColor artifactColor3 = ArtifactColor.UNKNOWN;

    enum ArtifactColor {
        PURPLE,
        GREEN,
        UNKNOWN
    }

    public void init(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;
        color1 = hardwareMap.get(ColorSensor.class, "color1");
        color2 = hardwareMap.get(ColorSensor.class, "color2");
        color3 = hardwareMap.get(ColorSensor.class, "color3");
    }

    public void updateColors() {
        Color.RGBToHSV(color1.red() * 8, color1.green() * 8, color1.blue() * 8, hsv1);
        Color.RGBToHSV(color2.red() * 8, color2.green() * 8, color2.blue() * 8, hsv2);
        Color.RGBToHSV(color3.red() * 8, color3.green() * 8, color3.blue() * 8, hsv3);

        ArtifactColor color = detectColor(hsv1);
        if (color != ArtifactColor.UNKNOWN) {
            artifactColor1 = color;
        }
        color = detectColor(hsv2);
        if (color != ArtifactColor.UNKNOWN) {
            artifactColor2 = color;
        }
        color = detectColor(hsv3);
        if (color != ArtifactColor.UNKNOWN) {
            artifactColor3 = color;
        }

    }

    public void clear() {
        artifactColor1 = ArtifactColor.UNKNOWN;
        artifactColor2 = ArtifactColor.UNKNOWN;
        artifactColor3 = ArtifactColor.UNKNOWN;
    }

    public void updateTelemetry() {
        String data = String.format(Locale.US, "{%s, %s, %s}", artifactColor1, artifactColor2, artifactColor3);
        telemetry.addData("Artifact Colors", data);
    }

    private ArtifactColor detectColor(float[] hsv) {
        float hue = hsv[0];

        if (isPurple(hue)) {
            return ArtifactColor.PURPLE;
        } else if (isGreen(hue)) {
            return ArtifactColor.GREEN;
        } else {
            return ArtifactColor.UNKNOWN;
        }
    }

    private boolean isPurple(float hue) {
        // typical purple hue range
        return hue >= 215 && hue <= 345;
    }

    private boolean isGreen(float hue) {
        // typical green hue range
        return hue >= 155 && hue <= 170;
    }

    public void setDesiredPattern(Pattern pattern) {
        this.desiredPattern = pattern;
    }

    public boolean shouldRotateBelly() {
        switch (desiredPattern) {
            case NONE:
                return false;
            case PPG: // PPG
                if (artifactColor1 == ArtifactColor.PURPLE &&
                        artifactColor2 == ArtifactColor.PURPLE &&
                        artifactColor3 == ArtifactColor.GREEN) {
                    return false;
                } else if (artifactColor1 == ArtifactColor.GREEN &&
                        artifactColor2 == ArtifactColor.PURPLE &&
                        artifactColor3 == ArtifactColor.PURPLE) {
                    return true;
                } else if (artifactColor1 == ArtifactColor.PURPLE &&
                        artifactColor2 == ArtifactColor.GREEN &&
                        artifactColor3 == ArtifactColor.PURPLE) {
                    return true;
                } else if (artifactColor2 == ArtifactColor.PURPLE &&
                        artifactColor3 == ArtifactColor.GREEN) {
                    return false;
                } else if (artifactColor1 == ArtifactColor.PURPLE &&
                        artifactColor3 == ArtifactColor.GREEN) {
                    return false;
                } else if (artifactColor1 == ArtifactColor.PURPLE &&
                        artifactColor2 == ArtifactColor.PURPLE &&
                        artifactColor3 == ArtifactColor.PURPLE) {
                    return false;
                } else if (artifactColor1 == ArtifactColor.GREEN &&
                        artifactColor2 == ArtifactColor.GREEN &&
                        artifactColor3 == ArtifactColor.GREEN) {
                    return false;
                } else {
                    return true;
                }
            case PGP: // PGP
                if (artifactColor1 == ArtifactColor.PURPLE &&
                        artifactColor2 == ArtifactColor.PURPLE &&
                        artifactColor3 == ArtifactColor.GREEN) {
                    return true;
                } else if (artifactColor1 == ArtifactColor.GREEN &&
                        artifactColor2 == ArtifactColor.PURPLE &&
                        artifactColor3 == ArtifactColor.PURPLE) {
                    return true;
                } else if (artifactColor1 == ArtifactColor.PURPLE &&
                        artifactColor2 == ArtifactColor.GREEN &&
                        artifactColor3 == ArtifactColor.PURPLE) {
                    return false;
                } else if (artifactColor1 == ArtifactColor.PURPLE &&
                        artifactColor2 == ArtifactColor.GREEN) {
                    return false;
                } else if (artifactColor3 == ArtifactColor.PURPLE &&
                        artifactColor2 == ArtifactColor.GREEN) {
                    return false;
                } else if (artifactColor1 == ArtifactColor.PURPLE &&
                        artifactColor2 == ArtifactColor.PURPLE &&
                        artifactColor3 == ArtifactColor.PURPLE) {
                    return false;
                } else if (artifactColor1 == ArtifactColor.GREEN &&
                        artifactColor2 == ArtifactColor.GREEN &&
                        artifactColor3 == ArtifactColor.GREEN) {
                    return false;
                } else {
                    return true;
                }
            case GPP: // GPP
                if (artifactColor1 == ArtifactColor.PURPLE &&
                        artifactColor2 == ArtifactColor.PURPLE &&
                        artifactColor3 == ArtifactColor.GREEN) {
                    return true;
                } else if (artifactColor1 == ArtifactColor.GREEN &&
                        artifactColor2 == ArtifactColor.PURPLE &&
                        artifactColor3 == ArtifactColor.PURPLE) {
                    return false;
                } else if (artifactColor1 == ArtifactColor.PURPLE &&
                        artifactColor2 == ArtifactColor.GREEN &&
                        artifactColor3 == ArtifactColor.PURPLE) {
                    return true;
                } else if (artifactColor2 == ArtifactColor.PURPLE &&
                        artifactColor1 == ArtifactColor.GREEN) {
                    return false;
                } else if (artifactColor3 == ArtifactColor.PURPLE &&
                        artifactColor1 == ArtifactColor.GREEN) {
                    return false;
                } else if (artifactColor1 == ArtifactColor.PURPLE &&
                        artifactColor2 == ArtifactColor.PURPLE &&
                        artifactColor3 == ArtifactColor.PURPLE) {
                    return false;
                } else if (artifactColor1 == ArtifactColor.GREEN &&
                        artifactColor2 == ArtifactColor.GREEN &&
                        artifactColor3 == ArtifactColor.GREEN) {
                    return false;
                } else {
                    return true;
                }
        }
        return false;
    }

}

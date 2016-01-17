package com.eveningoutpost.dexdrip.Models;

/**
 * Created by jamorham on 04/01/16.
 */

// user profile for insulin related parameters which can be configured and set at times of day
// currently a placeholder with hardcoded values

// TODO Proper support for MG/DL

public class Profile {

    private final static String TAG = "jamorham pred";
    public static double minimum_shown_iob = 0.005;
    public static double minimum_shown_cob = 0.01;
    public static double minimum_insulin_recommendation = 0.1;
    public static double minimum_carb_recommendation = 1;

    static double getSensitivity(double when) {
        return 3; //
    }

    static double getCarbAbsorptionRate(double when) {
        return 30; // carbs per hour
    }

    static double maxLiverImpactRatio(double when) {
        return 0.9; // how much can the liver block carbs going in to blood stream?
    }

    static double getCarbRatio(double when) {
        return 10; // g per unit
    }

    static double getLiverSensRatio(double when) {
        return 2.0;
    }

    static double getTargetRangeInMmol(double when) {
        return 5.5;
    }

    static double getCarbSensitivity(double when) {
        return getCarbRatio(when) / getSensitivity(when);
    }

    static double getCarbsToRaiseByMmol(double mmol, double when) {

        double result = getCarbSensitivity(when) * mmol;
        return result;
    }

    static double getInsulinToLowerByMmol(double mmol, double when) {
        return mmol / getSensitivity(when);
    }

    // take an average of carb suggestions when our scope is between two times
    static double getCarbsToRaiseByMmolBetweenTwoTimes(double mmol, double whennow, double whenthen) {
        double result = (getCarbsToRaiseByMmol(mmol, whennow) + getCarbsToRaiseByMmol(mmol, whenthen)) / 2;
        UserError.Log.d(TAG, "GetCarbsToRaiseByMmolBetweenTwoTimes: " + JoH.qs(mmol) + " result: " + JoH.qs(result));
        return result;
    }

    static double getInsulinToLowerByMmolBetweenTwoTimes(double mmol, double whennow, double whenthen) {
        return (getInsulinToLowerByMmol(mmol, whennow) + getInsulinToLowerByMmol(mmol, whenthen)) / 2;
    }

    public static double[] evaluateEndGameMmol(double mmol, double endGameTime, double timeNow) {
        double addcarbs = 0;
        double addinsulin = 0;
        double target_mmol = getTargetRangeInMmol(endGameTime);
        double diff_mmol = target_mmol - mmol;
        if (diff_mmol > 0) {
            addcarbs = getCarbsToRaiseByMmolBetweenTwoTimes(diff_mmol, timeNow, endGameTime);
        }

        if (diff_mmol < 0) {
            addinsulin = getInsulinToLowerByMmolBetweenTwoTimes(diff_mmol * -1, timeNow, endGameTime);
        }
        return new double[]{addcarbs, addinsulin};
    }
}
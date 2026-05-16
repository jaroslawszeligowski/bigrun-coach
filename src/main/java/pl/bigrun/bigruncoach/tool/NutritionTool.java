/**
 * Project: bigrun-coach
 * File: NutritionTool
 * ------------------------------------------------------------------------
 * Description:
 * Spring AI Tool for calculating hydration and carbohydrate needs during running.
 * Used by the AI assistant to answer nutrition-related questions from runners.
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 15/05/2026
 */
package pl.bigrun.bigruncoach.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class NutritionTool {

    private static final double COLD_FLUID_ML_PER_HOUR    = 300;
    private static final double COOL_FLUID_ML_PER_HOUR    = 400;
    private static final double BASE_FLUID_ML_PER_HOUR    = 500;
    private static final double HEAT_FLUID_ML_PER_DEGREE  = 30;

    private static final double COLD_THRESHOLD_CELSIUS    = 0;
    private static final double COOL_THRESHOLD_CELSIUS    = 10;
    private static final double HEAT_THRESHOLD_CELSIUS    = 20;

    private static final int    DRINKING_INTERVAL_MINUTES = 15;
    private static final int    CARBS_GRAMS_PER_HOUR      = 60;
    private static final int    CARBS_PER_GEL_GRAMS       = 25;
    private static final int    MINUTES_PER_HOUR          = 60;

    private final NutritionFormatter formatter;

    public NutritionTool(NutritionFormatter formatter) {
        this.formatter = formatter;
    }

    @Tool(description = "Calculates recommended fluid intake in ml for a run of given duration and temperature. " +
            "Parameters: durationMinutes (e.g. 30 for 30 min, 60 for 1h, 120 for 2h, 240 for 4h), " +
            "tempCelsius (e.g. -5 for frost, 5 for cold, 15 for mild, 25 for warm, 35 for hot). " +
            "Accounts for cold weather (reduced intake) and hot weather (increased intake). " +
            "Use this when user asks about hydration, drinking, water intake, or fluids during running.")
    public String calculateHydration(double durationMinutes, double tempCelsius) {
        if (durationMinutes <= 0) {
            return "Invalid input: duration must be positive.";
        }
        if (isInvalidTemperature(tempCelsius)) {
            return "Invalid input: temperature must be a valid number above absolute zero (-273°C).";
        }
        RunDurationCategory category  = RunDurationCategory.fromDuration((int) durationMinutes);
        double fluidRateMlPerHour     = fluidRateFor(tempCelsius);
        double totalMl                = fluidRateMlPerHour * (durationMinutes / MINUTES_PER_HOUR);
        int    portions               = (int) durationMinutes / DRINKING_INTERVAL_MINUTES;
        double mlPerPortion           = portions > 0 ? totalMl / portions : 0;

        return formatter.formatHydrationPlan((int) durationMinutes, tempCelsius, totalMl, category,
                DRINKING_INTERVAL_MINUTES, mlPerPortion);
    }

    @Tool(description = "Calculates recommended carbohydrate intake in grams for a run of given duration. " +
            "Parameters: durationMinutes (e.g. 45 for short run, 90 for long run, 180 for 3h, 300 for 5h ultra). " +
            "Returns total grams and number of energy gels needed. " +
            "Use this when user asks about nutrition, carbs, gels, energy, or fueling during running.")
    public String calculateCarbohydrates(double durationMinutes) {
        if (durationMinutes <= 0) {
            return "Invalid input: duration must be positive.";
        }
        RunDurationCategory category = RunDurationCategory.fromDuration((int) durationMinutes);
        int totalCarbs = (int) ((durationMinutes / MINUTES_PER_HOUR) * CARBS_GRAMS_PER_HOUR);
        int gels       = totalCarbs / CARBS_PER_GEL_GRAMS;

        return formatter.formatCarbPlan((int) durationMinutes, category, totalCarbs, gels);
    }

    double fluidRateFor(double tempCelsius) {
        if (tempCelsius < COLD_THRESHOLD_CELSIUS) return COLD_FLUID_ML_PER_HOUR;
        if (tempCelsius < COOL_THRESHOLD_CELSIUS) return COOL_FLUID_ML_PER_HOUR;
        if (tempCelsius <= HEAT_THRESHOLD_CELSIUS) return BASE_FLUID_ML_PER_HOUR;
        return BASE_FLUID_ML_PER_HOUR + (tempCelsius - HEAT_THRESHOLD_CELSIUS) * HEAT_FLUID_ML_PER_DEGREE;
    }

    private boolean isInvalidTemperature(double tempCelsius) {
        return Double.isNaN(tempCelsius) || Double.isInfinite(tempCelsius) || tempCelsius < -273;
    }
}

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

    private final NutritionFormatter formatter;

    public NutritionTool(NutritionFormatter formatter) {
        this.formatter = formatter;
    }

    @Tool(description = "Calculates recommended fluid intake in ml for a run of given duration and temperature. " +
            "Accounts for cold weather (reduced intake) and hot weather (increased intake). " +
            "Use this when user asks about hydration, drinking, water intake, or fluids during running.")
    public String calculateHydration(int durationMinutes, double tempCelsius) {
        if (durationMinutes <= 0) {
            return "Invalid input: duration must be positive.";
        }
        RunDurationCategory category = RunDurationCategory.fromDuration(durationMinutes);
        double fluidRateMlPerHour    = fluidRateFor(tempCelsius);
        double totalMl               = fluidRateMlPerHour * (durationMinutes / 60.0);
        int    portions              = durationMinutes / DRINKING_INTERVAL_MINUTES;
        double mlPerPortion          = portions > 0 ? totalMl / portions : totalMl;

        return formatter.formatHydrationPlan(durationMinutes, tempCelsius, totalMl, category,
                DRINKING_INTERVAL_MINUTES, mlPerPortion);
    }

    @Tool(description = "Calculates recommended carbohydrate intake in grams for a run of given duration. " +
            "Use this when user asks about nutrition, carbs, gels, energy, or fueling during running.")
    public String calculateCarbohydrates(int durationMinutes) {
        if (durationMinutes <= 0) {
            return "Invalid input: duration must be positive.";
        }
        RunDurationCategory category = RunDurationCategory.fromDuration(durationMinutes);
        int totalCarbs = (int) ((durationMinutes / 60.0) * CARBS_GRAMS_PER_HOUR);
        int gels       = totalCarbs / CARBS_PER_GEL_GRAMS;

        return formatter.formatCarbPlan(durationMinutes, category, totalCarbs, gels);
    }

    private double fluidRateFor(double tempCelsius) {
        if (tempCelsius < COLD_THRESHOLD_CELSIUS) return COLD_FLUID_ML_PER_HOUR;
        if (tempCelsius < COOL_THRESHOLD_CELSIUS) return COOL_FLUID_ML_PER_HOUR;
        if (tempCelsius <= HEAT_THRESHOLD_CELSIUS) return BASE_FLUID_ML_PER_HOUR;
        return BASE_FLUID_ML_PER_HOUR + (tempCelsius - HEAT_THRESHOLD_CELSIUS) * HEAT_FLUID_ML_PER_DEGREE;
    }
}

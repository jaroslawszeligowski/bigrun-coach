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

    @Tool(description = "Calculates recommended fluid intake in ml for a run of given duration and temperature. Use this when user asks about hydration, drinking, water intake, or fluids during running.")
    public String calculateHydration(int durationMinutes, double tempCelsius) {
        if (durationMinutes <= 0) {
            return "Invalid input: duration must be positive.";
        }
        double baseRateMlPerHour = 500;
        if (tempCelsius > 20) {
            baseRateMlPerHour += (tempCelsius - 20) * 30;
        }
        double totalMl = baseRateMlPerHour * (durationMinutes / 60.0);
        int drinkingIntervalMin = durationMinutes <= 30 ? 0 : 15;

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Hydration plan for %d min run at %.1f°C:%n", durationMinutes, tempCelsius));
        sb.append(String.format("Total fluid intake: %.0f ml%n", totalMl));
        if (durationMinutes <= 30) {
            sb.append("Short run — hydration before and after is sufficient, no drinking needed mid-run.");
        } else {
            int portions = durationMinutes / drinkingIntervalMin;
            double mlPerPortion = totalMl / portions;
            sb.append(String.format("Drink every %d minutes: ~%.0f ml per portion%n", drinkingIntervalMin, mlPerPortion));
            if (durationMinutes >= 60) {
                sb.append("Consider sports drink or electrolytes for runs over 60 min.");
            }
        }
        return sb.toString();
    }

    @Tool(description = "Calculates recommended carbohydrate intake in grams for a run of given duration. Use this when user asks about nutrition, carbs, gels, energy, or fueling during running.")
    public String calculateCarbohydrates(int durationMinutes) {
        if (durationMinutes <= 0) {
            return "Invalid input: duration must be positive.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Carbohydrate plan for %d min run:%n", durationMinutes));

        if (durationMinutes < 60) {
            sb.append("Under 60 min — no carbohydrates needed during the run.%n".formatted());
            sb.append("Ensure adequate pre-run meal 2-3 hours before.");
        } else if (durationMinutes < 90) {
            sb.append("60-90 min — optional 20-30g carbohydrates (1 gel) after 45 min%n");
            sb.append("Example: 1 energy gel or 400ml sports drink.");
        } else {
            double hoursRunning = durationMinutes / 60.0;
            int totalCarbs = (int) (hoursRunning * 60);
            int gels = totalCarbs / 25;
            sb.append(String.format("Total carbs: ~%dg (~%d energy gels)%n", totalCarbs, gels));
            sb.append(String.format("Start fueling after 30-45 min, then every 30-45 min%n"));
            sb.append(String.format("Take gels with water (150-200ml) to aid absorption."));
        }
        return sb.toString();
    }
}

/**
 * Project: bigrun-coach
 * File: NutritionFormatter
 * ------------------------------------------------------------------------
 * Description:
 * Responsible for formatting hydration and carbohydrate recommendations
 * into human-readable strings. Extracted from NutritionTool to satisfy SRP.
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 15/05/2026
 */
package pl.bigrun.bigruncoach.tool;

import org.springframework.stereotype.Component;

@Component
public class NutritionFormatter {

    public String formatHydrationPlan(int durationMinutes, double tempCelsius,
                                      double totalMl, RunDurationCategory category,
                                      int intervalMin, double mlPerPortion) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Hydration plan for %d min run at %.1f°C:%n", durationMinutes, tempCelsius));
        sb.append(String.format("Total fluid intake: %.0f ml%n", totalMl));

        if (category == RunDurationCategory.SHORT) {
            sb.append("Short run — hydration before and after is sufficient, no drinking needed mid-run.");
        } else {
            sb.append(String.format("Drink every %d minutes: ~%.0f ml per portion%n", intervalMin, mlPerPortion));
            if (category.isAtLeast(RunDurationCategory.LONG)) {
                sb.append("Consider sports drink or electrolytes for runs over 60 min.");
            }
        }
        return sb.toString();
    }

    public String formatCarbPlan(int durationMinutes, RunDurationCategory category,
                                 int totalCarbs, int gels) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Carbohydrate plan for %d min run:%n", durationMinutes));

        switch (category) {
            case SHORT, MEDIUM -> {
                sb.append("Under 60 min — no carbohydrates needed during the run.\n");
                sb.append("Ensure adequate pre-run meal 2-3 hours before.");
            }
            case LONG -> {
                sb.append("60-90 min — optional 20-30g carbohydrates (1 gel) after 45 min\n");
                sb.append("Example: 1 energy gel or 400ml sports drink.");
            }
            case ULTRA -> {
                sb.append(String.format("Total carbs: ~%dg (~%d energy gels)%n", totalCarbs, gels));
                sb.append("Start fueling after 30-45 min, then every 30-45 min\n");
                sb.append("Take gels with water (150-200ml) to aid absorption.");
            }
        }
        return sb.toString();
    }
}

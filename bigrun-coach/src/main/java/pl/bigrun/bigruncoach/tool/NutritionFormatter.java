/**
 * Project: bigrun-coach
 * File: NutritionFormatter
 * ------------------------------------------------------------------------
 * Description:
 * Responsible for formatting hydration and carbohydrate recommendations
 * into human-readable strings. Extracted from NutritionTool to satisfy SRP.
 * Domain-specific advice is delegated to RunDurationCategory (OCP).
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
        return String.format("Hydration plan for %d min run at %.1f°C:%n", durationMinutes, tempCelsius) +
               String.format("Total fluid intake: %.0f ml%n", totalMl) +
               category.formatHydrationAdvice(intervalMin, mlPerPortion);
    }

    public String formatCarbPlan(int durationMinutes, RunDurationCategory category,
                                 int totalCarbs, int gels) {
        return String.format("Carbohydrate plan for %d min run:%n", durationMinutes) +
               category.formatCarbPlan(totalCarbs, gels);
    }
}

/**
 * Project: bigrun-coach
 * File: RunDurationCategory
 * ------------------------------------------------------------------------
 * Description:
 * Categorizes run duration into distinct groups used for nutrition planning.
 * Each category encapsulates its own hydration and carbohydrate advice (OCP).
 * Adding a new category requires only a new enum constant — no changes to
 * NutritionFormatter or NutritionTool.
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 15/05/2026
 */
package pl.bigrun.bigruncoach.tool;

import java.util.Arrays;

public enum RunDurationCategory {

    SHORT(0, 30) {
        @Override
        public String formatHydrationAdvice(int intervalMin, double mlPerPortion) {
            return "Short run — hydration before and after is sufficient, no drinking needed mid-run.";
        }

        @Override
        public String formatCarbPlan(int totalCarbs, int gels) {
            return "Under 60 min — no carbohydrates needed during the run.\n" +
                   "Ensure adequate pre-run meal 2-3 hours before.";
        }
    },

    MEDIUM(31, 59) {
        @Override
        public String formatHydrationAdvice(int intervalMin, double mlPerPortion) {
            return String.format("Drink every %d minutes: ~%.0f ml per portion", intervalMin, mlPerPortion);
        }

        @Override
        public String formatCarbPlan(int totalCarbs, int gels) {
            return "Under 60 min — no carbohydrates needed during the run.\n" +
                   "Ensure adequate pre-run meal 2-3 hours before.";
        }
    },

    LONG_RUN(60, 89) {
        @Override
        public String formatHydrationAdvice(int intervalMin, double mlPerPortion) {
            return String.format("Drink every %d minutes: ~%.0f ml per portion%n", intervalMin, mlPerPortion) +
                   "Consider sports drink or electrolytes for runs over 60 min.";
        }

        @Override
        public String formatCarbPlan(int totalCarbs, int gels) {
            return "60-90 min — optional 20-30g carbohydrates (1 gel) after 45 min\n" +
                   "Example: 1 energy gel or 400ml sports drink.";
        }
    },

    EXTENDED(90, 239) {
        @Override
        public String formatHydrationAdvice(int intervalMin, double mlPerPortion) {
            return String.format("Drink every %d minutes: ~%.0f ml per portion%n", intervalMin, mlPerPortion) +
                   "Consider sports drink or electrolytes for runs over 60 min.";
        }

        @Override
        public String formatCarbPlan(int totalCarbs, int gels) {
            return String.format("Total carbs: ~%dg (~%d energy gels)%n", totalCarbs, gels) +
                   "Start fueling after 30-45 min, then every 30-45 min\n" +
                   "Take gels with water (150-200ml) to aid absorption.";
        }
    },

    ULTRA(240, Integer.MAX_VALUE) {
        @Override
        public String formatHydrationAdvice(int intervalMin, double mlPerPortion) {
            return String.format("Drink every %d minutes: ~%.0f ml per portion%n", intervalMin, mlPerPortion) +
                   "Consider sports drink or electrolytes.\n" +
                   "Plan for aid stations — include solid food and electrolyte tablets for runs over 4h.";
        }

        @Override
        public String formatCarbPlan(int totalCarbs, int gels) {
            return String.format("Total carbs: ~%dg (~%d energy gels)%n", totalCarbs, gels) +
                   "Start fueling after 30 min, then every 20-30 min\n" +
                   "Include solid food (bananas, dates, bars) at aid stations\n" +
                   "Take gels with water (150-200ml) to aid absorption.";
        }
    };

    private final int minMinutes;
    private final int maxMinutes;

    RunDurationCategory(int minMinutes, int maxMinutes) {
        this.minMinutes = minMinutes;
        this.maxMinutes = maxMinutes;
    }

    public abstract String formatHydrationAdvice(int intervalMin, double mlPerPortion);

    public abstract String formatCarbPlan(int totalCarbs, int gels);

    public static RunDurationCategory fromDuration(int durationMinutes) {
        return Arrays.stream(values())
                .filter(c -> durationMinutes >= c.minMinutes && durationMinutes <= c.maxMinutes)
                .findFirst()
                .orElse(ULTRA);
    }
}

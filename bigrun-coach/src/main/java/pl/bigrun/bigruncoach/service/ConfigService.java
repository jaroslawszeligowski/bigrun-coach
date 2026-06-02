/**
 * Project: bigrun-coach
 * File: ConfigService
 * ------------------------------------------------------------------------
 * Description:
 * Contract for retrieving active LLM provider configuration.
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 15/05/2026
 */
package pl.bigrun.bigruncoach.service;

import pl.bigrun.bigruncoach.dto.ConfigResponse;

public interface ConfigService {

    ConfigResponse getActiveConfig();
}

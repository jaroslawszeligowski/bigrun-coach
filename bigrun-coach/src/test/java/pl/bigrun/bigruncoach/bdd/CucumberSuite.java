/**
 * Project: bigrun-coach
 * File: CucumberSuite
 * ------------------------------------------------------------------------
 * Description:
 * JUnit Platform Suite runner for Cucumber BDD tests.
 * Discovers .feature files from classpath:features and uses step definitions
 * from pl.bigrun.bigruncoach.bdd package.
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 15/05/2026
 */
package pl.bigrun.bigruncoach.bdd;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME,   value = "pl.bigrun.bigruncoach.bdd")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty,io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm,junit:target/cucumber-reports/cucumber.xml")
public class CucumberSuite {}

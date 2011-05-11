package net.thucydides.easyb;


import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.StepListener;
import static net.thucydides.easyb.StepName.*;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.core.webdriver.WebdriverManager;

import org.easyb.plugin.BasePlugin;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory

import net.thucydides.core.reports.ReportService
import net.thucydides.core.reports.html.HtmlAcceptanceTestReporter
import net.thucydides.core.reports.xml.XMLAcceptanceTestReporter
import com.google.common.collect.ImmutableList
import net.thucydides.core.reports.AcceptanceTestReporter
import net.thucydides.core.model.AcceptanceTestRun
import org.easyb.listener.ListenerFactory

import net.thucydides.core.steps.StepFactory
import net.thucydides.core.steps.BaseStepListener
import net.thucydides.core.webdriver.Configuration
import net.thucydides.core.steps.ExecutedStepDescription

public class ThucydidesPlugin extends BasePlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThucydidesPlugin.class);

    private WebdriverManager webdriverManager;

    private runningFirstScenario = true;

    ReportService reportService;

    StepFactory stepFactory;

    StepListener stepListener;

    Configuration systemConfiguration;

    /**
     * Retrieve the runner configuration from an external source.
     */
    public ThucydidesPlugin() {
        Object.mixin ThucydidesExtensions;
    }


    @Override
    public String getName() {
        return "thucydides";
    }

    protected WebdriverManager getWebdriverManager() {
        if (webdriverManager == null) {
            webdriverManager = new WebdriverManager(getDefaultWebDriverFactory());
        }
        return webdriverManager;
    }

    protected WebDriverFactory getDefaultWebDriverFactory() {
        return new WebDriverFactory();
    }

    @Override
    public Object beforeStory(final Binding binding) {
        LOGGER.debug "Before story"
        WebDriver driver = getWebdriverManager().getWebdriver();
        binding.setVariable("driver", driver);
        binding.setVariable("thucydides", configuration);

        Pages pages = initializePagesObject(binding);

        initializeStepFactoryAndListeners(pages, driver)

        initializeStepsLibraries(pages, binding);

        initializeReportService();

        testRunStarted(binding);

        return super.beforeStory(binding);
    }

    private def testRunStarted(def binding) {
        def storyName = lookupStoryNameFrom(binding)
        stepListener.testRunStarted(storyName)
    }


    def lookupStoryNameFrom(def binding) {
        String sourceFile = binding.variables['sourceFile']
        if (sourceFile == null) {
            throw new IllegalArgumentException("No easyb source file name found - are you using a recent version of easyb (1.1 or greater)?")
        }
        String sourceFilename = new File(sourceFile).name
        sourceFilename.substring(0, sourceFilename.lastIndexOf("."))
    }

    private def initializeStepFactoryAndListeners(Pages pages, WebDriver driver) {
        stepFactory = new StepFactory(pages)
        if (stepListener == null) {
            stepListener = new BaseStepListener(driver, getSystemConfiguration().outputDirectory)
        }
        stepFactory.addListener(stepListener)

        ListenerFactory.registerBuilder(new ThucydidesListenerBuilder(stepListener));
    }

    @Override
    public Object beforeScenario(final Binding binding) {

        if (!runningFirstScenario && getConfiguration().isResetBrowserInEachScenario()) {
            resetDriver(binding)
        }
        return super.beforeScenario(binding);
    }


    @Override
    Object beforeGiven(Binding binding) {
        return super.beforeGiven(binding)
    }

    @Override
    Object beforeWhen(Binding binding) {
        return super.beforeWhen(binding)
    }

    @Override
    Object beforeThen(Binding binding) {
        return super.beforeThen(binding)
    }


    @Override
    public Object afterScenario(final Binding binding) {
        runningFirstScenario = false;
        return super.afterScenario(binding);
    }


    @Override
    public Object afterStory(final Binding binding) {

        closeDriver(binding);

        stepFactory.notifyStepFinished()

        generateReportsFor(stepListener.testRunResults);

        return super.afterStory(binding);
    }

    def initializeReportService() {

        reportService = new ReportService(getSystemConfiguration().outputDirectory, getDefaultReporters());
    }

    def generateReportsFor(final List<AcceptanceTestRun> testRunResults) {

        reportService.generateReportsFor(testRunResults);
    }

    def initializePagesObject(Binding binding) {
        Pages pages = newPagesInstanceIn(binding);

        openBrowserUsing(pages)

        return pages;
    }

    private def openBrowserUsing(Pages pages) {
        pages.start()
    }

    private Pages newPagesInstanceIn(Binding binding) {
        Pages pages = new Pages(getWebdriverManager().getWebdriver());
        pages.setDefaultBaseUrl(getConfiguration().getDefaultBaseUrl());
        binding.setVariable("pages", pages)
        return pages
    }

    def initializeStepsLibraries(Pages pages, Binding binding) {

        def factory = new StepFactory(pages);

        configuration.registeredSteps.each { stepLibraryClass ->
            def stepLibrary = proxyFor(stepLibraryClass)
            binding.setVariable(nameOf(stepLibraryClass), stepLibrary)
        }
    }

    private def proxyFor(def stepLibraryClass) {
        stepFactory.newSteps(stepLibraryClass)
    }

    private def closeDriver(Binding binding) {
        getWebdriverManager().closeDriver()
    }

    private def resetDriver(Binding binding) {
        Pages pages = binding.getVariable("pages")
        pages.start()
    }
    /**
     * The configuration manages output directories and driver types.
     * They can be defined as system values, or have sensible defaults.
     */
    public PluginConfiguration getConfiguration() {
        return PluginConfiguration.getInstance();
    }

    public PluginConfiguration resetConfiguration() {
        return PluginConfiguration.reset();
    }

    /**
     * The configuration manages output directories and driver types.
     * They can be defined as system values, or have sensible defaults.
     */
    protected Configuration getSystemConfiguration() {
        if (systemConfiguration == null) {
            systemConfiguration = new Configuration();
        }
        return systemConfiguration;
    }

    /**
     * The default reporters applicable for standard test runs.
     */
    public Collection<AcceptanceTestReporter> getDefaultReporters() {
        return ImmutableList.of(new XMLAcceptanceTestReporter(),
        new HtmlAcceptanceTestReporter());
    }
}

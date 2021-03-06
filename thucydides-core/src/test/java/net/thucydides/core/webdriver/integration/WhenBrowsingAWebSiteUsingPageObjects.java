package net.thucydides.core.webdriver.integration;

import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.pages.PageObject;
import net.thucydides.core.pages.PageUrls;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.SupportedWebDriver;
import net.thucydides.core.webdriver.SystemPropertiesConfiguration;
import net.thucydides.core.webdriver.WebDriverFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.io.File;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

public class WhenBrowsingAWebSiteUsingPageObjects {

    public class IndexPage extends PageObject {

        public WebElement multiselect;

        public WebElement checkbox;
        
        public WebElement color;

        public IndexPage(WebDriver driver, int timeout) {
            super(driver, timeout);
        }
    }

    @DefaultUrl("classpath:static-site/index.html")
    public class IndexPageWithDefaultUrl extends PageObject {

        public WebElement multiselect;

        public WebElement checkbox;

        public IndexPageWithDefaultUrl(WebDriver driver, int timeout) {
            super(driver, 1);
        }
    }

    WebDriver driver;
    static WebDriver firefoxDriver;

    IndexPage indexPage;

    @BeforeClass
    public static void openFirefox() {
        firefoxDriver = (new WebDriverFactory()).newInstanceOf(SupportedWebDriver.FIREFOX);
    }

    MockEnvironmentVariables environmentVariables;

    Configuration configuration;

    @Before
    public void openLocalStaticSite() {
        driver = new HtmlUnitDriver();
        openStaticTestSite(driver);
        indexPage = new IndexPage(driver, 1);
        indexPage.setWaitForTimeout(100);
    }

    @Before
    public void initConfiguration() {
        environmentVariables = new MockEnvironmentVariables();
        configuration = new SystemPropertiesConfiguration(environmentVariables);
    }


    @After
    public void closeDriver() {
        if (driver != null) {
            driver.close();
        }
    }

    @AfterClass
    public static void shutdownFirefox() {
        firefoxDriver.quit();
    }

    private void openStaticTestSite(WebDriver driver) {
        File baseDir = new File(System.getProperty("user.dir"));
        File testSite = new File(baseDir,"src/test/resources/static-site/index.html");
        this.driver.get("file://" + testSite.getAbsolutePath());
    }

    @Test
    public void should_find_page_title() {
        assertThat(indexPage.getTitle(), is("Thucydides Test Site"));
    } 

    @Test
    public void should_find_text_contained_in_page() {
        indexPage.shouldContainText("Some test pages");
    }

    @Test(expected=NoSuchElementException.class)
    public void should_not_find_text_not_contained_in_page() {
        indexPage.shouldContainText("This text is not in the pages");
    }
    
    @Test
    public void should_select_in_multiple_select_lists_correctly() {
        indexPage.selectMultipleItemsFromDropdown(indexPage.multiselect,"Label 1", "Label 3");
        
        Set<String> selectedLabels = indexPage.getSelectedOptionLabelsFrom(indexPage.multiselect);
        assertThat(selectedLabels.size(), is(2));
        assertThat(selectedLabels, hasItems("Label 1", "Label 3"));
    }
    
    @Test
    public void should_select_values_in_multiple_select_lists_correctly() {

        indexPage.selectMultipleItemsFromDropdown(indexPage.multiselect,"Label 1", "Label 3");
        
        Set<String> selectedValues = indexPage.getSelectedOptionValuesFrom(indexPage.multiselect);
        assertThat(selectedValues.size(), is(2));
        assertThat(selectedValues, hasItems("1", "3"));
    }

    @Test
    public void should_return_selected_value_in_select() {

        indexPage.selectMultipleItemsFromDropdown(indexPage.multiselect,"Label 2");
        String selectedValue = indexPage.getSelectedValueFrom(indexPage.multiselect);
        assertThat(selectedValue, is("2"));
    }

    @Test
    public void should_return_selected_label_in_select() {

        indexPage.selectMultipleItemsFromDropdown(indexPage.multiselect,"Label 2");
        String selectedLabel = indexPage.getSelectedLabelFrom(indexPage.multiselect);
        assertThat(selectedLabel, is("Label 2"));
    }

    @Test
    public void should_select_values_in_select() {
        indexPage.selectFromDropdown(indexPage.color, "Red");
        assertThat(indexPage.getSelectedOptionValuesFrom(indexPage.color), hasItem("red"));
    }
    
    @Test
    public void ticking_an_empty_checkbox_should_set_the_value_to_true() {
        indexPage.setCheckbox(indexPage.checkbox, true);
        
        assertThat(indexPage.checkbox.isSelected(), is(true));
    }
    
    @Test
    public void ticking_a_set_checkbox_should_set_the_value_to_true() {
        if (indexPage.checkbox.isSelected()) {
            indexPage.checkbox.click();
        }
        
        indexPage.setCheckbox(indexPage.checkbox, true);
        
        assertThat(indexPage.checkbox.isSelected(), is(true));
    }
    
    @Test
    public void unticking_an_unset_checkbox_should_set_the_value_to_false() {

        indexPage.setCheckbox(indexPage.checkbox, false);
        
        assertThat(indexPage.checkbox.isSelected(), is(false));
    }
    
    @Test
    public void unticking_a_set_checkbox_should_set_the_value_to_false() {
        if (indexPage.checkbox.isSelected()) {
            indexPage.checkbox.click();
        }
        
        indexPage.setCheckbox(indexPage.checkbox, false);
        
        assertThat(indexPage.checkbox.isSelected(), is(false));
    }
    
    
    @Test
    public void should_know_when_text_appears_on_a_page() {

        indexPage.waitForTextToAppear("Label 1");
    }
        
    @Test
    public void should_know_when_an_element_is_visible() {
        IndexPageWithDefaultUrl indexPage = new IndexPageWithDefaultUrl(firefoxDriver, 1);
        indexPage.open();
        assertThat(indexPage.isElementVisible(By.id("visible")), is(true));
    }

    @Test
    public void should_know_when_an_element_is_invisible() {
        IndexPageWithDefaultUrl indexPage = new IndexPageWithDefaultUrl(firefoxDriver, 1);
        indexPage.open();
        assertThat(indexPage.isElementVisible(By.id("invisible")), is(false));
    }


    @Test(expected=TimeoutException.class)
    public void should_fail_if_text_does_not_appear_on_a_page() {

        indexPage.waitForTextToAppear("Label that is not present");
    }

    @Test
    public void should_know_when_one_of_several_texts_appears_on_a_page() {
        indexPage.waitForAnyTextToAppear("Label 1", "Label that is not present");
    }

    @Test(expected=TimeoutException.class)
    public void should_fail_if_the_requested_text_is_not_on_the_page() {
        indexPage.waitForAnyTextToAppear("Label that is not present");
    }

    @Test
    public void should_know_when_all_of_a_set_of_texts_appears_on_a_page() {
        indexPage.waitForAllTextToAppear("Label 1", "Label 2");
    }

    @Test(expected=TimeoutException.class)
    public void should_fail_if_one_of_a_set_of_requested_texts_does_not_appear_on_a_page() {
        indexPage.waitForAllTextToAppear("Label 1", "Label that is not present");
    }

    @Test(expected=TimeoutException.class)
    public void should_fail_if_none_of_the_requested_texts_appear_on_a_page() {
        indexPage.waitForAllTextToAppear("Label that is not present", "Another label that is not present");
    }

    @Test
    public void the_page_can_be_read_from_a_file_on_the_classpath() {

        IndexPageWithDefaultUrl indexPage = new IndexPageWithDefaultUrl(driver, 1);

        assertThat(indexPage.getTitle(), is("Thucydides Test Site"));
    }

    @Test
    public void the_page_can_be_opened_using_an_unsecure_certificates_compatible_profile() {

        environmentVariables.setProperty("webdriver.driver","firefox");
        environmentVariables.setProperty("refuse.untrusted.certificates","true");

        IndexPageWithDefaultUrl indexPage = new IndexPageWithDefaultUrl(driver, 1);
        PageUrls pageUrls = new PageUrls(indexPage, configuration);
        indexPage.setPageUrls(pageUrls);

        assertThat(indexPage.getTitle(), is("Thucydides Test Site"));
    }

}

package net.thucydides.core.pages.integration;

import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.pages.PageObject;
import net.thucydides.core.pages.WebElementFacade;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;

@DefaultUrl("classpath:static-site/index.html")
    public class StaticSitePage extends PageObject {

        @FindBy(name = "firstname")
        protected WebElement firstName;

        @FindBy(name = "lastname")
        protected WebElement lastName;

        @FindBy(name = "city")
        protected WebElement city;

        @FindBy(name = "country")
        protected WebElement country;

        @FindBy(name = "hiddenfield")
        protected WebElement hiddenField;

        protected WebElement csshiddenfield;

        protected WebElement readonlyField;

        protected WebElement doesNotExist;

        protected WebElement textField;

        protected WebElement checkbox;

        protected WebElement radioButton1;
        protected WebElement radioButton2;

        protected WebElement selectedCheckbox;

        protected WebElement buttonThatIsInitiallyDisabled;

        protected WebElement buttonThatIsInitiallyEnabled;

        protected WebElement placetitle;

        protected WebElement dissapearingtext;

        @FindBy(id = "visible")
        protected WebElement visibleTitle;

        @FindBy(id = "color")
        protected WebElement colors;

        protected WebElement elements;

        protected WebElement grid;

        protected WebElement emptylist;

        @FindBy(name = "fieldDoesNotExist")
        protected WebElement fieldDoesNotExist;

        @FindBy(id = "emptyLabelID")
        protected WebElement emptyLabel;

        @FindBy(id = "nonEmptyLabelID")
        protected WebElement nonEmptyLabel;

        protected WebElement focusmessage;

        protected WebElement clients;

        protected WebElement clients_with_extra_cells;

        protected WebElement clients_with_missing_cells;

        protected WebElement table_with_merged_cells;

        protected WebElement table_with_empty_headers;

        public StaticSitePage(WebDriver driver, int timeout) {
            super(driver, timeout);
        }

        public void setFirstName(String value) {
            element(firstName).type(value);
        }

        public void fieldDoesNotExistShouldNotBePresent() {
            element(fieldDoesNotExist).shouldNotBePresent();
        }

        public void fieldDoesNotExistShouldBePresent() {
            element(fieldDoesNotExist).shouldBePresent();
        }

        public void hiddenFieldShouldNotBePresent() {
            element(hiddenField).shouldNotBePresent();
        }

        public void fieldDoesNotExistShouldContainText(String value) {
            element(fieldDoesNotExist).shouldContainText(value);
        }

        public WebElementFacade firstName() {
            return element(firstName);
        }

        public void waitForFirstNameField() {
            waitForCondition().until(firstAndLastNameAreEnabled());
        }

        public ExpectedCondition<Boolean> firstNameIsVisibleAndDisabled() {
            return new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver driver) {
                    return (firstName.isDisplayed() && firstName.isEnabled());
                }
            };
        }

        public ExpectedCondition<Boolean> firstAndLastNameAreEnabled() {
            return new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver driver) {
                    return (firstName.isEnabled() && lastName.isEnabled());
                }
            };
        }

        public ExpectedCondition<Boolean> twoFieldsAreDisabled() {
            return new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver driver) {
                    return (!buttonThatIsInitiallyEnabled.isEnabled() && !readonlyField.isEnabled());
                }
            };
        }

    }
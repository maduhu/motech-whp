package org.motechproject.whp.functional.page.admin;

import org.apache.commons.lang.StringUtils;
import org.motechproject.whp.functional.framework.MyPageFactory;
import org.motechproject.whp.functional.framework.WebDriverFactory;
import org.motechproject.whp.functional.page.LoggedInUserPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import java.util.List;

import static org.openqa.selenium.By.id;

public class ListAllPatientsPage extends LoggedInUserPage {

    @FindBy(how = How.CLASS_NAME, using = "patientId")
    protected List<WebElement> patientIds;

    @FindBy(how = How.CLASS_NAME, using = "name")
    protected List<WebElement> patientNames;

    @FindBy(how = How.CLASS_NAME, using = "tbId")
    protected List<WebElement> tbIds;

    @FindBy(how = How.CLASS_NAME, using = "category")
    protected List<WebElement> categories;

    @FindBy(how = How.ID, using = "district")
    private WebElement districtSearchBox;

    @FindBy(how = How.ID, using = "providerId")
    private WebElement providerSearchBox;

    @FindBy(how = How.ID, using = "searchButton")
    private WebElement searchButton;

    @FindBy(how = How.CLASS_NAME, using = "link")
    private List<WebElement> dashboardLinks;


    public ListAllPatientsPage(WebDriver webDriver) {
        super(webDriver);
    }

    public boolean hasPatient(String patientName) {
        for (WebElement patientNameElement : patientNames) {
            if (StringUtils.containsIgnoreCase(patientNameElement.getText(), patientName)) {
                return true;
            }
        }
        return false;
    }

    public String getTreatmentCategoryText(String patientId) {
        return webDriver.findElement(id(String.format("patient_%s_TreatmentCategory", patientId))).getText();
    }

    public String getTreatmentStartDateText(String patientId) {
        return webDriver.findElement(id(String.format("patient_%s_TreatmentStartDate", patientId))).getText();
    }

    public String getGenderText(String patientId) {
        return webDriver.findElement(id(String.format("patient_%s_Gender", patientId))).getText();
    }

    public String getVillageText(String patientId) {
        return webDriver.findElement(id(String.format("patient_%s_Village", patientId))).getText();
    }

    public String getDistrictText(String patientId) {
        return webDriver.findElement(id(String.format("patient_%s_District", patientId))).getText();
    }

    public boolean isPatientTreatmentPaused(String patientId) {
        return webDriver.findElement(id(String.format("patientList_%s", patientId))).getCssValue("background-color").contains("255,182,193");
    }

    public PatientDashboardPage clickOnPatientWithTherapyNotYetStarted(String patientId) {
        int index = -1;
        for (int i = 0; i < patientIds.size(); i++) {
            if (patientIds.get(i).getText().equals(patientId)) {
                index = i;
                break;
            }
        }
        dashboardLinks.get(index).findElement(By.className("patientId")).click();
        return MyPageFactory.initElements(webDriver, PatientDashboardPage.class);
    }

    public TreatmentCardPage clickOnPatientWithStartedTherapy(String patientId) {
        int index = -1;
        for (int i = 0; i < patientIds.size(); i++) {
            if (patientIds.get(i).getText().equals(patientId)) {
                index = i;
                break;
            }
        }
        WebDriverFactory.createWebElement(dashboardLinks.get(index)).click();
        return MyPageFactory.initElements(webDriver, TreatmentCardPage.class);
    }

    public boolean hasTbId(String tbId) {
        for (WebElement tbIdElement : tbIds) {
            if (tbIdElement.getText().compareToIgnoreCase(tbId) == 0) {
                return true;
            }
        }
        return false;
    }

    public ListAllPatientsPage searchByDistrict(String district) {
        districtSearchBox.sendKeys(district);
        searchButton.click();
        waitForPatientsToLoad();
        return MyPageFactory.initElements(webDriver, ListAllPatientsPage.class);
    }

    public ListAllPatientsPage searchByDistrictAndProvider(String districtName, String providerId) {
        districtSearchBox.sendKeys(districtName);
        providerSearchBox.sendKeys(providerId);
        searchButton.click();
        waitForPatientsToLoad();
        return MyPageFactory.initElements(webDriver, ListAllPatientsPage.class);
    }

    private void waitForPatientsToLoad() {
        waitForElementToBeReloadedByAjax(id("patientList"));
    }
}
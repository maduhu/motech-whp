package org.motechproject.whp.functional.framework;

import org.motechproject.whp.functional.page.HtmlSection;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.PageFactory;

import java.lang.reflect.InvocationTargetException;

public class MyPageFactory {

    public static <T extends HtmlSection> T initElements(WebDriver driver, Class<T> pageClassToProxy) {
        try {
            T page = PageFactory.initElements(driver, pageClassToProxy);
            page.postInitialize();
            return page;
        } catch (RuntimeException e) {
            if (e.getCause() instanceof InvocationTargetException && ((InvocationTargetException) e.getCause()).getTargetException() instanceof WebDriverException) {
                String message = String.format("Most likely is caused by page transition didn't happen as a result of previous action. URL for current page: %s. Expected Page: %s", driver.getCurrentUrl(), pageClassToProxy.getName());
                throw new RuntimeException(message, e.getCause());
            }
            throw e;
        }
    }

    public static void initElements(WebDriver driver, HtmlSection pageObject) {
        try {
            PageFactory.initElements(driver, pageObject);
            pageObject.postInitialize();
        } catch (RuntimeException e) {
            if (e.getCause() instanceof InvocationTargetException && ((InvocationTargetException) e.getCause()).getTargetException() instanceof WebDriverException) {
                String message = String.format("Most likely is caused by page transition didn't happen as a result of previous action. URL for current page: %s. Expected Page: %s", driver.getCurrentUrl(), pageObject.getClass().getName());
                throw new RuntimeException(message, e.getCause());
            }
            throw e;
        }
    }

}

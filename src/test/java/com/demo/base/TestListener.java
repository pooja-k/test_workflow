package com.demo.base;

import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;
import java.io.File;

public class TestListener implements ITestListener {

    @Override
    public void onTestSuccess(ITestResult result) {
        attachScreenshotLink(result);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        attachScreenshotLink(result);
    }

    private void attachScreenshotLink(ITestResult result) {
        // Map the method names to the specific screenshots we save in the tests
        String screenshotName = "";
        if (result.getMethod().getMethodName().equals("testDarkModeEmulation")) {
            screenshotName = "dark-mode-check.png";
        }
        
        if (!screenshotName.isEmpty()) {
            // Check if the screenshot file actually exists in the report directory
            String filePath = "target/surefire-reports/" + screenshotName;
            File screenshot = new File(filePath);
            
            if (screenshot.exists()) {
                // Inject an HTML link into the TestNG Report log
                Reporter.log("<br><a href='" + screenshotName + "' target='_blank'><b>View Captured Screenshot</b></a><br>");
                // Optional: To embed the image directly on the page instead of a link, uncomment below:
                // Reporter.log("<br><img src='" + screenshotName + "' width='400' height='300'/><br>");
            }
        }
    }
}
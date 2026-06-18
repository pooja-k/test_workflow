package com.demo.base;

import com.microsoft.playwright.Page;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;
import java.io.File;
import java.nio.file.Paths;

public class TestListener implements ITestListener {

    @Override
    public void onTestSuccess(ITestResult result) {
        attachScreenshotLink(result);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        // Automatically capture screenshot on failure if not already done
        captureFailureScreenshot(result);
        attachScreenshotLink(result);
    }

    private void captureFailureScreenshot(ITestResult result) {
        Object instance = result.getInstance();
        if (instance instanceof BaseTest) {
            Page page = ((BaseTest) instance).page;
            if (page != null) {
                try {
                    String screenshotName = result.getMethod().getMethodName() + "-failure.png";
                    String filePath = "target/surefire-reports/" + screenshotName;
                    page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(filePath)));
                    System.out.println("Captured failure screenshot: " + filePath);
                } catch (Exception e) {
                    System.err.println("Failed to capture failure screenshot: " + e.getMessage());
                }
            }
        }
    }

    private void attachScreenshotLink(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        String screenshotName = methodName + "-failure.png";
        
        // Check if the failure screenshot file actually exists in the report directory
        String filePath = "target/surefire-reports/" + screenshotName;
        File screenshot = new File(filePath);
        
        if (screenshot.exists()) {
            // Inject an HTML link and embed the image directly in the TestNG Report log
            Reporter.log("<br><a href='" + screenshotName + "' target='_blank'><b>View Captured Failure Screenshot</b></a><br>");
            Reporter.log("<br><a href='" + screenshotName + "' target='_blank'><img src='" + screenshotName + "' width='600' style='border:1px solid #ccc; max-width:100%;'/></a><br>");
        }
    }
}
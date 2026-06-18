package com.demo.base;

import com.microsoft.playwright.Page;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Paths;

public class TestListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        Reporter.log("<br><span style='color: #0066cc; font-weight: bold;'>⏳ Starting Test: " + result.getMethod().getMethodName() + "</span>");
        String desc = result.getMethod().getDescription();
        if (desc != null && !desc.isEmpty()) {
            Reporter.log("<br>&nbsp;&nbsp;<i>Description: " + desc + "</i>");
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        long duration = result.getEndMillis() - result.getStartMillis();
        Reporter.log("<br><span style='color: #2ea44f; font-weight: bold;'>✔ Test Passed: " + result.getMethod().getMethodName() + "</span> (Duration: " + duration + "ms)");
        attachScreenshotLink(result);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        long duration = result.getEndMillis() - result.getStartMillis();
        Reporter.log("<br><span style='color: #cf222e; font-weight: bold;'>❌ Test Failed: " + result.getMethod().getMethodName() + "</span> (Duration: " + duration + "ms)");
        
        Throwable throwable = result.getThrowable();
        if (throwable != null) {
            Reporter.log("<br><span style='color: #cf222e; font-weight: bold;'>Error Details:</span>");
            Reporter.log("<pre style='background-color: #ffebe9; border: 1px solid #ffc1c0; padding: 10px; border-radius: 6px; overflow-x: auto; color: #cf222e; max-width: 100%; font-family: monospace; font-size: 12px;'>" 
                         + throwable.toString() + "\n" + getStackTrace(throwable) + "</pre>");
        }

        // Automatically capture screenshot on failure if not already done
        captureFailureScreenshot(result);
        attachScreenshotLink(result);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        Reporter.log("<br><span style='color: #8250df; font-weight: bold;'>⏭ Test Skipped: " + result.getMethod().getMethodName() + "</span>");
        Throwable throwable = result.getThrowable();
        if (throwable != null) {
            Reporter.log("<br><span style='color: #8250df;'>Reason: " + throwable.getMessage() + "</span>");
        }
    }

    private String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
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
            Reporter.log("<br><a href='" + screenshotName + "' target='_blank'><b>📷 View Captured Failure Screenshot</b></a><br>");
            Reporter.log("<br><a href='" + screenshotName + "' target='_blank'><img src='" + screenshotName + "' width='600' style='border: 1px solid #ddd; border-radius: 6px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); max-width: 100%;'/></a><br>");
        }
    }
}
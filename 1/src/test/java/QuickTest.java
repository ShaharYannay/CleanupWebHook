import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.IOSMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;


public class QuickTest {


    private String accessKey = "eyJ4cC51IjoyLCJ4cC5wIjoxLCJ4cC5tIjoiTVRVMk1UWTBNalkyTlRjMk1BIiwiYWxnIjoiSFMyNTYifQ.eyJleHAiOjE4NzcwMDI2NjUsImlzcyI6ImNvbS5leHBlcml0ZXN0In0.B3ptPrCxbg06_BubE9SQUNzWvDa0iNepN7bqOD0w4C4";
    protected IOSDriver<IOSElement> iosDriver = null;
    protected AndroidDriver<AndroidElement> androidDriver = null;

    DesiredCapabilities dc = new DesiredCapabilities();
    private String uid = System.getenv("deviceID");
    private String os = System.getenv("deviceOS");
    private String deviceName = System.getenv("deviceName");
    private String osVersion = System.getenv("osVersion");
    private String deviceModel = System.getenv("deviceModel");
    private String deviceManufacturer = System.getenv("deviceManufacturer");
    private String deviceCategory = System.getenv("deviceCategory");
    private String username = System.getenv("username");
    private String userProject = System.getenv("userProject");
    private String status = "failed";
    PrintWriter writer = new PrintWriter(deviceName + "_" + uid + ".txt", "UTF-8");

    public QuickTest() throws FileNotFoundException, UnsupportedEncodingException {
    }


    @Before
    public void setUp() throws Exception {
        printParameters();
        dc.setCapability("testName", "Cleanup Webhook Test");
        dc.setCapability("accessKey", accessKey);
        dc.setCapability("releaseDevice", false);
        dc.setCapability("deviceQuery", "@serialnumber='" + uid + "'");
        dc.setCapability("reportFormat", "xml");
        if (os.equals("iOS")){
            iOSDriver();
        }
        if (os.equals("Android")){
            androidDriver();
        }
    }

    @Test
    public void quickStartNativeDemo() {
        status = "passed";
    }

    @After
    public void tearDown() {

        if (iosDriver!=null) {
            System.out.println("Report URL: " + iosDriver.getCapabilities().getCapability("reportUrl"));
            iosDriver.quit();
        }
        if (androidDriver!=null) {
            System.out.println("Report URL: " + androidDriver.getCapabilities().getCapability("reportUrl"));
            androidDriver.quit();
        }

        sendResponseToCloud();

        writer.close();
    }


    private void iOSDriver() {
        dc.setCapability(IOSMobileCapabilityType.BUNDLE_ID, "com.experitest.ExperiBank");
        try {
            iosDriver = new IOSDriver<>(new URL("https://mastercloud.experitest.com/wd/hub"), dc);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void androidDriver() {
        dc.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, "com.experitest.ExperiBank");
        dc.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, ".LoginActivity");
        try {
            androidDriver = new AndroidDriver<>(new URL("https://mastercloud.experitest.com/wd/hub"), dc);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }


    private void sendResponseToCloud() {
        HttpPost post = new HttpPost("https://mastercloud.experitest.com/api/v1/cleanup-finish?deviceId=" + uid + "&status=" + status);
        post.addHeader(HttpHeaders.AUTHORIZATION, "bearer " + accessKey);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)) {
        } catch (Exception ignore){ }
    }

    private void printParameters(){
        writer.println("uid: " + uid);
        writer.println("os: " + os);
        writer.println("deviceName: " + deviceName);
        writer.println("osVersion: " + osVersion);
        writer.println("deviceModel: " + deviceModel);
        writer.println("deviceManufacturer: " + deviceManufacturer);
        writer.println("deviceCategory: " + deviceCategory);
        writer.println("username: " + username);
        writer.println("userProject: " + userProject);

    }
}

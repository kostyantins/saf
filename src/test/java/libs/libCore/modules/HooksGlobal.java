package libs.libCore.modules;

import cucumber.api.java.Before;
import org.openqa.selenium.support.events.EventFiringWebDriver;

public class HooksGlobal {

    private SharedContext ctx;

    // PicoContainer injects class SharedContext
    public HooksGlobal(SharedContext ctx) {
        this.ctx = ctx;
    }

    //http://automationpanda.com/2017/03/03/cucumber-jvm-global-hook-workarounds/
    //http://zsoltfabok.com/blog/2012/09/cucumber-jvm-hooks/
    //http://github.com/cucumber/cucumber-jvm/issues/515
    //http://github.com/cucumber/cucumber-jvm/tree/master/examples/java-webbit-websockets-selenium/src/test/java/cucumber/examples/java/websockets
    private static boolean dunit = false;

    @Before(order=10)
    public void beforeAll() {
        if( ! dunit ) {

            //afterAll hook
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {

                    // do the afterAll stuff...
                    Log.info("*** All features executed");
                    Storage Storage = ctx.Object.get("Storage", Storage.class);
                    Boolean closeWebDriver = Storage.get("Environment.Active.WebDrivers.CloseBrowserAfterScenario");
                    if ( ! closeWebDriver ) {
                        EventFiringWebDriver Page = ctx.Object.get("Page", EventFiringWebDriver.class);
                        if ( Page != null ) {
                            Log.debug("Driver cleanup started");
                            Page.close();
                            Page.quit();
                            Log.debug("Driver cleanup done");
                        }
                    }

                    Log.info("Test Suite execution ENDED!");
                }
            });

            //beforeAll hook
            // do the beforeAll stuff...
            Log.info("Test Suite execution STARTED!");
            PropertyReader.readSystemProperties();
            Log.info("*** Running features *** ");
            Log.info("");
            Log.info("");

            //
            dunit = true;
        }
    }
}
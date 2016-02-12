package com.wikia.webdriver.common.core.drivers;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.wikia.webdriver.common.core.WikiaWebDriver;
import com.wikia.webdriver.common.core.configuration.Configuration;
import com.wikia.webdriver.common.core.geoedge.GeoEdgeProxy;
import com.wikia.webdriver.common.core.networktrafficinterceptor.NetworkTrafficInterceptor;
import com.wikia.webdriver.common.logging.PageObjectLogging;

public abstract class BrowserAbstract {

  protected DesiredCapabilities caps = new DesiredCapabilities();
  protected NetworkTrafficInterceptor server;

  public WikiaWebDriver getInstance() {
    setOptions();
    setProxy();
    setExtensions();
    setBrowserLogging(Level.SEVERE);
    WikiaWebDriver webdriver = create();
    setTimeputs(webdriver);
    setListeners(webdriver);

    return webdriver;
  }

  public abstract void setOptions();

  public abstract WikiaWebDriver create();

  protected void setBrowserLogging(Level logLevel) {
    LoggingPreferences loggingprefs = new LoggingPreferences();
    loggingprefs.enable(LogType.BROWSER, logLevel);
    caps.setCapability(CapabilityType.LOGGING_PREFS, loggingprefs);
  }

  protected void setTimeputs(WebDriver webDriver) {
    webDriver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
  }

  protected void setListeners(WikiaWebDriver webDriver) {
    webDriver.register(new PageObjectLogging());
  }

  public abstract void addExtension(String extensionName);

  protected void setExtensions() {
    for (String name : Configuration.getExtensions()) {
      addExtension(name);
    }
  }

  protected void setProxy() {
    if (Configuration.useProxy()) {
      String countryCode = Configuration.getCountryCode();
      if (StringUtils.isNotBlank(countryCode)) {
        String proxyAddress = GeoEdgeProxy.getProxyAddress(countryCode);
        Proxy proxy = new Proxy();
        proxy.setHttpProxy(proxyAddress);
        proxy.setSslProxy(proxyAddress);
        caps.setCapability(CapabilityType.PROXY, proxy);
      } else {
        server = new NetworkTrafficInterceptor();
        server.startSeleniumProxyServer();
        caps.setCapability(CapabilityType.PROXY, server.seleniumProxy());
      }
    }
  }
}

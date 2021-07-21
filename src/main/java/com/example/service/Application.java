package com.example.service;

import com.example.service.configuration.ConfigParser;
import com.example.service.configuration.Configuration;
import com.example.service.util.Generics;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * The type Application.
 */
public abstract class Application <T extends Configuration> {

  private final Logger logger = LoggerFactory.getLogger(Application.class);

  private static final Pattern WINDOWS_NEWLINE = Pattern.compile("\\r\\n?");


  public Application() {
  }

  /**
   * Initialize.
   */
  public void initialize() {}

  /**
   * Initialize with config.
   *
   * @param config the config
   */
  public void initialize(T config) {}

  /**
   * When the application runs, this is called after the are run. Override it to add
   * resources, etc. for your application..
   *
   * @param config the config
   * @throws Exception the exception
   */
  public abstract void run(T config) throws Exception;

  /**
   * Returns the name of the application.
   *
   * @return the name
   */
  public String getName() {
    return getClass().getSimpleName();
  }

  /**
   * Print banner.
   *
   * @param name the name
   */
  protected void printBanner(String name) {
    try {
      final String banner = WINDOWS_NEWLINE.matcher(Resources.toString(Resources.getResource("banner.txt"),
                                                                       StandardCharsets.UTF_8))
                                           .replaceAll("\n")
                                           .replace("\n", String.format("%n"));

      logger.info(String.format("Starting {}%n{}"), name, banner);
    } catch (IllegalArgumentException | IOException ignored) {
      // don't display the banner if there isn't one
      logger.info("Starting {}", name);
    }
  }


  private void printGitInfo() throws IOException {
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    Properties props = new Properties();
    try(InputStream resourceStream = loader.getResourceAsStream("git.properties")) {
      props.load(resourceStream);
    }

    props.forEach((key, value) -> logger.info(key + ": " + value));
  }


  /**
   * Run Application.
   *
   * @param arguments the arguments
   * @throws Exception the exception
   */
  public void run(String ... arguments) throws Exception {

    printBanner(getName());
//    printGitInfo();

    String          configPath   = getConfigPath();
    ConfigParser<T> configParser = new ConfigParser<>(configPath);
    T               config       = configParser.readConfig(getConfigurationClass(), arguments);

    initialize();
    initialize(config);
//
    run(config);
  }

  private String getConfigPath(String ... args){
    return args.length != 0 && args[0] != null ? args[0] : "config/config.yml";
  }

  public Class<T> getConfigurationClass() {
    return Generics.getTypeParameter(getClass(), Configuration.class);
  }

}

package com.example.service.configuration;

import com.example.util.SystemYamlMapper;
import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * The type Config parser.
 */
public class ConfigParser<T> {

  private final Logger logger = LoggerFactory.getLogger(ConfigParser.class);

  private final Validator validator;
  private final String    configPath;

  public ConfigParser(String configPath) {
    ValidatorFactory validatorFactory = Validation
            .byProvider(HibernateValidator.class)
            .configure()
            .buildValidatorFactory();

    this.validator  = validatorFactory.getValidator();
    this.configPath = configPath;
  }

  /**
   * Read config Application configuration.
   */
  public T readConfig(Class<T> configClass, String... args) throws ConfigException {
    try {
      String configPath;
      logger.info("Reading config ..");

      if (args.length != 0 && args[0] != null) {
        configPath = args[0];
      } else {
        configPath = this.configPath;
      }

      File configFile = new File(configPath);

      if (!configFile.exists() && !configFile.isFile()) {
        logger.error("Config is not found!: (" + configFile.getAbsolutePath() + ")");
        throw new ConfigException("Config is not found!: (" + configFile.getAbsolutePath() + ")");
      }

      T config = SystemYamlMapper.getMapper().readValue(configFile, configClass);

      validate(config);

      logger.info("Config - OK");
      return config;
    } catch (IOException e) {
      throw new ConfigException("Cloud not parse config: " + e.getMessage());
    }
  }


  private void validate(T config) throws ConfigException {
    if (validator != null) {
      final Set<ConstraintViolation<T>> violations = validator.validate(config);
      if (!violations.isEmpty()) {
        System.out.println();
        throw new ConfigException(configPath, violations);
      }
    }
  }

}
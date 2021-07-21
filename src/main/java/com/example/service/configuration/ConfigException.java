package com.example.service.configuration;

import com.example.service.validation.ValidationMethod;

import javax.validation.ConstraintViolation;
import java.util.Collection;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class ConfigException extends Exception {


  /**
   * Instantiates a new Config exception.
   *
   * @param msg the msg
   */
  public ConfigException(String msg) {
    super(msg);
  }

  public <T> ConfigException(String path, Set<ConstraintViolation<T>> errors) {
    super(formatMessage(path, format(errors)));
  }


  public static <T> Collection<String> format(Set<ConstraintViolation<T>> violations) {
    final SortedSet<String> errors = new TreeSet<>();
    for (ConstraintViolation<?> v : violations) {
      errors.add(format(v));
    }
    return errors;
  }

  public static <T> String format(ConstraintViolation<T> v) {
    if (v.getConstraintDescriptor().getAnnotation() instanceof ValidationMethod) {
      return v.getMessage();
    } else {
      return String.format("%s %s", v.getPropertyPath(), v.getMessage());
    }
  }

  protected static String formatMessage(String file, Collection<String> errors) {
    final StringBuilder msg = new StringBuilder(file);
    msg.append(errors.size() == 1 ? " has an error:" : " has the following errors:").append(System.lineSeparator());
    for (String error : errors) {
      msg.append("  * ").append(error).append(System.lineSeparator());
    }
    return msg.toString();
  }
}

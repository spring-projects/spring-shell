package org.springframework.shell.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.shell.core.Converter;
import org.springframework.shell.core.JLineShellComponent;

/**
 * @author Jon Brisbin
 */
public class ConverterPostProcessor implements BeanPostProcessor {

  @Autowired
  private JLineShellComponent shell;

  public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    return bean;
  }

  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    if(bean instanceof Converter) {
      shell.getSimpleParser().add((Converter)bean);
    }
    return bean;
  }

}

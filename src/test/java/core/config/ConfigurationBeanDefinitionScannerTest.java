package core.config;

import core.di.factory.BeanFactory;
import core.jdbc.JdbcTemplate;
import next.config.MyConfiguration;
import org.junit.Assert;
import org.junit.Test;

import javax.sql.DataSource;

public class ConfigurationBeanDefinitionScannerTest {

    @Test
    public void getAnnotationInformation() {
        BeanFactory beanFactory = new BeanFactory();
        ConfigurationBeanDefinitionScanner scanner = new ConfigurationBeanDefinitionScanner(beanFactory);
        scanner.doScan(MyConfiguration.class);
        beanFactory.initialize();

        Assert.assertNotNull(beanFactory.getBean(DataSource.class));
        Assert.assertNotNull(beanFactory.getBean(JdbcTemplate.class));
    }
}

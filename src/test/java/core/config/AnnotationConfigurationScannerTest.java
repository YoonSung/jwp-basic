package core.config;

import core.di.factory.ApplicationContext;
import org.junit.Assert;
import org.junit.Test;

import javax.sql.DataSource;

public class AnnotationConfigurationScannerTest {

    @Test
    public void getAnnotationInformation() {
        AnnotationConfigurationScanner scanner = new AnnotationConfigurationScanner();
        ApplicationContext applicationContext = scanner.scan();
        Assert.assertNotNull(applicationContext.getBean(DataSource.class));
    }
}

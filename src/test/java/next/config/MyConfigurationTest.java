package next.config;

import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class MyConfigurationTest {

    @Test
    public void getReturnClassType() {
        for (Method method : MyConfiguration.class.getDeclaredMethods()) {
            System.out.println("method name : " + method.getName() + " / type : " + method.getReturnType());
        }
    }
}
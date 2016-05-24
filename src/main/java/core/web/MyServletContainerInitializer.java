package core.web;

import com.google.common.collect.Lists;
import core.web.mvc.WebApplicationInitializer;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import java.util.List;
import java.util.Set;

@HandlesTypes(WebApplicationInitializer.class)
public class MyServletContainerInitializer implements ServletContainerInitializer {
    @Override
    public void onStartup(Set<Class<?>> webInitializerClasses, ServletContext servletContext) throws ServletException {
        List<WebApplicationInitializer> initializerList = Lists.newLinkedList();

        if (webInitializerClasses != null) {
            for(Class<?> clazz : webInitializerClasses) {
                try {
                    initializerList.add((WebApplicationInitializer) clazz.newInstance());
                } catch (Throwable ex) {
                    throw new ServletException("Failed to instantiate WebApplicationInitializer class", ex);
                }
            }
        }

        if (initializerList.isEmpty()) {
            servletContext.log("No WebApplicationInitializer types detected on classpath");
            return;
        }

        for (WebApplicationInitializer initializer : initializerList) {
            initializer.onStartUp(servletContext);
        }
    }
}

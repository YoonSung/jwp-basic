package core.web.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public class MyWebApplicationInitializer implements WebApplicationInitializer {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void onStartUp(ServletContext servletContext) throws ServletException {
        AnnotationHandlerMapping ahm = new AnnotationHandlerMapping("next", "core");
        ahm.initialize();

        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", new DispatcherServlet(ahm));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("", "/");
        log.info("Start MyWebApplication Initializer");
    }
}

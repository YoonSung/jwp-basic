package core.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class HandlerExecution {
    private final Controller instance;
    private final Method method;

    public HandlerExecution(Controller instance, Method method) {
        this.instance = instance;
        this.method = method;
    }

    public ModelAndView execute(HttpServletRequest request,  HttpServletResponse response) throws Exception {
        return (ModelAndView) this.method.invoke(instance, new Object[]{request, response});
    }
}

package core.di.factory.example;

import core.annotation.Controller;
import core.annotation.Inject;
import core.web.mvc.AbstractNewController;

@Controller
public class FieldDIController extends AbstractNewController {

    @Inject
    private JdbcQuestionRepository jdbcQuestionRepository;

    public JdbcQuestionRepository getJdbcQuestionRepository() {
        return jdbcQuestionRepository;
    }
}

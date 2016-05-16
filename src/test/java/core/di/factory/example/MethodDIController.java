package core.di.factory.example;

import core.annotation.Controller;
import core.annotation.Inject;

@Controller
public class MethodDIController {

    JdbcQuestionRepository jdbcQuestionRepository;

    @Inject
    public void setJdbcQuestionRepository(JdbcQuestionRepository jdbcQuestionRepository) {
        this.jdbcQuestionRepository = jdbcQuestionRepository;
    }

    public JdbcQuestionRepository getJdbcQuestionRepository() {
        return jdbcQuestionRepository;
    }
}

package org.example;

import org.example.controllers.MathAssistantControllerTest;
import org.example.repository.DatabaseHelperTest;
import org.example.services.ExpressionEvaluatorTest;
import org.example.utils.StringToDoubleConverterTest;
import org.example.validators.MathEquationValidatorTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        DatabaseHelperTest.class,
        ExpressionEvaluatorTest.class,
        MathEquationValidatorTest.class,
        StringToDoubleConverterTest.class,
        MathAssistantControllerTest.class
})
public class MathAssistantTestsSuite {
}

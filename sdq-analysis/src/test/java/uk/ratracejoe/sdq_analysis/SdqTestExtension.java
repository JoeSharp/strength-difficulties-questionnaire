package uk.ratracejoe.sdq_analysis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.ratracejoe.sdq_analysis.service.DatabaseService;

public class SdqTestExtension implements BeforeEachCallback, AfterEachCallback {
    private static final Logger LOGGER = LoggerFactory.getLogger(SdqTestExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        deleteDatabase(context);
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        deleteDatabase(context);
    }

    private void deleteDatabase(ExtensionContext context) {
        ApplicationContext appContext = SpringExtension.getApplicationContext(context);
        DatabaseService service = appContext.getBean(DatabaseService.class);
        LOGGER.info("Ending SDQ Test");
        service.deleteDatabase();
    }
}

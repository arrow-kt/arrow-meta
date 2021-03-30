package arrow.meta.ide.internal;

import arrow.meta.ide.MetaIde;
import com.intellij.ide.ApplicationInitializedListener;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import kotlin.contracts.ExperimentalContracts;
import java.util.Objects;

/**
 * entry point of Meta in the Ide
 */
public class IdeRegistrar implements ApplicationInitializedListener {
    public static Logger LOG = Logger.getInstance("#arrow.AppRegistrar");

    @ExperimentalContracts
    @Override
    public void componentsInitialized() {
        LOG.info("componentsInitialized");
        Application app = Objects.requireNonNull(ApplicationManager.getApplication());
        app.getService(MetaIde.class).registerMetaIdeComponents(app);
    }
}
package pure.fsm.dropwizard.inmemory.bundle;

import io.dropwizard.Bundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import pure.fsm.core.cleanup.CleanUpFinalisedStateMachines;
import pure.fsm.core.cleanup.OnCleanupListener;
import pure.fsm.repository.inmemory.InMemoryTransitionRepository;

import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public abstract class StateMachineBundle implements Bundle {

    private InMemoryTransitionRepository repository;

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }

    @Override
    public void run(Environment environment) {
        repository = new InMemoryTransitionRepository();
    }

    public InMemoryTransitionRepository getRepository() {
        return repository;
    }

    public CleanUpFinalisedStateMachines createCleaner(Collection<OnCleanupListener> cleanupListeners,
                                                       long scheduleFrequency, TimeUnit scheduleTimeUnit,
                                                       long keepFinalised, ChronoUnit keepFinalisedTimeUnit) {

        return new CleanUpFinalisedStateMachines(repository, cleanupListeners, scheduleFrequency, scheduleTimeUnit, keepFinalised, keepFinalisedTimeUnit);
    }
}

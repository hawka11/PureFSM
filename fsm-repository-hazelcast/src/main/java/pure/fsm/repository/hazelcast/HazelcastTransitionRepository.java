package pure.fsm.repository.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.IMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.TransitionRepository;
import pure.fsm.core.Transition;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.google.common.collect.ImmutableSet.copyOf;
import static pure.fsm.core.Transition.initialTransition;

public class HazelcastTransitionRepository implements TransitionRepository {

    private final Logger LOG = LoggerFactory.getLogger(HazelcastTransitionRepository.class);

    private final HazelcastInstance hazelcastInstance;

    public HazelcastTransitionRepository(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String create(Object initialState, List<Object> initialContextData) {
        IAtomicLong idAtomicLong = getHazel().getAtomicLong("STATE_MACHINE_ID_GENERATOR");
        String id = String.valueOf(idAtomicLong.addAndGet(1));

        final Transition transition = initialTransition(id, initialState, initialContextData);

        getHolderMap().put(id, transition);

        return id;
    }

    @Override
    public Transition get(String stateMachineId) {
        return getHolderMap().get(stateMachineId);
    }

    @Override
    public Optional<Lock> tryLock(String stateMachineId, long timeout, TimeUnit timeUnit) {
        java.util.concurrent.locks.Lock distributedLock = getHazel().getLock("STATE_MACHINE-" + stateMachineId);

        try {
            if (distributedLock.tryLock(timeout, timeUnit)) {
                return createLock(stateMachineId, distributedLock);
            }
        } catch (InterruptedException e) {
            LOG.warn("Could not get HZ distributed distributedLock for state machine [" + stateMachineId + "]", e);
        }
        return Optional.empty();
    }

    @Override
    public Set<String> getIds() {
        return copyOf(getHolderMap().keySet());
    }

    private Optional<Lock> createLock(String stateMachineId, java.util.concurrent.locks.Lock distributedLock) {
        Lock lock = new Lock() {
            @Override
            public Transition getLast() {
                return getHolderMap().get(stateMachineId);
            }

            @Override
            public void update(Transition next) {
                getHolderMap().put(stateMachineId, next);
            }

            @Override
            public boolean unlock() {
                try {
                    distributedLock.unlock();
                    return true;
                } catch (IllegalMonitorStateException e) {
                    return false;
                }
            }

            @Override
            public boolean unlockAndRemove() {
                getHolderMap().remove(stateMachineId);
                return unlock();
            }
        };

        return Optional.of(lock);
    }

    private IMap<String, Transition> getHolderMap() {
        return getHazel().getMap("STATE_MACHINE_HOLDER");
    }

    private HazelcastInstance getHazel() {
        return hazelcastInstance;
    }
}

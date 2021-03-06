package pure.fsm.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static pure.fsm.core.Context.initialContext;

public class Transition {

    private final static Logger LOG = LoggerFactory.getLogger(Transition.class);

    @JsonSerialize
    private final LocalDateTime transitioned;

    @JsonSerialize
    private final String state;

    @JsonSerialize
    private final String event;

    @JsonSerialize
    private final Transition previous;

    @JsonSerialize
    private Context context;

    @JsonCreator
    private Transition(
            @JsonProperty("transitioned") LocalDateTime transitioned,
            @JsonProperty("state") String state,
            @JsonProperty("event") String event,
            @JsonProperty("previous") Transition previous,
            @JsonProperty("context") Context context) {
        this.transitioned = transitioned;
        this.state = state;
        this.event = event;
        this.previous = previous;
        this.context = context;
    }

    public static Transition initialTransition(String stateMachineId,
                                               Object initialState,
                                               List<Object> initialContexts) {

        final Context context = initialContext(stateMachineId, initialContexts);

        return new Transition(LocalDateTime.now(),
                initialState.getClass().getName(),
                "InitialEvent", null, context);
    }

    @JsonIgnore
    public Optional<Transition> previous() {
        return ofNullable(previous);
    }

    @JsonIgnore
    public String getEvent() {
        return event;
    }

    @JsonIgnore
    public Object getState() {
        return createInstance(this.state);
    }

    //assumes no-arg default constructor
    private Object createInstance(String className) {
        try {
            final Class<?> klass = Class.forName(className);
            final Constructor<?> c = klass.getDeclaredConstructors()[0];
            return setAccessable(c).newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Constructor<?> setAccessable(Constructor<?> constructor) {
        constructor.setAccessible(true);
        return constructor;
    }

    public LocalDateTime getTransitioned() {
        return transitioned;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public String toString() {
        return reflectionToString(this);
    }

    public static Transition To(Object state, Object event, Context context) {
        return Transition.To(state.getClass(), event, context);
    }

    public static Transition To(Class<?> state, Object event, Context context) {
        return new Transition(LocalDateTime.now(),
                state.getName(),
                event.getClass().getName(),
                null, context);
    }

    public Transition setPrevious(Transition previous) {
        return new Transition(this.transitioned, this.state, this.event, previous, this.context);
    }
}

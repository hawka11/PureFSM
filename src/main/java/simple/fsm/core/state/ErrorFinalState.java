package simple.fsm.core.state;

import simple.fsm.core.Context;
import simple.fsm.core.event.Event;

import java.time.LocalDateTime;

public class ErrorFinalState extends BaseFinalState {

    private final Exception e;
    private final LocalDateTime createdDateTime;

    public ErrorFinalState(Exception e) {
        this.e = e;
        this.createdDateTime = LocalDateTime.now();
    }

    @Override
    public LocalDateTime getCreated() {
        return createdDateTime;
    }

    @Override
    public State handle(Context context, Event event) {

        throw new IllegalStateException("In Error Final State, cannot process any more events");
    }
}

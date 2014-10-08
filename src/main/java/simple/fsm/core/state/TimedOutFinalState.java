package simple.fsm.core.state;

import simple.fsm.core.Context;
import simple.fsm.core.event.Event;

import java.time.LocalDateTime;

public class TimedOutFinalState extends BaseFinalState {

    private final String msg;
    private final LocalDateTime createdDateTime;

    public TimedOutFinalState(String msg) {
        this.msg = msg;
        this.createdDateTime = LocalDateTime.now();
    }

    @Override
    public LocalDateTime getCreated() {
        return createdDateTime;
    }

    @Override
    public State handle(Context context, Event event) {
        throw new IllegalStateException("In Timed Out Final State, cannot process any more events");
    }
}

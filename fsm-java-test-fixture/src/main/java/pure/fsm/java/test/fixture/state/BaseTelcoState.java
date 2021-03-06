package pure.fsm.java.test.fixture.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.Transition;
import pure.fsm.java.test.fixture.event.CancelRechargeEvent;
import pure.fsm.java.test.fixture.event.RechargeAcceptedEvent;
import pure.fsm.java.test.fixture.event.RequestRechargeEvent;
import pure.fsm.java.test.fixture.event.TelcoEventVisitor;
import pure.fsm.java.test.fixture.event.TimeoutTickEvent;
import pure.fsm.java.test.fixture.event.TelcoEvent;

import java.time.LocalDateTime;

import static pure.fsm.core.FinalState.TIMEOUT_ERROR_FINAL_STATE;
import static pure.fsm.core.context.ContextMessage.withMessage;

public class BaseTelcoState implements TelcoEventVisitor {

    public static final int TIMEOUT_SECS = 2;

    private final static Logger LOG = LoggerFactory.getLogger(BaseTelcoState.class);

    public Transition handle(Transition last, TelcoEvent event) {
        return event.accept(last, this);
    }

    @Override
    public Transition visit(Transition last, RequestRechargeEvent requestRechargeEvent) {
        return nonHandledEvent(last, requestRechargeEvent);
    }

    @Override
    public Transition visit(Transition last, CancelRechargeEvent cancelRechargeEvent) {
        return nonHandledEvent(last, cancelRechargeEvent);
    }

    @Override
    public Transition visit(Transition last, RechargeAcceptedEvent rechargeAcceptedEvent) {
        return nonHandledEvent(last, rechargeAcceptedEvent);
    }


    @Override
    public Transition visit(Transition last, TimeoutTickEvent timeoutTickEvent) {
        System.out.println("In " + getClass().getSimpleName() + ", processing TimeoutTickEvent event ");

        return isTimeout(last)
                ? Transition.To(TIMEOUT_ERROR_FINAL_STATE, timeoutTickEvent, last.getContext().appendState(withMessage("because timedout")))
                : last;
    }

    private boolean isTimeout(Transition last) {
        return LocalDateTime.now().isAfter(last.getTransitioned().plusSeconds(TIMEOUT_SECS));
    }

    private Transition nonHandledEvent(Transition last, TelcoEvent event) {
        LOG.warn("ignored event {}", event);
        return Transition.To(last.getState(), event, last.getContext());
    }
}

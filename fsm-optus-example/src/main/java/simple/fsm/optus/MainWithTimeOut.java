package simple.fsm.optus;

import simple.fsm.optus.event.RequestRechargeEvent;

import java.math.BigDecimal;

public class MainWithTimeOut {

    public static void main(String[] args) throws Exception {
        StateMachineOperations ops = new StateMachineOperations();

        //create state machine
        final String stateMachineId = ops.createStateMachineInInitialState();

        //One thread will send RequestRechargeEvent to sm
        ops.scheduleEventOnThread(stateMachineId, new RequestRechargeEvent(new BigDecimal("20.00")));
        Thread.sleep(6000);

        //something should configure this to run periodically
        ops.getTimeoutTicker().sendTimeOutTickerEvents();

        Thread.sleep(1000);

        //This 'current' state could be inspected by anything, which could react as desired / or send their own event to sm etc...
        ops.logCurrentState(stateMachineId);
    }
}

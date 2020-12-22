package griezma.springb.statemachine;

import griezma.springb.statemachine.model.Events;
import griezma.springb.statemachine.model.States;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;

@SpringBootTest
class StatemachineApplicationTests {
    @Autowired(required = false)
    private StateMachine<States, Events> machine;

    @Test
    void contextLoads() {
        machine.sendEvent(Events.E1);
        machine.sendEvent(Events.E2);
    }

}

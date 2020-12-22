package griezma.springb.statemachine;

import griezma.springb.statemachine.model.Events;
import griezma.springb.statemachine.model.States;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class StatemachineApplication {
    public static void main(String[] args) {
        SpringApplication.run(StatemachineApplication.class, args);
    }

}

@Component
@RequiredArgsConstructor
class StateMachineRunner implements CommandLineRunner {

    private final StateMachine<States, Events> stateMachine;

    @Override
    public void run(String... args) throws Exception {
        stateMachine.sendEvent(Events.E1);
        stateMachine.sendEvent(Events.E2);
    }
}

package griezma.springstatemachine.payment.services;

import griezma.springstatemachine.payment.actions.Authorize;
import griezma.springstatemachine.payment.actions.NotifyAuth;
import griezma.springstatemachine.payment.actions.NotifyPreAuth;
import griezma.springstatemachine.payment.actions.PreAuthorize;
import griezma.springstatemachine.payment.model.PaymentEvent;
import griezma.springstatemachine.payment.model.PaymentState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

@Slf4j
@Configuration
@EnableStateMachineFactory
@RequiredArgsConstructor
public class PaymentStateMachine extends EnumStateMachineConfigurerAdapter<PaymentState, PaymentEvent> {

    private final NotifyPreAuth notifyPreAuth;
    private final NotifyAuth notifyPaymentAuth;
    private final PreAuthorize preAuth;
    private final Authorize authorize;

    @Override
    public void configure(StateMachineConfigurationConfigurer<PaymentState, PaymentEvent> config) throws Exception {
        config.withConfiguration()
                .autoStartup(true)
                .listener(smListener());
    }

    @Bean
    StateMachineListener<PaymentState, PaymentEvent> smListener() {
        return new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(State from, State to) {
                log.debug("state changed to {} from {}", to, from);
            }
        };
    }

    @Override
    public void configure(StateMachineStateConfigurer<PaymentState, PaymentEvent> states) throws Exception {
        states.withStates()
                .initial(PaymentState.NEW)
                .states(EnumSet.allOf(PaymentState.class))
                .end(PaymentState.AUTH)
                .end(PaymentState.PRE_AUTH_ERROR)
                .end(PaymentState.AUTH_ERROR);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PaymentState, PaymentEvent> transitions) throws Exception {
        transitions.withExternal()
                    .source(PaymentState.NEW)
                    .event(PaymentEvent.PRE_AUTHORIZE)
                    .target(PaymentState.NEW)
                    .action(preAuth)
                    .guard(paymentIdIncluded())
                .and().withExternal()
                    .source(PaymentState.NEW)
                    .event(PaymentEvent.PRE_AUTH_APPROVED)
                    .target(PaymentState.PRE_AUTH)
                    .action(notifyPreAuth)
                .and().withExternal()
                    .source(PaymentState.NEW)
                    .event(PaymentEvent.PRE_AUTH_DECLINED)
                    .target(PaymentState.PRE_AUTH_ERROR)
                    .action(notifyPreAuth)
                .and().withExternal()
                    .source(PaymentState.PRE_AUTH)
                    .event(PaymentEvent.AUTHORIZE)
                    .target(PaymentState.PRE_AUTH)
                    .action(authorize)
                    .guard(paymentIdIncluded())
                .and().withExternal()
                    .source(PaymentState.PRE_AUTH)
                    .event(PaymentEvent.AUTH_DECLINED)
                    .target(PaymentState.AUTH_ERROR)
                    .action(notifyPaymentAuth)
                .and().withExternal()
                    .source(PaymentState.PRE_AUTH)
                    .event(PaymentEvent.AUTH_APPROVED)
                    .target(PaymentState.AUTH)
                    .action(notifyPaymentAuth);
    }

    @Bean
    Guard<PaymentState, PaymentEvent> paymentIdIncluded() {
        return context -> context.getMessageHeader(PaymentService.PAYMENT_ID_HEADER) != null;
    }
}


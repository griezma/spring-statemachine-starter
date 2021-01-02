package griezma.springstatemachine.payment.services;

import griezma.springstatemachine.payment.data.PaymentRepository;
import griezma.springstatemachine.payment.model.Payment;
import griezma.springstatemachine.payment.model.PaymentEvent;
import griezma.springstatemachine.payment.model.PaymentState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StateMachineService {
    private final StateMachineFactory<PaymentState, PaymentEvent> factory;
    private final PaymentStatePersister persistIntercepter;

    public StateMachine<PaymentState, PaymentEvent> getForPayment(Payment payment) {
        log.debug("getForPayment: {}", payment);
        var sm = factory.getStateMachine(payment.getId().toString());
        sm.stop();
        sm.getStateMachineAccessor()
                .doWithAllRegions(accessor -> {
                    accessor.addStateMachineInterceptor(persistIntercepter);
                    accessor.resetStateMachine(new DefaultStateMachineContext<>(payment.getState(), null, null, null));
                });
        sm.start();
        return sm;
    }
}
@Slf4j
@Component
@RequiredArgsConstructor
class PaymentStatePersister extends StateMachineInterceptorAdapter<PaymentState, PaymentEvent> {
    private final PaymentRepository repo;

    @Transactional
    @Override
    public void preStateChange(State<PaymentState, PaymentEvent> state, Message<PaymentEvent> message, Transition<PaymentState, PaymentEvent> transition, StateMachine<PaymentState, PaymentEvent> stateMachine) {
        if (message == null) return;

        Long paymentId = message.getHeaders().get(PaymentService.PAYMENT_ID_HEADER, Long.class);
        if (paymentId == null) return;

        log.debug("preStateChange: {} {}", paymentId, state.getId());

        Payment payment = repo.getOne(paymentId);
        payment.setState(state.getId());
        repo.save(payment);
    }
}

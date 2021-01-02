package griezma.springstatemachine.payment.services;

import griezma.springstatemachine.payment.data.PaymentRepository;
import griezma.springstatemachine.payment.model.Payment;
import griezma.springstatemachine.payment.model.PaymentEvent;
import griezma.springstatemachine.payment.model.PaymentState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    public static final String PAYMENT_ID_HEADER = "payment_id";

    private final StateMachineService paymentStateMachine;

    private final PaymentRepository repo;

    public Payment newPayment(Payment payment) {
        return repo.save(payment);
    }

    public void preAuthorize(Long paymentId) {
        log.debug("preAuthorize: {} ", paymentId);
        sendEvent(paymentId, PaymentEvent.PRE_AUTHORIZE);
    }

    public void authorize(Long paymentId) {
        log.debug("authorize: {} ", paymentId);
        sendEvent(paymentId, PaymentEvent.AUTHORIZE);
    }

    StateMachine<PaymentState, PaymentEvent> sendEvent(Long paymentId, PaymentEvent event) {
        Payment payment = repo.findById(paymentId).orElseThrow();
        Message eventMessage = MessageBuilder.withPayload(event)
                .setHeader(PAYMENT_ID_HEADER, paymentId)
                .build();
        var sm =  paymentStateMachine.getForPayment(payment);
        sm.sendEvent(eventMessage);
        return sm;
    }

    public Payment getPayment(Long paymentId) {
        return repo.getOne(paymentId);
    }
}

package griezma.springstatemachine.payment.actions;

import griezma.springstatemachine.payment.model.PaymentEvent;
import griezma.springstatemachine.payment.model.PaymentState;
import griezma.springstatemachine.payment.services.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component
public class Authorize implements Action<PaymentState, PaymentEvent> {
    @Override
    public void execute(StateContext<PaymentState, PaymentEvent> context) {
        log.debug("authorizeAction: {}", context);

        PaymentEvent event;
        if (new Random().nextInt() < 8) {
            event = PaymentEvent.AUTH_APPROVED;
        } else {
            event = PaymentEvent.AUTH_DECLINED;
        }
        Message message = MessageBuilder
                .withPayload(event)
                .setHeader(PaymentService.PAYMENT_ID_HEADER, context.getMessageHeader(PaymentService.PAYMENT_ID_HEADER))
                .build();
        context.getStateMachine().sendEvent(message);
    }
}

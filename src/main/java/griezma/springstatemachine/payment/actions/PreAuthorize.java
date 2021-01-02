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
public class PreAuthorize implements Action<PaymentState, PaymentEvent> {
    @Override
    public void execute(StateContext<PaymentState, PaymentEvent> context) {
        log.debug("preAuthAction: {}", context);

        PaymentEvent event;
        if (new Random().nextInt() < 8) {
            event = PaymentEvent.PRE_AUTH_APPROVED;
        } else {
            event = PaymentEvent.PRE_AUTH_DECLINED;
        }
        Message message = MessageBuilder
                .withPayload(event)
                .setHeader(PaymentService.PAYMENT_ID_HEADER, context.getMessageHeader(PaymentService.PAYMENT_ID_HEADER))
                .build();
        context.getStateMachine().sendEvent(message);
    }
}

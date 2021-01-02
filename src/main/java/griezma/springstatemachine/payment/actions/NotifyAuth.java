package griezma.springstatemachine.payment.actions;

import griezma.springstatemachine.payment.model.PaymentEvent;
import griezma.springstatemachine.payment.model.PaymentState;
import griezma.springstatemachine.payment.services.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotifyAuth implements Action<PaymentState, PaymentEvent> {

    @Override
    public void execute(StateContext<PaymentState, PaymentEvent> context) {
        log.debug("payment={}, result={}", context.getMessageHeader(PaymentService.PAYMENT_ID_HEADER), context.getTarget().getId());
    }
}
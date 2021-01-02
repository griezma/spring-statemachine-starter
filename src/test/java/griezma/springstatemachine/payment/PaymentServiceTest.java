package griezma.springstatemachine.payment;

import griezma.springstatemachine.payment.model.Currency;
import griezma.springstatemachine.payment.model.Payment;
import griezma.springstatemachine.payment.model.PaymentState;
import griezma.springstatemachine.payment.services.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.math.BigDecimal;

import static griezma.springstatemachine.payment.model.PaymentState.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SpringBootTest
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Test
    void transitionNewToPreAuth() {
        Payment p = newPayment();
        assertEquals(PaymentState.NEW, p.getState());

        paymentService.preAuthorize(p.getId());

        Payment payment = paymentService.getPayment(p.getId());
        assertThat(payment.getState(), anyOf(is(PRE_AUTH_ERROR), is(PRE_AUTH)));
    }

    @Test
    void transitionPreAuthToAuth() {
        Payment p = preAuthPayment();
        assertEquals(PRE_AUTH, p.getState());
        paymentService.authorize(p.getId());
        Payment payment = paymentService.getPayment(p.getId());
        assertThat(payment.getState(), anyOf(is(AUTH_ERROR), is(AUTH)));
    }

    Payment newPayment() {
        Payment payment = Payment.builder()
                .currency(Currency.EUR)
                .amount(BigDecimal.valueOf(12.99))
                .build();
        return paymentService.newPayment(payment);
    }

    @Transactional
    public Payment preAuthPayment() {
        Payment payment = Payment.builder()
                .currency(Currency.EUR)
                .amount(BigDecimal.valueOf(13))
                .state(PRE_AUTH)
                .build();
        payment = paymentService.newPayment(payment);
        return payment;
    }
}

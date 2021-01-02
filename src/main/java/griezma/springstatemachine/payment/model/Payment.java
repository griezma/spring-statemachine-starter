package griezma.springstatemachine.payment.model;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Builder @Getter @ToString @NoArgsConstructor @AllArgsConstructor
public class Payment {

    @Id @GeneratedValue
    private Long id;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Setter @Builder.Default
    private PaymentState state = PaymentState.NEW;

}

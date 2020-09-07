package me.sup2is.order.client;

import me.sup2is.order.exception.PaymentFailureException;
import org.springframework.stereotype.Component;

@Component
public class PaymentModuleClient {

    public void payment() {
        //실제 모듈을 태우지는 않고 약 1~10초간격으로, 1/10 확률로 결제 실패, 9/10 확률로 결제 완료
        int random = (int)(Math.random() * 10) + 1;
        try {
            Thread.sleep(random);
            if(random == 5) {
                throw new PaymentFailureException();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (PaymentFailureException e) {
            e.printStackTrace();
        }

    }

}

package top.mushanyu.config.transaction;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
public class InvokerWithTransaction {

    private final TransactionTemplate TRANSACTION_TEMPLATE;

    private InvokerWithTransaction() {
        this.TRANSACTION_TEMPLATE = new TransactionTemplate(SpringUtil.getBean(PlatformTransactionManager.class),
                new RequiresNewTransactionDefinition());
        this.TRANSACTION_TEMPLATE.afterPropertiesSet();
    }


    public static void invoke(Runnable run) {
        new InvokerWithTransaction().TRANSACTION_TEMPLATE.executeWithoutResult(status -> {
            try {
                run.run();
            } catch (Throwable e) {
                log.error(e.getMessage(), e);
            }
        });

    }


    static class RequiresNewTransactionDefinition implements TransactionDefinition {
        @Override
        public int getPropagationBehavior() {
            return TransactionDefinition.PROPAGATION_REQUIRES_NEW;
        }
    }
}

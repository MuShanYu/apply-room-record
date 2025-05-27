package top.mushanyu.config.transaction;


import cn.hutool.extra.spring.SpringUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.function.SingletonSupplier;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

@Aspect
@Component
public class SpringTxAspect {


    public static final String TX_LOCAL_POINTCUT = """
            (execution(public * top.mushanyu..*.api..*.*(..)))
            && !@annotation(org.springframework.transaction.annotation.Transactional)
            """;

    public static final SingletonSupplier<NameMatchTransactionAttributeSource> TRANSACTION_ATTRIBUTE_SOURCE = SingletonSupplier.of(() -> {
        var readOnlyTx = new RuleBasedTransactionAttribute(TransactionDefinition.PROPAGATION_SUPPORTS, List.of());
        readOnlyTx.setReadOnly(true);

        var requiredTx = new RuleBasedTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRED, List.of());

        var source = new NameMatchTransactionAttributeSource();
        source.addTransactionalMethod("get*", readOnlyTx);
        source.addTransactionalMethod("find*", readOnlyTx);
        source.addTransactionalMethod("query*", readOnlyTx);
        source.addTransactionalMethod("*", requiredTx);

        return source;
    });

    @Pointcut(TX_LOCAL_POINTCUT)
    public void txLocalPointcut() {
    }


    @Around("txLocalPointcut()")
    public Object txAround(ProceedingJoinPoint pjp) throws Throwable {

        var attr = Objects.requireNonNull(TRANSACTION_ATTRIBUTE_SOURCE.get())
                .getTransactionAttribute(getMethod(pjp), pjp.getTarget().getClass());
        if (attr == null) {
            return pjp.proceed();
        }

        var tem = new TransactionTemplate(SpringUtil.getBean(PlatformTransactionManager.class), attr);
        return tem.execute(status -> {
            try {
                return pjp.proceed();
            } catch (Throwable e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                } else {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    private Method getMethod(ProceedingJoinPoint pjp) throws NoSuchMethodException {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        return pjp.getTarget().getClass().getMethod(method.getName(), method.getParameterTypes());
    }

}
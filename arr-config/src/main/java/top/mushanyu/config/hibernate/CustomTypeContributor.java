package top.mushanyu.config.hibernate;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.boot.model.TypeContributor;
import org.hibernate.service.ServiceRegistry;


public class CustomTypeContributor implements TypeContributor {

    @Override
    public void contribute(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
        typeContributions.contributeType(new BooleanToIntegerUserType());
    }

}

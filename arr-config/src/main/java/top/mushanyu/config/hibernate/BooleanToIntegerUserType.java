package top.mushanyu.config.hibernate;

import org.hibernate.type.descriptor.java.BooleanJavaType;
import org.hibernate.type.descriptor.jdbc.IntegerJdbcType;
import org.hibernate.usertype.StaticUserTypeSupport;

public class BooleanToIntegerUserType extends StaticUserTypeSupport<Boolean> {

    public BooleanToIntegerUserType() {
        super(BooleanJavaType.INSTANCE, IntegerJdbcType.INSTANCE);
    }
}

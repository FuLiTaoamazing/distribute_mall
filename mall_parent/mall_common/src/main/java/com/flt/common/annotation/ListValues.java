package com.flt.common.annotation;

import com.flt.common.valid.ListValuesConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

// 自定义的form表单valid注解
// 主要实现共能是要求value出现在values中 就是做值限定
//@Constraint的作用是指定用于哪个验证器 这里可以指定多个校验器 来满足不同数据类型的校验
@Constraint(
        validatedBy = {ListValuesConstraintValidator.class}
)
//@Traget 注解的作用是能作用在类的哪里 ，下面是 方法 属性 注解上 构造器
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
//@Retention 表示运行时的环境
@Retention(RetentionPolicy.RUNTIME)
public @interface ListValues {
    //这里的默认properties是JRS303规范中提供的我们可以自定义个自己的properties
    //系统默认的这个消息文件的全程名尾 ValidationMessages.properties
    String message() default "{com.flt.common.annotation.ListValues.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int[] values() default {};
}

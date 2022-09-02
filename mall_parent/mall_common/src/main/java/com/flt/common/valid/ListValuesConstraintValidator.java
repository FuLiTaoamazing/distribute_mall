package com.flt.common.valid;

import com.flt.common.annotation.ListValues;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

//自定义校验注解的实际校验器
public class ListValuesConstraintValidator implements ConstraintValidator<ListValues,Integer> {
    //这里是初始化的额方法 可以获取到注解上的values或者其他信息
    private Set<Integer> set;
    @Override
    public void initialize(ListValues constraintAnnotation) {
    this.set=new HashSet<>();
        int[] values = constraintAnnotation.values();
        for (int value : values) {
            this.set.add(value);
        }
    }
    //实际校验的规则是写在这里 放行就return true  不放行就是return false
    // integer:代表实际的值
    // constrainValidatorContext :代表校验器的上下文
    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
        return this.set.contains(integer);
    }
}

package cn.hbjx.alib.network;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by DengYiQian on 2017/6/6.
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ARequestType {

    String type() default "get";

    Http.DataType dataType() default Http.DataType.TYPE1_FORM;

    Http.NetType netType() default Http.NetType.HTTP;

    String url() default "";

}

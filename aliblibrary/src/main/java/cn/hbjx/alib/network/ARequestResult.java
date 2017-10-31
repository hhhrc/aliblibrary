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
public @interface ARequestResult {

    boolean root() default false;

    boolean md5() default false;

    boolean base64() default false;

    boolean des() default false;

}

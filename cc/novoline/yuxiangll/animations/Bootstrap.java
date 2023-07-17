package cn.YolBi.Lite.util.yuxiangll.aniations;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

@Retention(CLASS)
@Target({ METHOD })
public @interface Bootstrap {

}

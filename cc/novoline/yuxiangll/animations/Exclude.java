package cc.novoline.yuxiangll.animations;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(CLASS)
@Target({ TYPE, FIELD, METHOD, CONSTRUCTOR })
public @interface Exclude {

	public Strategy[] value() default {Strategy.NO_STRATEGY};
	
}

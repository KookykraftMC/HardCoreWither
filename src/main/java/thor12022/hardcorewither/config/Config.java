package thor12022.hardcorewither.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Config
{
   float minFloat() default 0.0f;
   
   float maxFloat() default Float.MAX_VALUE;
   
   int minInt() default 0;
   
   int maxInt() default Integer.MAX_VALUE;
   
   String comment() default "";
   
   String fieldName() default "";
}

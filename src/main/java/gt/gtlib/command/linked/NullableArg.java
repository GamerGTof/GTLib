package gt.gtlib.command.linked;

import java.lang.annotation.*;

/**
 * Marks a method argument as nullable, that means its value can be optional when executing the command. And, sure, also that the value of the annotated parameter might be {@code null}.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface NullableArg {
}

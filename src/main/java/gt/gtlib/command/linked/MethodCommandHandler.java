package gt.gtlib.command.linked;

import java.lang.annotation.*;

/**
 * Marks a method as a handler for the root of the command.
 * That means the method name can be any, as it won't be used in the command itself as in {@link SubCommandHandler}.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface MethodCommandHandler {
}

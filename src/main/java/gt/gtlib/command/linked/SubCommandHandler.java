package gt.gtlib.command.linked;

import java.lang.annotation.*;

/**
 * Marks a method as a sub-command-handler. That means the method name will be used as the name for the sub-command.
 * Parent should be the name of another sub-command-method, in the same object.
 * As an example, a method called "live" marked with {@link SubCommandHandler}, would be called as: {@code /gt live [arguments]} - being 'gt' the name of the command.
 * To describe the use of 'parent', if we have another function called 'life', also annotated with {@link SubCommandHandler} and parent set to "live", the command would be called as: {@code /gt live life [arguments]}.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface SubCommandHandler {
    String parent() default "";
}

package org.learn.quarkus.exception;

import org.hibernate.exception.ConstraintViolationException;

public class ExceptionUtil {

    public static boolean isConstraintViolation(Throwable e) {
        while (e != null) {
            if (e instanceof ConstraintViolationException) {
                return true;
            }
            e = e.getCause();
        }
        return false;
    }

}

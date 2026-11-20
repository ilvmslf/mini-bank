package bank.adapters;

import bank.application.annotation.Audited;
import bank.application.annotation.RequiresRole;

import java.lang.reflect.Method;

public class AnnotationInspector {

    public void inspect(Class<?> type) {

        if (type == null) {
            throw new IllegalArgumentException(
                    "Type must not be null"
            );
        }

        System.out.println(
                "Class: " + type.getName()
        );

        boolean audited =
                type.isAnnotationPresent(
                        Audited.class
                );

        System.out.println(
                "Audited: " + audited
        );

        System.out.println(
                "Declared methods:"
        );

        for (Method method :
                type.getDeclaredMethods()) {

            System.out.println(
                    "- " + method.getName()
            );
        }

        System.out.println(
                "Methods with @RequiresRole:"
        );

        for (Method method :
                type.getDeclaredMethods()) {

            RequiresRole role =
                    method.getAnnotation(
                            RequiresRole.class
                    );

            if (role != null) {
                System.out.println(
                        method.getName()
                                + " -> "
                                + role.value()
                );
            }
        }
    }
}
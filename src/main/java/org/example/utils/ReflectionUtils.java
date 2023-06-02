package org.example.utils;

public class ReflectionUtils {
    public static boolean isJDKOrPrimitive(Class<?> clazz) {
        return clazz.isPrimitive() || clazz.getName().startsWith("java.lang");
    }

    public static String printJDK(Class<?> clazz){

        if(clazz.getTypeName().equals("java.lang.Boolean"))
            return "boolean";
        if(clazz.getTypeName().equals("java.lang.Character"))
            return "char";
        if(clazz.getTypeName().equals("java.lang.Integer"))
            return "int";

        return clazz.getSimpleName();
    }

    public static int getArrayDimension(Class<?> clazz){
        int dimension = 0;
        while (clazz.isArray()) {
            dimension++;
            clazz = clazz.getComponentType();
        }
        return dimension;
    }
}

package org.example.processinfutils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static org.example.reflection.Reflection.simpleFields;
import static org.example.reflection.Reflection.typeChecker;
import static org.example.utils.ReflectionUtils.getArrayDimension;
import static org.example.utils.ReflectionUtils.isJDKOrPrimitive;

public class Processing {

    public static void processingInnerClass(Class clazz, StringBuilder jsonBuilder, Field[] fields) {
        Class[] innerClass = clazz.getDeclaredClasses();
        for ( int k=0; k < innerClass.length; k++) {
            jsonBuilder.append("\"")
                       .append(innerClass[k].getSimpleName())
                       .append("\" : [");
            typeChecker(innerClass[k],jsonBuilder);
            if (k==innerClass.length-1 && fields.length==0)
                jsonBuilder.append("]");
            else
                jsonBuilder.append("]")
                           .append(",");
        }
    }

    public static void processingCollection(Type fieldGenericType, StringBuilder jsonBuilder, String fieldName , Field[] fields, int i) {
        if (fieldGenericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType =
                    (ParameterizedType) fieldGenericType;
            Type[] typeArguments =
                    parameterizedType.getActualTypeArguments();
            if (typeArguments.length == 1) {
                Type collectionElementType = typeArguments[0];
                jsonBuilder.append("\"")
                        .append(fieldName)
                        .append("\": [");
                simpleFields(collectionElementType, jsonBuilder);
                jsonBuilder.append(", \"...\"]");
                if (i != fields.length - 1)
                    jsonBuilder.append(",");

            }
        }
    }

    public static void processingMap(Type fieldGenericType, StringBuilder jsonBuilder, String fieldName , Field[] fields, int i) {
        if (fieldGenericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType =
                    (ParameterizedType) fieldGenericType;
            Type[] typeArguments =
                    parameterizedType.getActualTypeArguments();
            if (typeArguments.length == 2) {
                Type mapKeyType = typeArguments[0];
                Type mapValueType = typeArguments[1];
                jsonBuilder.append("\"")
                        .append(fieldName)
                        .append("\": [{");
                if ((Class)mapKeyType instanceof Class && !isJDKOrPrimitive((Class) mapKeyType)){
                    Class mapKeyClass = (Class)mapKeyType;
                    jsonBuilder.append("\"")
                            .append(mapKeyClass.getSimpleName())
                            .append("\"");
                }
                else {
                    simpleFields(mapKeyType, jsonBuilder);
                }
                jsonBuilder.append(": ");
                simpleFields(mapValueType, jsonBuilder);
                jsonBuilder.append("}, \"...\"]");
                if (i != fields.length - 1)
                    jsonBuilder.append(",");

            }
        }
    }

    public static void processingArray(Class fieldType, StringBuilder jsonBuilder, String fieldName , Field[] fields, int i) {
        String elementType = fieldType.getSimpleName();
        int arrayDimension = getArrayDimension(fieldType);
        String elementSimpleType =
                elementType.substring(0,elementType.length() -arrayDimension*2);
        switch (elementSimpleType){
            case "Integer":
                elementSimpleType = "int";
                break;
            case "Character":
                elementSimpleType = "char";
                break;
            case "Boolean":
                elementSimpleType = "boolean";
                break;
        }

        jsonBuilder.append("\"")
                .append(fieldName)
                .append("\" : ");
        for (int j = 0; j < arrayDimension; j++) {
            jsonBuilder.append("[");
        }
        jsonBuilder.append("\"")
                .append(elementSimpleType)
                .append("\",");
        for (int j = 0; j < arrayDimension; j++) {
            jsonBuilder.append("\"...\"]");
            if (j!= arrayDimension - 1)
                jsonBuilder.append(",");
        }
        if (i!=fields.length-1)
            jsonBuilder.append(",");
    }
}

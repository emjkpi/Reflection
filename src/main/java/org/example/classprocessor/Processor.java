package org.example.classprocessor;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static org.example.reflection.Reflection.simpleFields;
import static org.example.reflection.Reflection.typeChecker;
import static org.example.utils.ReflectionUtils.getArrayDimension;
import static org.example.utils.ReflectionUtils.isJDKOrPrimitive;

public class Processor {

    public static StringBuilder processingInnerClass(Class clazz,
                                            Field[] fields) {

        StringBuilder jsonBuilder = new StringBuilder();
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
        return jsonBuilder;
    }

    public static StringBuilder processingCollection(Type fieldGenericType,
                                            String fieldName ,
                                            Field[] fields, int i) {

        StringBuilder jsonBuilder = new StringBuilder();
        if (fieldGenericType instanceof ParameterizedType) {
            Type[] typeArguments =
                    ((ParameterizedType)fieldGenericType).getActualTypeArguments();
            if (typeArguments.length == 1) {
                Type collectionElementType = typeArguments[0];
                jsonBuilder.append("\"")
                        .append(fieldName)
                        .append("\": [")
                        .append(simpleFields(collectionElementType))
                        .append(", \"...\"]");
                if (i != fields.length - 1)
                    jsonBuilder.append(",");

            }
        }
        return jsonBuilder;
    }

    public static StringBuilder processingMap(Type fieldGenericType,
                                     String fieldName ,
                                     Field[] fields, int i) {

        StringBuilder jsonBuilder = new StringBuilder();
        if (fieldGenericType instanceof ParameterizedType) {
            Type[] typeArguments =
                    ((ParameterizedType)fieldGenericType).getActualTypeArguments();
            if (typeArguments.length == 2) {
                Type mapKeyType = typeArguments[0];
                Type mapValueType = typeArguments[1];
                jsonBuilder.append("\"")
                        .append(fieldName)
                        .append("\": [{");
                if ((Class)mapKeyType instanceof Class &&
                        !isJDKOrPrimitive((Class) mapKeyType)){

                    Class mapKeyClass = (Class)mapKeyType;
                    jsonBuilder.append("\"")
                            .append(mapKeyClass.getSimpleName())
                            .append("\"");
                }
                else {
                    jsonBuilder.append(simpleFields(mapKeyType));
                }
                jsonBuilder.append(": ")
                        .append(simpleFields(mapValueType))
                        .append("}, \"...\"]");
                if (i != fields.length - 1)
                    jsonBuilder.append(",");

            }
        }
        return jsonBuilder;
    }

    public static void processingArray(Class fieldType,
                                       StringBuilder jsonBuilder,
                                       String fieldName ,
                                       Field[] fields, int i) {

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

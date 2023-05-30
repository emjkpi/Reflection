package org.example.reflectionutils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

public class Reflection {

    public static String classReflection(Class clazz) {
        StringBuilder jsonBuilder = new StringBuilder();
        parameterizedFields(clazz, jsonBuilder);

        return jsonBuilder.toString();
    }


    public static void parameterizedFields(Class clazz, StringBuilder jsonBuilder) {
        jsonBuilder.append("{");
        Field[] fields = clazz.getDeclaredFields();
        Class[] innerClass = clazz.getDeclaredClasses();
        for ( int k=0; k < innerClass.length; k++) {
            jsonBuilder.append("\"")
                    .append(innerClass[k].getSimpleName()).append("\" : [");
            parameterizedFields(innerClass[k],jsonBuilder);
            if (k==innerClass.length-1 && fields.length==0)
                jsonBuilder.append("]");
            else
                jsonBuilder.append("]").append(",");
        }

        for (int i=0; i < fields.length; i++) {
            String fieldName = fields[i].getName();
            Class fieldType = fields[i].getType();
            Type fieldGenericType = fields[i].getGenericType();

            if (Collection.class.isAssignableFrom(fieldType)) {
                if (fieldGenericType instanceof ParameterizedType) {
                    ParameterizedType parameterizedType =
                            (ParameterizedType) fieldGenericType;
                    Type[] typeArguments = parameterizedType.getActualTypeArguments();
                    if (typeArguments.length == 1) {
                        Type collectionElementType = typeArguments[0];
                        jsonBuilder.append("\"");
                        jsonBuilder.append(fieldName).append("\": [");
                        simpleFields(collectionElementType, jsonBuilder);
                        jsonBuilder.append(", \"...\"]");
                        if (i != fields.length - 1)
                            jsonBuilder.append(",");
                        continue;
                    }
                }
            } else if (Map.class.isAssignableFrom(fieldType)) {
                if (fieldGenericType instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) fieldGenericType;
                    Type[] typeArguments =
                            parameterizedType.getActualTypeArguments();
                    if (typeArguments.length == 2) {
                        Type mapKeyType = typeArguments[0];
                        Type mapValueType = typeArguments[1];
                        jsonBuilder.append("\"");
                        jsonBuilder.append(fieldName).append("\": [{");
                        if ((Class)mapKeyType instanceof Class && !isJDKOrPrimitive((Class) mapKeyType)){
                            Class mapKeyClass = (Class)mapKeyType;
                            jsonBuilder.append("\"");
                            jsonBuilder.append(mapKeyClass.getSimpleName())
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
                        continue;
                    }
                }
            } else if (fieldType.isArray()) {
                String elementType = fieldType.getSimpleName();
                int arrayDimension = getArrayDimension(fieldType);
                String elementSimpleType;
                elementSimpleType = elementType.substring(0,elementType.length() -arrayDimension*2);
                switch (elementSimpleType){
                    case "Integer":
                        elementSimpleType = "int";
                        break;
                    case "Character":
                        elementSimpleType = "char";
                        break;
                    case "Boolean":
                        elementSimpleType = "bool";
                        break;
                }

                jsonBuilder.append("\"").append(fieldName).append("\" : ");
                for (int j = 0; j < arrayDimension; j++) {
                    jsonBuilder.append("[");
                }
                jsonBuilder.append("\"");
                jsonBuilder.append(elementSimpleType).append("\",");
                for (int j = 0; j < arrayDimension; j++) {
                    jsonBuilder.append("\"...\"]");
                    if (j!= arrayDimension - 1)
                        jsonBuilder.append(",");
                }
                if (i!=fields.length-1)
                    jsonBuilder.append(",");

                continue;

            } else if (isJDKOrPrimitive(fieldType)) {

                jsonBuilder.append("\"").append(fieldName)
                        .append("\": \"").append(printJDK(fieldType))
                        .append("\"");
                if (i != fields.length - 1)
                    jsonBuilder.append(",");

                continue;

            } else if (fieldType instanceof Class) {
                jsonBuilder.append("\"").append(fieldName).append("\": [");
                simpleFields(fieldType, jsonBuilder);
                jsonBuilder.append("]");
                if (i != fields.length - 1)
                    jsonBuilder.append(",");

                continue;
            }

            jsonBuilder.append("\"").append(fieldName)
                    .append("\": \"").append(fieldType.getSimpleName())
                    .append("\"");
            if (i != fields.length - 1)
                jsonBuilder.append(",");
        }
        jsonBuilder.append("}");
    }

    private static void simpleFields(Type type, StringBuilder jsonBuilder) {
        if (type instanceof Class) {
            if(isJDKOrPrimitive((Class) type)){
                jsonBuilder.append("\"")
                        .append(printJDK((Class) type)).append("\"");
            } else {
                parameterizedFields((Class) type, jsonBuilder);
            }
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] typeArguments;
            typeArguments = parameterizedType.getActualTypeArguments();
            jsonBuilder.append("[");
            for (int i = 0; i < typeArguments.length; i++) {
                simpleFields(typeArguments[i], jsonBuilder);
            }
            jsonBuilder.append(", \"...\"]");
        } else {
            jsonBuilder.append("\"").append(type.toString()).append("\"");
        }
    }

    private static boolean isJDKOrPrimitive(Class clazz) {
        return clazz.isPrimitive() || clazz.getName().startsWith("java.lang");
    }

    private static String printJDK(Class clazz){

        if(clazz.getTypeName().equals("java.lang.Boolean"))
            return "bool";
        if(clazz.getTypeName().equals("java.lang.Character"))
            return "char";
        if(clazz.getTypeName().equals("java.lang.Integer"))
            return "int";

        return clazz.getSimpleName();
    }

    private static int getArrayDimension(Class clazz){
        int dimension = 0;
        while (clazz.isArray()) {
            dimension++;
            clazz = clazz.getComponentType();
        }
        return dimension;
    }
}
package org.example;

import static org.example.reflection.Reflection.classReflection;

public class Main {
    public static void main(String[] args) {
        for (String arg : args) {
            Class<?> classToConvert;
            try {
                classToConvert = Class.forName(arg);
                String result = classReflection(classToConvert);
                System.out.println(result);
            }
            catch (ClassNotFoundException e) {
                System.out.println("Can't find class" + e.getMessage());
            }
        }
    }
}

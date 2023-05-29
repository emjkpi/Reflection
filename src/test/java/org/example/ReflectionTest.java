package org.example;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ReflectionTest
{
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    public void simpleInt()
    {
        String[] test = {"org.example.TestSimpleInt"};
        Reflection.main(test);
        String expected ="{\"age\": \"int\"}";
        Assert.assertEquals(expected,outContent.toString().trim());
    }

    @Test
    public void arrayLast()
    {
        String[] test = {"org.example.ArrayLast"};
        Reflection.main(test);
        String expected ="{\"age\": \"int\",\"money\": \"int\",\"children\" : " +
                "[[[\"String\",\"...\"],\"...\"],\"...\"]}";
        Assert.assertEquals(expected,outContent.toString().trim());
    }

    @Test
    public void baseTest()
    {
        String[] test = {"org.example.BaseTest"};
        Reflection.main(test);
        String expected ="{\"result\": \"int\",\"error\": \"String\",\"keywords\": [{\"String\": [{\"isDirectory\": " +
                "\"boolean\",\"name\": \"String\",\"filePath\": \"String\",\"size\": \"long\"}, \"...\"]}, \"...\"]}";
        Assert.assertEquals(expected,outContent.toString().trim());
    }

    @Test
    public void collectionLast()
    {
        String[] test = {"org.example.CollectionLast"};
        Reflection.main(test);
        String expected ="{\"age\": \"int\",\"money\": \"int\",\"grades\":" +
                " [\"int\", \"...\"],\"temp\": [\"bool\", \"...\"]}";
        Assert.assertEquals(expected,outContent.toString().trim());
    }

    @Test
    public void wrappedAndSimple()
    {
        String[] test = {"org.example.WrappedAndSimple"};
        Reflection.main(test);
        String expected ="{\"age\": \"int\",\"money\": \"int\",\"isTrue\": \"bool\",\"zero\": \"Byte\"," +
                "\"sale\": \"Short\",\"price\": \"Long\",\"temp\": \"Float\",\"discount\": \"Double\"," +
                "\"word\": \"char\",\"file\": [{\"isDirectory\": \"boolean\",\"name\": \"String\"," +
                "\"filePath\": \"String\",\"size\": \"long\"}]}";
        Assert.assertEquals(expected,outContent.toString().trim());
    }

    @Test
    public void innerClass()
    {
        String[] test = {"org.example.InnerClass"};
        Reflection.main(test);
        String expected ="{\"Six\" : [{\"Five\" : [{\"Four\" : [{\"id\": \"int\",\"name\": \"String\"}]," +
                "\"isTrue\": \"bool\"}],\"grades\": [\"int\", \"...\"]}]}";
        Assert.assertEquals(expected,outContent.toString().trim());
    }

    @Test
    public void keyIsClass()
    {
        String[] test = {"org.example.KeyIsClass"};
        Reflection.main(test);
        String expected ="{\"age\" : [[[\"int\",\"...\"],\"...\"],\"...\"],\"grades\": [\"int\", \"...\"]," +
                "\"keywords\": [{\"RemouteFile\": [\"int\", \"...\"]}, \"...\"],\"discount\": \"bool\"}";
        Assert.assertEquals(expected,outContent.toString().trim());
    }
}

class BaseTest {
    private int result;
    private String error;
    private Map<String, Set<RemouteFile>> keywords;
}

class RemouteFile {

    private boolean isDirectory;
    private String name;
    private String filePath;
    private long size = -1;
}

class TestSimpleInt {
    private int age;
}

class ArrayLast{
    private int age;
    private Integer money;
    String[][][] children;
}

class CollectionLast{
    private int age;
    private Integer money;
    Set<Integer> grades;
    List<Boolean> temp;
}

class WrappedAndSimple{
    private int age;
    private Integer money;
    private Boolean isTrue;
    private Byte zero;
    private Short sale;
    private Long price;
    private Float temp;
    private Double discount;
    private Character word;
    private RemouteFile file;
}

class InnerClass{
    private class Six {
        Set<Integer> grades;
        private class Five{
            private Boolean isTrue;
            private class Four {
                int id;
                String name;
            }
        }
    }
}

class KeyIsClass{
    private Integer[][][] age;
    Set<Integer> grades;
    Map<RemouteFile,Set<Integer>> keywords;
    Boolean discount;
}
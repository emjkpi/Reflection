package org.example.testclasses;

import java.util.Set;

public class InnerClass{
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

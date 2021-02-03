/*
 * Copyright (c) 2004, 2020, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * @test
 * @bug 5040740
 * @summary annotations cause memory leak
 * @author gafter
 * @library /test/lib /test/jdk/java/lang/annotation/repository
 * @build jdk.test.lib.process.* A B C
 * @run testng/othervm LoaderLeakTest
*/

import jdk.test.lib.Utils;
import jdk.test.lib.process.ProcessTools;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.nio.file.*;
import java.util.*;

public class LoaderLeakTest {
/*
    @BeforeClass
    public void initialize() throws Exception {
        final Path TEST_CLASSES_PATH = Paths.get(Utils.TEST_CLASSES).toAbsolutePath();
        final Path REPOSITORY_PATH = TEST_CLASSES_PATH.resolve("classes").toAbsolutePath();
        Files.createDirectories(REPOSITORY_PATH);
        List<String> classes = List.of("A.class", "B.class", "C.class");
        for (String fileName : classes) {
            Files.move(
                TEST_CLASSES_PATH.resolve(fileName),
                REPOSITORY_PATH.resolve(fileName),
                StandardCopyOption.REPLACE_EXISTING
            );
        }
    }
*/
    @Test
    public void testWithoutReadingAnnotations() throws Throwable {
        System.out.println("classpath from inside test 1 is " + System.getProperty("java.class.path"));
        runJavaProcessExpectSuccessExitCode("Main");
    }

    @Test
    public void testWithReadingAnnotations() throws Throwable {
        System.out.println("classpath from inside test 2 is " + System.getProperty("java.class.path"));
        runJavaProcessExpectSuccessExitCode("Main",  "foo");
    }

    private void runJavaProcessExpectSuccessExitCode(String ... command) throws Throwable {
        ProcessTools
                .executeCommand(
                        ProcessTools
                                .createJavaProcessBuilder(command)
                                .directory(Paths.get(Utils.TEST_CLASSES).toFile()
                        )
                ).shouldHaveExitValue(0);
    }

}

class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("classpath from inside main is " + System.getProperty("java.class.path"));
        for (int i=0; i<100; i++)
            doTest(args.length != 0);
    }

    static void doTest(boolean readAnn) throws Exception {
        // URL classes = new URL("file://" + System.getProperty("user.dir") + "/classes");
        // URL[] path = { classes };
        // URLClassLoader loader = new URLClassLoader(path);
        ClassLoader loader = new SimpleClassLoader();
        //SimpleClassLoader loader = new SimpleClassLoader();
        WeakReference<Class<?>> c = new WeakReference<Class<?>>(loader.loadClass("C"));
        if (c.get() == null) throw new AssertionError();
        if (c.get().getClassLoader() != loader) throw new AssertionError();
        if (readAnn) System.out.println(c.get().getAnnotations()[0]);
        if (c.get() == null) throw new AssertionError();
        System.gc();
        System.gc();
        if (c.get() == null) throw new AssertionError();
        System.gc();
        System.gc();
        Reference.reachabilityFence(loader);
        loader = null;

        // Might require multiple calls to System.gc() for weak-references
        // processing to be complete. If the weak-reference is not cleared as
        // expected we will hang here until timed out by the test harness.
        while (true) {
            System.gc();
            Thread.sleep(20);
            if (c.get() == null) {
                break;
            }
        }
    }
}

class SimpleClassLoader extends ClassLoader {

    private Map<String, Class<?>> classes = new HashMap<>();

    public SimpleClassLoader() {
    }

    private byte getClassImplFromDataBase(String className)[] {
        byte result[];
        try {
            FileInputStream fi = new FileInputStream("repository/" +className+".class");
            result = new byte[fi.available()];
            fi.read(result);
            return result;
        } catch (Exception e) {
            System.err.printf("attempt to load class %s from test custom database failed%n", className);
            /*
             * If we caught an exception, either the class wasnt found or it
             * was unreadable by our process.
             */
            return null;
        }
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        System.out.println("1 should go here, loading"+className);
        return (loadClass(className, true));
    }

    @Override
    public synchronized Class<?> loadClass(String className, boolean resolveIt)
            throws ClassNotFoundException {
        Class<?> result;
        byte  classData[];

        System.out.println("2 checking cache");
        /* Check our local cache of classes */
        result = classes.get(className);
        if (result != null) {
            return result;
        }


        System.out.println("3 not found in cache");
        /* Check with the primordial class loader */
        try {
            result = super.findSystemClass(className);

            System.out.println("4 parent has it");
            return result;
        } catch (ClassNotFoundException e) {
        }


        System.out.println("5 parent does not have it");
        /* Try to load it from our repository */
        classData = getClassImplFromDataBase(className);
        if (classData == null) {
            throw new ClassNotFoundException();
        }

        /* Define it (parse the class file) */
        result = defineClass(className, classData, 0, classData.length);
        if (result == null) {
            throw new ClassFormatError();
        }

        if (resolveIt) {
            resolveClass(result);
        }

        classes.put(className, result);
        return result;
    }
}

/*
 * Copyright (c) 2002, 2018, Oracle and/or its affiliates. All rights reserved.
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

package nsk.jdb.klass.class001;

import nsk.share.Log;
import nsk.share.jdb.JdbArgumentHandler;

import java.io.PrintStream;

/* This is debuggee application */
public class class001a {

    public static void main(String[] args) {
        class001a _class001a = new class001a();
        System.exit(class001.JCK_STATUS_BASE + _class001a.runIt(args, System.out));
    }


    static void lastBreak() {
    }

    public int runIt(String[] args, PrintStream out) {
        JdbArgumentHandler argumentHandler = new JdbArgumentHandler(args);
        Log log = new Log(out, argumentHandler);

        init();

        log.display("Debuggee PASSED");
        return class001.PASSED;
    }

    interface InnerInt1 {
    }

    public class Inner2 implements InnerInt1 {
    }

    private class Inner3 {
    }

    protected class Inner4 {
    }

    abstract class Inner5 {
    }

    final class Inner6 extends Inner5 {
    }

    private void init() {
        Outer1 o1 = new Outer1();
        Inner2 i2 = new Inner2();
        Inner3 i3 = new Inner3();
        Inner4 i4 = new Inner4();
        Inner6 i6 = new Inner6();

        lastBreak();
    }
}

class Outer1 {
}

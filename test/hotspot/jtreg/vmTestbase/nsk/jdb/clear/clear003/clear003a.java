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

package nsk.jdb.clear.clear003;

import nsk.share.Log;
import nsk.share.jdb.JdbArgumentHandler;

import java.io.PrintStream;

/* This is debuggee application */
public class clear003a {
    public static void main(String[] args) {
        clear003a _clear003a = new clear003a();
        lastBreak();
        System.exit(clear003.JCK_STATUS_BASE + _clear003a.runIt(args, System.out));
    }

    static void lastBreak() {
    }

    public int runIt(String[] args, PrintStream out) {
        JdbArgumentHandler argumentHandler = new JdbArgumentHandler(args);
        Log log = new Log(out, argumentHandler);

        int i = func1(0);

        log.display("Debuggee PASSED");
        return clear003.PASSED;
    }

    public int func1(int i) {
        return func2(i) + 1;
    }

    public int func2(int i) {
        return func3(i) + 1;
    }

    public int func3(int i) {
        int value;
        value = func4(i) + 1;
        return value;
    }

    public int func4(int i) {
        int value;
        value = func5(i) + 1;
        return value;
    }

    public int func5(int i) {
        int value;
        value = func6(i) + 1;
        return value;
    }

    public int func6(int i) {
        return i - 5;
    }
}

/*
 * Copyright (c) 2013, 2016, Oracle and/or its affiliates. All rights reserved.
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
 * @bug 4786406 4781221 4780341 6214324
 * @summary Validates rewritten javah handling of class defined constants and
 * ensures that the appropriate macro definitions are placed in the generated
 * header file.
 * @library /tools/lib
 * @modules jdk.compiler/com.sun.tools.javac.api
 *          jdk.compiler/com.sun.tools.javac.main
 *          jdk.jdeps/com.sun.tools.javap
 * @build toolbox.ToolBox toolbox.JavahTask
 * @run main ConstMacroTest
 */

import java.io.*;
import java.util.List;

import toolbox.JavahTask;
import toolbox.ToolBox;

// Original test: test/tools/javah/ConstMacroTest.sh
public class ConstMacroTest {

    private static final String subClassConstsGoldenFileTemplate =
        "/* DO NOT EDIT THIS FILE - it is machine generated */\n" +
        "#include <jni.h>\n" +
        "/* Header for class SubClassConsts */\n" +
        "\n" +
        "#ifndef _Included_SubClassConsts\n" +
        "#define _Included_SubClassConsts\n" +
        "#ifdef __cplusplus\n" +
        "extern \"C\" {\n" +
        "#endif\n" +
        "#undef SubClassConsts_serialVersionUID\n" +
        "#define SubClassConsts_serialVersionUID 6733861379283244755%s\n" +
        "#undef SubClassConsts_SUPER_INT_CONSTANT\n" +
        "#define SubClassConsts_SUPER_INT_CONSTANT 3L\n" +
        "#undef SubClassConsts_SUPER_FLOAT_CONSTANT\n" +
        "#define SubClassConsts_SUPER_FLOAT_CONSTANT 99.3f\n" +
        "#undef SubClassConsts_SUPER_DOUBLE_CONSTANT\n" +
        "#define SubClassConsts_SUPER_DOUBLE_CONSTANT 33.2\n" +
        "#undef SubClassConsts_SUPER_BOOLEAN_CONSTANT\n" +
        "#define SubClassConsts_SUPER_BOOLEAN_CONSTANT 0L\n" +
        "#undef SubClassConsts_SUB_INT_CONSTANT\n" +
        "#define SubClassConsts_SUB_INT_CONSTANT 2L\n" +
        "#undef SubClassConsts_SUB_DOUBLE_CONSTANT\n" +
        "#define SubClassConsts_SUB_DOUBLE_CONSTANT 2.25\n" +
        "#undef SubClassConsts_SUB_FLOAT_CONSTANT\n" +
        "#define SubClassConsts_SUB_FLOAT_CONSTANT 7.9f\n" +
        "#undef SubClassConsts_SUB_BOOLEAN_CONSTANT\n" +
        "#define SubClassConsts_SUB_BOOLEAN_CONSTANT 1L\n" +
        "#ifdef __cplusplus\n" +
        "}\n" +
        "#endif\n" +
        "#endif";

    public static void main(String[] args) throws Exception {
        ToolBox tb = new ToolBox();

        new JavahTask(tb)
                .classpath(ToolBox.testClasses)
                .classes("SubClassConsts")
                .run();

        String longSuffix = tb.isWindows() ? "i64" : "LL";
        List<String> subClassConstsGoldenFile = tb.split(
                String.format(subClassConstsGoldenFileTemplate, longSuffix), "\n");

        List<String> subClassConstsFile = tb.readAllLines("SubClassConsts.h");

        tb.checkEqual(subClassConstsFile, subClassConstsGoldenFile);
    }

}

class SuperClassConsts implements Serializable {
    // Define class constant values, base class is serializable
    private static final long serialVersionUID = 6733861379283244755L;
    public static final int SUPER_INT_CONSTANT = 3;
    public final static float SUPER_FLOAT_CONSTANT = 99.3f;
    public final static double SUPER_DOUBLE_CONSTANT  = 33.2;
    public final static boolean SUPER_BOOLEAN_CONSTANT  = false;
    // A token instance field
    int instanceField;

    public native int numValues();
}

class SubClassConsts extends SuperClassConsts {
    private final static int SUB_INT_CONSTANT = 2;
    private final static double SUB_DOUBLE_CONSTANT = 2.25;
    private final static float SUB_FLOAT_CONSTANT = 7.90f;
    private final static boolean SUB_BOOLEAN_CONSTANT = true;
}

/**
 * <copyright>
 *  Copyright 1997-2002 InfoEther, LLC
 *  under sponsorship of the Defense Advanced Research Projects Agency
(DARPA).
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the Cougaar Open Source License as published
by
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).
 *
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 *  PROVIDED 'AS IS' WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.
 * </copyright>
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.rules.AtLeastOneConstructorRule;

public class AtLeastOneConstructorRuleTest extends SimpleAggregatorTst {

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "ok", 0, new AtLeastOneConstructorRule()),
           new TestDescriptor(TEST2, "simple failure case", 1, new AtLeastOneConstructorRule()),
           new TestDescriptor(TEST3, "inner bad, outer ok", 1, new AtLeastOneConstructorRule()),
           new TestDescriptor(TEST4, "inner ok, outer bad", 1, new AtLeastOneConstructorRule()),
           new TestDescriptor(TEST5, "inner and outer both bad", 2, new AtLeastOneConstructorRule()),
           new TestDescriptor(TEST6, "inner and outer both ok", 0, new AtLeastOneConstructorRule()),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " public Foo() {}" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " public class Bar {}" + PMD.EOL +
    " public Foo() {}" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class Foo {" + PMD.EOL +
    " public class Bar { " + PMD.EOL +
    "  public Bar() {}" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST5 =
    "public class Foo {" + PMD.EOL +
    " public class Bar { " + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST6 =
    "public class Foo {" + PMD.EOL +
    " public class Bar { " + PMD.EOL +
    "  public Bar() {}" + PMD.EOL +
    " }" + PMD.EOL +
    " public Foo() {}" + PMD.EOL +
    "}";


}

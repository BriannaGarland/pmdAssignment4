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
package test.net.sourceforge.pmd.symboltable;

import junit.framework.TestCase;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.symboltable.AbstractScope;
import net.sourceforge.pmd.symboltable.ClassScope;
import net.sourceforge.pmd.symboltable.NameDeclaration;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.symboltable.Scope;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;

import java.util.Iterator;

public class AbstractScopeTest extends TestCase {

    // A helper class to stub out AbstractScope's abstract stuff
    private class MyScope extends AbstractScope {
        protected NameDeclaration findVariableHere(NameOccurrence occ) {
            for (Iterator i = variableNames.keySet().iterator(); i.hasNext();) {
                NameDeclaration decl = (NameDeclaration) i.next();
                if (decl.getImage().equals(occ.getImage())) {
                    return decl;
                }
            }
            return null;
        }
    }

    // Another helper class to test the search for a class scope behavior
    private class IsEnclosingClassScope extends ClassScope {

        public IsEnclosingClassScope(String name) {
            super(name);
        }

        protected NameDeclaration findVariableHere(NameOccurrence occ) {
            return null;
        }

        public ClassScope getEnclosingClassScope() {
            return this;
        }
    }

    public void testAccessors() {
        Scope scope = new MyScope();
        MyScope parent = new MyScope();
        scope.setParent(parent);
        assertEquals(parent, scope.getParent());

        assertTrue(!scope.getVariableDeclarations(false).keySet().iterator().hasNext());
        assertTrue(scope.getVariableDeclarations(true).isEmpty());
    }

    public void testEnclClassScopeGetsDelegatedRight() {
        Scope scope = new MyScope();
        Scope isEncl = new IsEnclosingClassScope("Foo");
        scope.setParent(isEncl);
        assertEquals(isEncl, scope.getEnclosingClassScope());
    }

    public void testAdd() {
        Scope scope = new MyScope();
        ASTVariableDeclaratorId node = new ASTVariableDeclaratorId(1);
        node.setImage("foo");
        VariableNameDeclaration decl = new VariableNameDeclaration(node);
        scope.addDeclaration(decl);
        assertTrue(scope.contains(new NameOccurrence(new SimpleNode(1), "foo")));
    }
}

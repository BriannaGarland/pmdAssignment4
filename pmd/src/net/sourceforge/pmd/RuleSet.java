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
package net.sourceforge.pmd;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class RuleSet {
    private Set rules = new HashSet();
    private String name;
    private String description;

    /**
     * Indicates whether or not the rule set should be included in PMD's analysis.
     * True to include the rule set; otherwise, false to exclude the rule set.
     */
    private boolean m_include;

    /**
     * The name of the file the rule set is stored in, e.g., "basic_rules.xml".  The user may
     * change the rule set name; therefore, the rule set name cannot be used for a file name.
     * This variable is set when the rule set is read.
     */
    private String m_fileName;

    public int size() {
        return rules.size();
    }

    public void addRule(Rule rule) {
        rules.add(rule);
    }

    public Set getRules() {
        return rules;
    }

    public Rule getRuleByName(String ruleName) {
        for (Iterator i = rules.iterator(); i.hasNext();) {
            Rule r = (Rule) i.next();
            if (r.getName().equals(ruleName)) {
                return r;
            }
        }
        throw new RuntimeException("Couldn't find rule named " + ruleName + " in the ruleset " + name);
    }

    public void addRuleSet(RuleSet ruleSet) {
        rules.addAll(ruleSet.getRules());
    }

    public void apply(List acuList, RuleContext ctx) {
        Iterator rs = rules.iterator();
        while (rs.hasNext()) {
            Rule rule = (Rule) rs.next();

            rule.apply(acuList, ctx);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns true when the rule set is included in PMD's analysis; otherwise, false when
     * it is excluded.
     *
     * @return True to include during analysis.
     */
    public boolean include() {
        return m_include;
    }

    /**
     * Set to true when the rule set is included in PMD's analysis; otherwise, set to false
     * when it is excluded.
     *
     * @param include True to include during analysis.
     */
    public void setInclude(boolean include) {
        m_include = include;
    }

    /**
     * Get the name of the file the rule set is to be stored in, e.g., "basic_rules.xml".
     *
     * @return The name of the rule set file.
     */
    public String getFileName() {
        if (m_fileName == null) {
            m_fileName = name.toLowerCase().replace(' ', '_') + ".xml";
        }

        return m_fileName;
    }

    /**
     * Set the name of the file the rule set is to be stored in, e.g., "basic_rules.xml".
     *
     * @param fileName The name of the rule set file.
     */
    public void setFileName(String fileName) {
        if (fileName != null) {
            fileName = fileName.trim();

            if (fileName.length() == 0) {
                fileName = null;
            }
        }

        m_fileName = fileName;
    }
}

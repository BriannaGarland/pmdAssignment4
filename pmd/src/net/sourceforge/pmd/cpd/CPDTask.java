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
package net.sourceforge.pmd.cpd;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.FileSet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * CPDTask
 * 
 * Runs the CPD utility via ant. The ant task looks like this:
 *
 * <project name="CPDProj" default="main" basedir=".">
 *  <taskdef name="cpd" classname="net.sourceforge.pmd.cpd.CPDTask" />
 *	 <target name="main">
 * 		<cpd minimumTokenCount="100" outputFile="c:\cpdrun.txt" verbose=true>
 *          <fileset dir="/path/to/my/src">
 *              <include name="*.java"/>
 *          </fileset>
 *      </cpd>
 *	</target>
 *</project>
 *
 * Required: minimumTokenCount, outputFile, and at least one file
 * Optional: verbose
 */
public class CPDTask extends Task {

    private static final String TEXT_FORMAT = "text";
    private static final String XML_FORMAT = "xml";

    private String format = TEXT_FORMAT;
	private int minimumTokenCount;
	private File outputFile;
    private List filesets = new ArrayList();

    public void execute() throws BuildException {
      try {
        validateFields();

        log("Tokenizing files", Project.MSG_INFO);
        CPD cpd = new CPD(minimumTokenCount, new JavaLanguage());
        tokenizeFiles(cpd);

        log("Starting to analyze code", Project.MSG_INFO);
        long timeTaken = analyzeCode(cpd);
        log("Done analyzing code; that took " + timeTaken + " milliseconds");

        log("Generating report", Project.MSG_INFO);
        report(cpd);
      } catch (IOException ioe) {
        log(ioe.toString(), Project.MSG_ERR);
        throw new BuildException("IOException during task execution", ioe);
      } catch (ReportException re) {
        log(re.toString(), Project.MSG_ERR);
        throw new BuildException("ReportException during task execution", re);
      }
    }

    private void report(CPD cpd) throws ReportException {
      if (!cpd.getMatches().hasNext()) {
        log("No duplicates over " + minimumTokenCount + " tokens found", Project.MSG_INFO);
      }
      Renderer renderer = createRenderer();
      if (outputFile.isAbsolute()) {
        new FileReporter(outputFile).report(renderer.render(cpd.getMatches()));
      } else {
        new FileReporter(new File(project.getBaseDir(), outputFile.toString()));
      }
    }


    private void tokenizeFiles(CPD cpd) throws IOException {
      for (Iterator iterator = filesets.iterator(); iterator.hasNext();) {
        FileSet fileSet = (FileSet) iterator.next();
        DirectoryScanner directoryScanner = fileSet.getDirectoryScanner(project);
        String[] includedFiles = directoryScanner.getIncludedFiles();
        for (int i = 0; i < includedFiles.length; i++) {
          File file = new File(directoryScanner.getBasedir() + System.getProperty("file.separator") + includedFiles[i]);
          log("Tokenizing " + file.getAbsolutePath(), Project.MSG_VERBOSE);
          cpd.add(file);
        }
      }
    }

    private long analyzeCode(CPD cpd) {
      long start = System.currentTimeMillis();
      cpd.go();
      long stop = System.currentTimeMillis();
      return stop - start;
    }

    private Renderer createRenderer() {
      if (format.equals(TEXT_FORMAT)) {
        return new SimpleRenderer();
      } else
        return new XMLRenderer();
    }

	private void validateFields() throws BuildException{
		if(minimumTokenCount == 0){
			throw new BuildException("minimumTokenCount is required and must be greater than zero");
		} else if(outputFile == null) {
            throw new BuildException("outputFile is a required attribute");
        } else if (filesets.isEmpty()) {
            throw new BuildException("Must include at least one FileSet");
        }

	}

    public void addFileset(FileSet set) {
        filesets.add(set);
    }

	public void setMinimumTokenCount(int minimumTokenCount) {
		this.minimumTokenCount = minimumTokenCount;
	}

	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}

    public void setFormat(FormatAttribute formatAttribute) {
      format = formatAttribute.getValue();
    }

    public static class FormatAttribute extends EnumeratedAttribute {
      private String[] formats = new String[] {XML_FORMAT, TEXT_FORMAT};

      public String[] getValues() {
        return formats;
      }
    }
}

/*START HEADER*/
/* Zofar Survey System
* Copyright (C) 2014 Deutsches Zentrum für Hochschul- und Wissenschaftsforschung
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
/*STOP HEADER*/
package tests.common;
import java.util.Enumeration;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestFailure;
import junit.framework.TestResult;
import junit.textui.ResultPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class ZofarResultPrinter extends ResultPrinter {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ZofarResultPrinter.class);
	public ZofarResultPrinter() {
		super(System.out);
	}
    protected void printHeader(long runTime) {
        getWriter().println();
    }
	@Override
    public void addError(Test test, Throwable t) {
    }
    @Override
    public void addFailure(Test test, AssertionFailedError t) {
    }
	@Override
    protected void printDefects(Enumeration<TestFailure> booBoos, int count, String type) {
        if (count == 0) return;
        getWriter().println("Errors found: " );
        for (int i = 1; booBoos.hasMoreElements(); i++) {
            printDefect(booBoos.nextElement(), i);
        }
    }
    @Override
    public void printDefect(TestFailure booBoo, int count) { 
        printDefectHeader(booBoo, count);
        getWriter().print(" ==> " );
        printDefectTrace(booBoo);
    }
	@Override
    protected void printDefectHeader(TestFailure booBoo, int count) {
        getWriter().print(count + ") " + booBoo.failedTest().getClass().getSimpleName());
    }
	@Override
    protected void printDefectTrace(TestFailure booBoo) {
        getWriter().print(booBoo.exceptionMessage());
    }
	@Override
    protected void printFooter(TestResult result) {
        if (result.wasSuccessful()) {
            getWriter().println();
            getWriter().print("OK");
            getWriter().println(" (" + result.runCount() + " test" + (result.runCount() == 1 ? "" : "s") + ")");
        }
        getWriter().println();
    }
	@Override
    public void startTest(Test test) {
    }
}

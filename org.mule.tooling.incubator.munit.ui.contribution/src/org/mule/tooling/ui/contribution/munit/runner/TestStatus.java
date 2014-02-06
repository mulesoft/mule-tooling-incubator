/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.tooling.ui.contribution.munit.runner;

public class TestStatus
{

    private String testName;
    private boolean failed;
    private boolean error;
    private boolean finished;
    private String cause;


    public TestStatus(String testName)
    {
        this.testName = testName;
    }

    public String getTestName()
    {
        return testName;
    }

    public void setFailed(boolean failed)
    {
        this.failed = failed;
    }

    public void setError(boolean error)
    {
        this.error = error;
    }

    public void setFinished(boolean finished)
    {
        this.finished = finished;
    }

    public boolean hasError()
    {
        return error;
    }

    public boolean hasFailed()
    {
        return failed;
    }

    public boolean isFinished()
    {
        return finished || failed || error;
    }

    public void setCause(String cause)
    {
        this.cause = cause;
    }

    public String getCause()
    {
        return cause;
    }

}

package org.mule.tooling.ui.contribution.munit.runner;

public class TestStatus {

    private String testName;
    private boolean failed;
    private boolean error;
    private boolean finished;
    private String cause;

    public TestStatus(String testName) {
        this.testName = testName;
    }

    public String getTestName() {
        return testName;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public boolean hasError() {
        return error;
    }

    public boolean hasFailed() {
        return failed;
    }

    public boolean isFinished() {
        return finished || failed || error;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getCause() {
        return cause;
    }

}

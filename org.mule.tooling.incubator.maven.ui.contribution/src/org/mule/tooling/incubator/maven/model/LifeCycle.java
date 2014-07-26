package org.mule.tooling.incubator.maven.model;


public enum LifeCycle implements ILifeCycle{
    PRE_CLEAN("pre-clean"),
    CLEAN("clean"),
    POST_CLEAN("post-clean"),
    VALIDATE("validate"),
    INITIALIZE("initialize"),
    GENERATE_SOURCES("generate-sources"),
    PROCESS_SOURCES("process-sources"),
    GENERATE_RESOURCES("generate-resources"),
    PROCESS_RESOURCES("process-resources"),
    COMPILE("compile"),
    PROCESS_CLASSES("process-classes"),
    GENERATE_TEST_SOURCES("generate-test-sources"),
    PROCESS_TEST_SOURCES("process-test-sources"),
    GENERATE_TEST_RESOURCES("generate-test-resources"),
    PROCESS_TEST_RESOURCES("process-test-resources"),
    TEST_COMPILE("test-compile"),
    PROCESS_TEST_CLASSES("process-test-classes"),
    TEST("test"),
    PREPARE_PACKAGE("prepare-package"),
    PACKAGE("package"),
    PRE_INTEGRATION_TEST("pre-integration-test"),
    INTEGRATION_TEST("integration-test"),
    POST_INTEGRATION_TEST("post-integration-test"),
    verify("verify"),
    INSTALL("install"),
    PRE_SITE("pre-site"),
    SITE("site"),
    POST_SITE("post-site"),
    SITE_DEPLOY("site-deploy"),
    DEPLOY("deploy");

    String phase;

    LifeCycle(String phase) {
        this.phase = phase;
    }

    public String getPhase() {
        return phase;
    }
    
    public String toString(){
        return this.getPhase();
    }
    
    public static LifeCycle[] getReducedLifeCycle(){
        return new LifeCycle[]{CLEAN,VALIDATE,COMPILE,TEST,PACKAGE,INSTALL,SITE,DEPLOY};
    }
}

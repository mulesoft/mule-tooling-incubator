package org.mule.tooling.ui.contribution.munit.coverage;

import java.util.Map;
import java.util.Set;

/**
 * <p>
 * Representation of the coverage report of the last ran Munit suite
 * </p>
 */
public class CoverageReport {

    /**
     * The overall coverage
     */
    private double coverage;

    /**
     * The coverage per container
     */
    private Map<String, Double> containersCoverage;

    /**
     * All the Message flow node path that where covered
     */
    private Set<String> coveredPaths;

    /**
     * All the paths that should be tested
     */
    private Set<String> allPaths;

    public void setCoverage(double coverage) {
        this.coverage = coverage;
    }

    public void setContainersCoverage(Map<String, Double> containersCoverage) {
        this.containersCoverage = containersCoverage;
    }

    public void setCoveredPaths(Set<String> coveredPaths) {
        this.coveredPaths = coveredPaths;
    }

    public void setAllPaths(Set<String> allPaths) {
        this.allPaths = allPaths;
    }

    public double getCoverage() {
        return coverage;
    }

    public Map<String, Double> getContainersCoverage() {
        return containersCoverage;
    }

    public Set<String> getCoveredPaths() {
        return coveredPaths;
    }

    public Set<String> getAllPaths() {
        return allPaths;
    }
}

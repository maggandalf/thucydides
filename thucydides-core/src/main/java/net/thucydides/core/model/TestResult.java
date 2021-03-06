package net.thucydides.core.model;


/**
 * Acceptance test results. 
 * Records the possible outcomes of tests within an acceptance test case
 * and of the overall acceptance test case itself.
 * 
 * @author johnsmart
 *
 */
public enum TestResult {
    /** 
     * Test failure.
     * For a test case, this means one of the tests in the test case failed.
     */
    FAILURE,    
    
    /**
     * The test or test case ran as expected.
     */
    SUCCESS, 
    
    /**
     * The test or test case was deliberately ignored.
     * Tests can be ignored via the @Ignore annotation in JUnit, for example.
     * Ignored tests are not considered the same as pending tests: a pending test is one that
     * has been specified, but the corresponding code is yet to be implemented, whereas an
     * ignored test can be a temporarily-deactivated test (during refactoring, for example).
     */
    IGNORED, 
    
    /**
     * The test was not executed because a previous test in this test case failed.
     * Doesn't make sense for a test case.
     */
    SKIPPED,
    
    /**
     * A pending test is one that has been specified but not yet implemented.
     * In a JUnit test case, you can use the (Thucydides) @Pending annotation to mark this.
     * A pending test case is a test case that has at least one pending test.
     */
    PENDING
    
}

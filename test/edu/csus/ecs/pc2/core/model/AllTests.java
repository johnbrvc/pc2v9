package edu.csus.ecs.pc2.core.model;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * All JUnit tests for core.model package.
 * 
 * @author pc2@ecs.csus.edu
 * $version $Id$
 */

// $HeadURL$
public final class AllTests {

    private AllTests() {

    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for edu.csus.ecs.pc2.core.model");
        //$JUnit-BEGIN$
        suite.addTestSuite(AccountListTest.class);
        suite.addTestSuite(SiteTest.class);
        suite.addTestSuite(TimeFormatTest.class);
        suite.addTestSuite(LanguageAutoFillTest.class);
        suite.addTestSuite(JudgementNotificationTest.class);
        suite.addTestSuite(InternalContestTest.class);
        suite.addTestSuite(ContestTest.class);
        suite.addTestSuite(DisplayTeamNameTest.class);
        suite.addTestSuite(AccountTest.class);
        suite.addTestSuite(BalloonSettingsTest.class);
        suite.addTestSuite(ProfileTest.class);
        suite.addTestSuite(ContestInformationTest.class);
        suite.addTestSuite(FilterTest.class);
        suite.addTestSuite(PluralizeTest.class);
        suite.addTestSuite(FilterFormatterTest.class);
        suite.addTestSuite(NotificationSettingTest.class);
        suite.addTestSuite(ProblemTest.class);
        suite.addTestSuite(RunUtilitiesTest.class);
        suite.addTestSuite(ProblemDataFilesTest.class);
        //$JUnit-END$
        return suite;
    }

}

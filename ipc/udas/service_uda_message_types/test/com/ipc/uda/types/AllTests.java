package com.ipc.uda.types;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

    public static Test suite()
    {
        TestSuite suite = new TestSuite();

        suite.addTestSuite(AddPersonalContactCommandImplTest.class);
        suite.addTestSuite(UpdateContactCommandImplTest.class);
        suite.addTestSuite(DeleteContactCommandImplTest.class);
        //suite.addTestSuite(GetPersonalDirectoryQueryImplTest.class);
        //suite.addTestSuite(GetInstanceDirectoryQueryImplTest.class);
        suite.addTestSuite(GetInstanceContactDetailsQueryImplTest.class);
        suite.addTestSuite(GetPersonalContactDetailsQueryImplTest.class);
        suite.addTestSuite(AddPersonalPOCToFavoritesCommandImplTest.class);
        suite.addTestSuite(AddInstancePOCToFavoritesCommandImplTest.class);
        suite.addTestSuite(GetContactDetailsFromButtonPageQueryImplTest.class);
        suite.addTestSuite(GetContactHistoryFromButtonPageQueryImplTest.class);
        suite.addTestSuite(GetNextPageOnButtonSheetQueryImplTest.class);
        suite.addTestSuite(GetPrevPageOnButtonSheetQueryImplTest.class);
        suite.addTestSuite(GetButtonSheetForThePageQueryImplTest.class);
        suite.addTestSuite(GetButtonSheetQueryImplTest.class);
        suite.addTestSuite(GetDirectoryListQueryImplTest.class);
        suite.addTestSuite(GetDirectoryNameListQueryImplTest.class);
        suite.addTestSuite(GetDirectoryCategoryContentsQueryImplTest.class);
        return suite;
    }

    /**
     * Runs the test suite using the textual runner.
     */
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(suite());
    }
}

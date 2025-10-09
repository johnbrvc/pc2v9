// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.exports.ccs;

import java.io.File;

import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.scoring.StandingsRecord;
import edu.csus.ecs.pc2.core.model.FinalizeData;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;
import edu.csus.ecs.pc2.imports.ccs.ContestSnakeYAMLLoader;
import edu.csus.ecs.pc2.imports.ccs.IContestLoader;

/**
 * Test ResultsFile adjustMedals method.
 * 
 * @author John Buck
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9sandbox/trunk/test/edu/csus/ecs/pc2/exports/ccs/ResultsFileTest.java $
public class AdjustMedalTiesTest extends AbstractTestCase {

    private static final int MAX_AMT_TEAMS = 52;
    private final boolean debugMode = false;
    private ResultsFile results = new ResultsFile();
    private StandingsRecord [] recs = null;
    private int nRec = 0;
    
    /**
     * Sample Finalize Data.
     * 
     * @param rank
     * @return
     */
    private static FinalizeData createSampFinalData(int ng, int ns, int nb) {
        FinalizeData data = new FinalizeData();

        data.setGoldRank(ng);
        data.setSilverRank(ng+ns);
        data.setBronzeRank(ng+ns+nb);

        return data;
    }

    private void resetStandingsRecords(int nRecs) {
        recs = createStandingsRecordsWithRank(nRecs);
        nRec = nRecs;
    }
    
    private StandingsRecord [] createStandingsRecordsWithRank(int nRecs) {
        StandingsRecord [] standingsRecords = new StandingsRecord [nRecs];
        int idx;
        for(idx = 0; idx < nRecs; idx++) {
            standingsRecords[idx] = new StandingsRecord();
            standingsRecords[idx].setRankNumber(idx+1);
        }
        return standingsRecords;
    }
    
    private void checkFinalizeData(String testName, FinalizeData data, int gr, int sr, int br) {
        assertEquals(testName + " gold ", gr, data.getGoldRank());
        assertEquals(testName + " silver ", sr, data.getSilverRank());
        assertEquals(testName + " bronze ", br, data.getBronzeRank());
    }
    /**
     * Test for control cases (no ties).
     * 
     * @throws Exception
     */
    public void testNoTies() throws Exception {
        FinalizeData fData = createSampFinalData(1, 2, 3);
        resetStandingsRecords(MAX_AMT_TEAMS);
        results.adjustMedals(recs, fData);
        checkFinalizeData("No ties 1/2/3", fData, 1, 3, 6);
        
        fData = createSampFinalData(0, 5, 0);
        checkFinalizeData("No ties 0/5/0", fData, 0, 5, 5);
    }

    private void adjustRank(int nStart, int nCnt) {
        while(nCnt > 0 && nStart < nRec) {
            recs[nStart].setRankNumber(recs[nStart-1].getRankNumber());
            nStart++;
            nCnt--;
        }
    }
    
    /**
     * Test for ties at boundaries.
     * 
     * @throws Exception
     */
    public void testTies() throws Exception {
        FinalizeData fData = createSampFinalData(1, 2, 3);
        resetStandingsRecords(MAX_AMT_TEAMS);
        // Make an extra gold
        adjustRank(1, 1);
        results.adjustMedals(recs, fData);
        checkFinalizeData("Tie 2/1/3 extra g", fData, 2, 3, 6);
        // Now make an extra silver
        adjustRank(3, 1);
        fData = createSampFinalData(1, 2, 3);
        results.adjustMedals(recs,  fData);
        checkFinalizeData("Tie 2/2/2 extra g,s", fData, 2, 4, 6);
        // Now make all the rest be bronze
        adjustRank(6, MAX_AMT_TEAMS-6);
        fData = createSampFinalData(1, 2, 3);
        results.adjustMedals(recs, fData);
        checkFinalizeData("Tie 2/2/48 extra g,s", fData, 2, 4, 52);
        // No silvers, just g and b
        resetStandingsRecords(MAX_AMT_TEAMS);
        adjustRank(1, 2);
        fData = createSampFinalData(1, 2, 3);
        results.adjustMedals(recs,  fData);
        checkFinalizeData("Tie 3/0/3 extra no silvers", fData, 3, 3, 6);
    }

}

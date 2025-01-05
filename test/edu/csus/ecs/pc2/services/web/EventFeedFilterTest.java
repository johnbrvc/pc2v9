// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.services.web;

import edu.csus.ecs.pc2.clics.API202306.EventFeedFilter;
import edu.csus.ecs.pc2.clics.API202306.EventFeedJSON;
import edu.csus.ecs.pc2.clics.API202306.EventFeedType;
import edu.csus.ecs.pc2.clics.API202306.JSON202306Utilities;
import edu.csus.ecs.pc2.clics.API202306.JSONTool;
import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit Test.
 *
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class EventFeedFilterTest extends AbstractTestCase {

    public void testNoFilter() throws Exception {

        EventFeedFilter filter = new EventFeedFilter();

        assertEquals("startid = <none set>, event types = <none set>, groupids = <none set>", filter.toString());

        String[] lines = getStandardContestJSON();
        assertEquals("Expected line count ", 143, lines.length);

        assertNumberEvents(143, filter, lines);
    }

    private String[] getStandardContestJSON() throws IllegalContestState {

        SampleContest samp = new SampleContest();
        IInternalContest contest = samp.createStandardContest();

        EventFeedJSON efJson = new EventFeedJSON(new JSONTool(contest, null));
        String json = efJson.createJSON(contest, null, null);
        return json.split(JSON202306Utilities.NL);
    }

    private void assertNumberEvents(int expectedCount, EventFeedFilter filter, String[] lines) {

        countJSONLines(filter, lines);
        int matchingLineCount = countJSONLines(filter, lines);
        assertEquals("Expecting matching JSON lines for filter " + filter, expectedCount, matchingLineCount);

    }

    private int countJSONLines(EventFeedFilter filter, String[] lines) {
        int count = 0;
        for (String string : lines) {
            if (filter.matchesFilter(string)) {
                count++;
            }
        }
        return count;
    }

    public void testgetEventFeedType() throws Exception {

        // {"type":"judgement-types", "token":"pc2-8", "id":"OFE", "data": {"id":"OFE", "name":"Consider switching to another major", "penalty":true, "solved":false}}
        // {"type":"judgement-types", "token":"pc2-9", "id":"WA3", "data": {"id":"WA3", "name":"How did you get into this place ?", "penalty":true, "solved":false}}
        // {"type":"judgement-types", "token":"pc2-10", "id":"JE", "data": {"id":"JE", "name":"Contact Staff - you have no hope", "penalty":true, "solved":false}}
        // {"type":"languages", "token":"pc2-11", "id":"1", "data": {"id":"1","name":"Java"}}
        // {"type":"languages", "token":"pc2-12", "id":"1", "data": [{"id":"1","name":"Java"},{"id":"2","name":"Default"}]}
        // {"type":"languages", "token":"pc2-13", "id":"1", "data": [{"id":"1","name":"Java"},{"id":"2","name":"Default"},{"id":"3","name":"GNU C++ (Unix / Windows)"}]}

        EventFeedFilter filter = new EventFeedFilter();

        String string = "{\"type\":\"languages\", \"token\":\"pc2-11\", \"id\":\"java\", \"data\": {\"id\":\"java\",\"name\":\"Java\"}}";
        assertEquals(EventFeedType.LANGUAGES, filter.getEventFeedType(string));

    }

    public void testgetEventFeedSequence() throws Exception {

        EventFeedFilter filter = new EventFeedFilter();
        String string = "{\"type\":\"judgement-types\", \"token\":\"pc2-9\", \"id\":\"WA3\", \"data\": {\"id\":\"WA3\", \"name\":\"How did you get into this place ?\", \"penalty\":true, \"solved\":false}}";
        assertEquals("pc2-9", filter.getEventFeedSequence(string));
    }
}

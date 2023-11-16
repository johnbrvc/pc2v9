// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.util.ArrayList;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.util.JSONTool;
import edu.csus.ecs.pc2.services.core.JSONUtilities;

/**
 * WebService to handle languages
 * 
 * @author ICPC
 *
 */
@Path("/contests/{contestId}/judgement-types")
@Produces(MediaType.APPLICATION_JSON)
@Provider
@Singleton
public class JudgementTypeService implements Feature {

    private IInternalContest model;

    private IInternalController controller;

    public JudgementTypeService(IInternalContest inContest, IInternalController inController) {
        super();
        this.model = inContest;
        this.controller = inController;
    }

    /**
     * This method returns a representation of the current contest groups in JSON format. The returned value is a JSON array with one language description per array element, matching the description
     * at {@link https://clics.ecs.baylor.edu/index.php/Draft_CCS_REST_interface#GET_baseurl.2Flanguages}.
     * 
     * @return a {@link Response} object containing the contest languages in JSON form
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJudgementTypes(@PathParam("contestId") String contestId) {

        // check contest id
        if(contestId.equals(model.getContestIdentifier()) == false) {
            return Response.status(Response.Status.NOT_FOUND).build();        
        }
        
        ArrayList<CLICSJudgmentType> jlist = new ArrayList<CLICSJudgmentType>();
        
        for(Judgement judgment: model.getJudgements()) {
            if (judgment.isActive()) {
                jlist.add(new CLICSJudgmentType(model, judgment));
            }
        }
        try {
            ObjectMapper mapper = JSONUtilities.getObjectMapper();
            String json = mapper.writeValueAsString(jlist);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error creating JSON for judgment-types " + e.getMessage()).build();
        }
    }

    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("{judgmentId}/")
    public Response getJudgementType(@PathParam("contestId") String contestId, @PathParam("judgmentId") String judgmentType) {

        // check contest id
        if(contestId.equals(model.getContestIdentifier()) == true) {
        
            for(Judgement judgment: model.getJudgements()) {
                if (judgment.isActive() && JSONTool.getJudgementType(judgment).equals(judgmentType)) {
                    try {
                        ObjectMapper mapper = JSONUtilities.getObjectMapper();
                        String json = mapper.writeValueAsString(new CLICSJudgmentType(model, judgment));
                        return Response.ok(json, MediaType.APPLICATION_JSON).build();
                    } catch (Exception e) {
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error creating JSON for judgment-type " + judgmentType + " " + e.getMessage()).build();
                    }
                }
            }
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @Override
    public boolean configure(FeatureContext arg0) {
        // TODO Auto-generated method stub
        return false;
    }
}

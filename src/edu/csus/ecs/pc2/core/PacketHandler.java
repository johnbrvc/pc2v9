package edu.csus.ecs.pc2.core;

import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.IModel;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.packet.Packet;
import edu.csus.ecs.pc2.core.packet.PacketFactory;
import edu.csus.ecs.pc2.core.packet.PacketType.Type;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;

/**
 * Process all incoming packets.
 * 
 * Process packets. In {@link #handlePacket(IController, IModel, Packet) handlePacket} a packet is unpacked, model is updated, and
 * controller used to send packets as needed.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public final class PacketHandler {

    private PacketHandler() {

    }

    /**
     * Take each input packet, update the model, send out packets as needed.
     * 
     * @param controller
     * @param model
     * @param packet
     * @param connectionHandlerID 
     */
    public static void handlePacket(IController controller, IModel model, Packet packet, ConnectionHandlerID connectionHandlerID) {

        Type packetType = packet.getType();

        info("handlePacket " + packet);

        if (packetType.equals(Type.MESSAGE)) {
            PacketFactory.dumpPacket(System.err, packet);
        } else if (packetType.equals(Type.RUN_SUBMISSION_CONFIRM)) {
            Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
            model.addRun(run);
        } else if (packetType.equals(Type.RUN_SUBMISSION)) {
            // RUN submitted by team to server

            Run submittedRun = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
            RunFiles runFiles = (RunFiles) PacketFactory.getObjectValue(packet, PacketFactory.RUN_FILES);
            Run run = model.acceptRun(submittedRun, runFiles);

            // Send to team
            Packet confirmPacket = PacketFactory.createRunSubmissionConfirm(model.getClientId(), packet.getSourceId(), run);
            controller.sendToClient(confirmPacket);

            // Send to all other interested parties.
            controller.sendToAdministrators(confirmPacket);
            controller.sendToJudges(confirmPacket);
            controller.sendToScoreboards(confirmPacket);
            controller.sendToServers(confirmPacket);

        } else if (packetType.equals(Type.LOGIN_FAILED)) {
            String message = PacketFactory.getStringValue(packet, PacketFactory.MESSAGE_STRING);
            
            // TODO Handle this better via new login code.
            info("Login Failed: " + message);
            if (message.equals("No such account")) {
                message = "(Accounts Generated ??) ERROR " +message ;
            }
            model.loginDenied(packet.getDestinationId(), connectionHandlerID, message);
            
        } else if (packetType.equals(Type.RUN_NOTAVAILABLE)) {
            // Run not available
            // TODO code handle RUN_NOTAVAILABLE
            
            info(" Unhandled packet RUN_NOTAVAILABLE ");
            
        } else if (packetType.equals(Type.RUN_JUDGEMENT)) {
            // Judgement from judge.
            // TODO code handle RUN_JUDGEMENT
            
            info(" Unhandled packet RUN_JUDGEMENT ");
        } else if (packetType.equals(Type.RUN_UNCHECKOUT)) {
            // Cancel run
            // TODO code handle RUN_UNCHECKOUT
            info(" Unhandled packet RUN_UNCHECKOUT ");
            
        } else if (packetType.equals(Type.RUN_REQUEST)) {
            // Request Run
            // TODO code handle RUN_UNCHECKOUT
            info(" Unhandled packet RUN_REQUEST ");
            
        } else if (packetType.equals(Type.LOGIN_SUCCESS)) {
            if (! model.isLoggedIn()){
                info(" handlePacket LOGIN_SUCCESS before ");
                loadDataIntoModel(packet, controller, model, connectionHandlerID);
                info(" handlePacket LOGIN_SUCCESS after -- all settings loaded "); 
            } else {
                info(" handlePacket LOGIN_SUCCESS again: "+packet); 
            }

        } else {

            Exception exception = new Exception("PacketHandler.handlePacket Unhandled packet " + packet);
            info("Exception " + exception.getMessage());
            exception.printStackTrace(System.err);
        }

    }

    /**
     * Add contest data into the model.
     * 
     * This will read a packet and as it finds data loads it into the model.
     * <P>
     * It processes:
     * <ol>
     * <li> {@link PacketFactory#CLIENT_ID}
     * <li> {@link PacketFactory#SITE_NUMBER}
     * <li> {@link PacketFactory#SITE_NUMBER}
     * <li> {@link PacketFactory#LANGUAGE_LIST}
     * <li> {@link PacketFactory#PROBLEM_LIST}
     * <li> {@link PacketFactory#JUDGEMENT_LIST}
     * <li> {@link PacketFactory#CONTEST_TIME}
     * <ol>
     * 
     * @param packet
     * @param controller
     * @param model
     */
    private static void loadDataIntoModel(Packet packet, IController controller, IModel model, ConnectionHandlerID connectionHandlerID) {

        ClientId clientId = null;
        
        try {
            clientId = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
            if (clientId != null) {
                model.setClientId(clientId);
            }
        } catch (Exception e) {
            // TODO: log handle exception
            StaticLog.log("Exception logged ", e);
        }

        try {
            Integer siteNumber = (Integer) PacketFactory.getObjectValue(packet, PacketFactory.SITE_NUMBER);
            if (siteNumber != null) {
                controller.setSiteNumber(siteNumber.intValue());
            }
        } catch (Exception e) {
            // TODO: log handle exception
            StaticLog.log("Exception logged ", e);
        }

        info("Site set to " + model.getSiteNumber());

        try {
            Language[] languages = (Language[]) PacketFactory.getObjectValue(packet, PacketFactory.LANGUAGE_LIST);
            if (languages != null) {
                for (Language language : languages) {
                    model.addLanguage(language);
                }
            }
        } catch (Exception e) {
            // TODO: log handle exception
            StaticLog.log("Exception logged ", e);
        }

        try {
            Problem[] problems = (Problem[]) PacketFactory.getObjectValue(packet, PacketFactory.PROBLEM_LIST);
            if (problems != null) {
                for (Problem problem : problems) {
                    model.addProblem(problem);
                }
            }
        } catch (Exception e) {
            // TODO: log handle exception
            StaticLog.log("Exception logged ", e);
        }

        try {
            Judgement[] judgements = (Judgement[]) PacketFactory.getObjectValue(packet, PacketFactory.JUDGEMENT_LIST);
            if (judgements != null) {
                for (Judgement judgement : judgements) {
                    model.addJudgement(judgement);
                }
            }
        } catch (Exception e) {
            // TODO: log handle exception
            StaticLog.log("Exception logged ", e);
        }

        try {
            ContestTime contestTime = (ContestTime) PacketFactory.getObjectValue(packet, PacketFactory.CONTEST_TIME);
            if (contestTime != null) {
                model.addContestTime(contestTime, model.getSiteNumber());
            }
            
        } catch (Exception e) {
            // TODO: log handle exception
            StaticLog.log("Exception logged ", e);
        }
        
        try {
            Site[] sites = (Site[]) PacketFactory.getObjectValue(packet, PacketFactory.SITE_LIST);
            if (sites != null) {
                for (Site site : sites) {
                    model.addSite(site);
                }
            }
        } catch (Exception e) {
            // TODO: log handle exception
            StaticLog.log("Exception logged ", e);
        }
        
        if (model.isLoggedIn()){
            controller.startMainUI(clientId);
        }else{
            String message = "Trouble loggin in, check logs";
            model.loginDenied(packet.getDestinationId(), connectionHandlerID, message);
        }
    }

    /**
     * TODO - a temporary logging routine.
     * 
     * @param s
     */
    public static void info(String s) {
        StaticLog.info(s) ;
    }
}

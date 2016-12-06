package control.setup.agentGenerators;

import agents.Agent;
import agents.AgentPopulation;
import agents.InfBckAgent;
import agents.UninfFwdLevelAgent;
import control.Simulation;

import java.util.ArrayList;

/**
 * Created by Emily on 11/30/2016.
 */
public class UninformedAgentGenerator extends AgentGenerator {
    private Simulation sim;
    private AgentPopulation population;

    public UninformedAgentGenerator(AgentPopulation population, Simulation sim) {
        this.sim = sim;
        this.population = population;
    }

    public ArrayList<Agent> generateAgents(int numberOfAgents){
        ArrayList<Agent> agents = new ArrayList<>();
        for(int n = 0; n < numberOfAgents; n++){
            Agent newAgent = new InfBckAgent(
                    population,
                    true);
            agents.add(newAgent);
        }
        return agents;
    }

}
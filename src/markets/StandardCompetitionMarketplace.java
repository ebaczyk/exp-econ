package markets;

import agents.Agent;
import agents.AgentPopulation;
import assets.Asset;
import assets.AssetRegistry;
import control.Simulation;
import control.assetGenerators.AssetGenerator;
import control.brainAllocators.BrainAllocator;
import control.marketObjects.Bid;
import control.marketObjects.Offer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Created by Emily on 9/28/2016.
 */
public class StandardCompetitionMarketplace extends Marketplace{
    private Simulation sim;
    private AgentPopulation agentPopulation;
    private AssetRegistry registry;
    ArrayList<Agent> agents = new ArrayList<>();
    ArrayList<Asset> assets = new ArrayList<>();
    int numAgents;

    private Bid activeBid;
    private Offer activeOffer;
    Queue<Bid> bids;
    Queue<Offer> offers;

    ArrayList<Double> pastTransactionPrices = new ArrayList<>();

    int[] indices;

    public StandardCompetitionMarketplace(BrainAllocator brainAllocator,
                                          AssetGenerator assetGenerator,
                                          int numAgents,
                                          Simulation sim){
        this.sim = sim;
        this.numAgents = numAgents;

        this.agentPopulation = sim.getPopulation();
        this.agents = brainAllocator.generateAgents(numAgents);
        for(Agent agent : agents) {
            agentPopulation.init(agent);
        }
        this.agents = new ArrayList<>(agentPopulation.getAgents());

        this.registry = sim.getAssetRegistry();
        this.assets = assetGenerator.generateAssets((sim.getConfig().getnAgents() *
                sim.getConfig().getInitAssetEndowment()));
        for(Asset asset : assets) {
            registry.init(asset);
        }
        this.assets = new ArrayList<>(registry.getAssets());

        this.initializeAssetAllocation();

        this.activeBid = new Bid(null, sim.getConfig().getMinAssetValue());
        this.activeOffer = new Offer(null, sim.getConfig().getMaxAssetValue(), null);
        bids = new PriorityQueue<>();
        offers = new PriorityQueue<>();
        bids.add(activeBid);
        offers.add(activeOffer);

        this.setAgentOrder();
    }

    public boolean initializeAssetAllocation() {
        ArrayList<Asset> unallocatedAssets = new ArrayList<>(assets);
        for(Agent agent : agents) {
            for(int i = 0; i < sim.getConfig().getInitAssetEndowment(); i++) {
                int index = (int) Math.floor(Math.random() * unallocatedAssets.size());
                System.out.println(index);
                agent.endowAsset(unallocatedAssets.get(index));
                unallocatedAssets.remove(index);
            }
        }
        assert unallocatedAssets.size() == 0: "assets were created and were not allocated";
        return true;
    }

    public boolean runOneStep() {
        for(int i=0; i < agents.size(); i++) {
            Agent actingAgent = agents.get(indices[i]);
            if (Math.random() > 0.5d) {
                //the agent is a buyer
                Bid actingAgentBid = actingAgent.getBid();
                if (actingAgentBid.getBidPrice() > activeBid.getBidPrice()) {
                    bids.add(actingAgentBid);
                }
            } else {
                Offer actingAgentOffer = actingAgent.getOffer();
                if (actingAgentOffer.getOfferPrice() < activeOffer.getOfferPrice()) {
                    offers.add(actingAgentOffer);
                }
            }

            if (activeBid.getBidPrice() >= activeOffer.getOfferPrice()) {
                //a transaction has occurred
                double price = (activeBid.getBidPrice() + activeOffer.getOfferPrice()) / 2;
                activeBid.getBiddingAgent().buyAsset(activeOffer.getOfferedAsset(), price);
                activeOffer.getOfferingAgent().sellAsset(activeOffer.getOfferedAsset(), price);

                offers.remove();
                bids.remove();
            }


        }


        for(Agent agent : agents) {
            //agent.getFundamentalValue();
            if(agent.getID().equals("Agent1")) {
//                System.out.println("here");
                System.out.println(agent.getOwnedAssets().get(0).getID() + " " + agent.getOwnedAssets().get(0).getIntrinsicValue() +
                " " + agent.getOwnedAssets().get(1).getID() + " " + agent.getOwnedAssets().get(1).getIntrinsicValue());
            }

            //collect all agents' bids and asks
//            if(Math.random() > 0.5d) {
//                //the agent has been selected to be a buyer
//                bids.put(agent.getBid(), agent);
//            }  else {
//                offers.put(agent.getOffer(), agent);
//            }
            //sort them
//            ArrayList<Double> sortedBids = new ArrayList<Double>(bids.keySet());
//            Collections.sort(sortedBids);
//            ArrayList<Double> sortedOffers = new ArrayList<Double>(offers.keySet());
//            Collections.sort(sortedOffers);


        }
        return true;
    }

    public void setAgentOrder() {
        indices = new int[agents.size()];
        ArrayList<Agent> temp = new ArrayList<>(agents);
        Collections.shuffle(temp);
        for(int i = 0; i < agents.size(); i++) {
            indices[i] = temp.indexOf(agents.get(i));
        }
        System.out.println(indices);
    }

    public ArrayList<Double> getPastTransactionPrices() {
        return this.pastTransactionPrices;
    }

}

package net.scyllamc.matan.prudence.network;

import java.util.ArrayList;

import net.scyllamc.matan.prudence.Sentence;

public class Network {

	
		private static ArrayList<Neuron> current_neurons;

	 
	    public Network() {
	    	
	    	current_neurons = new ArrayList<>();
	    }

	    
	    public void addNeurons(Sentence sentence) {
	        current_neurons.add(Neuron.getNeuron(sentence));
	    }
	 
	    
	    public void adjustWages(ArrayList<Double> goodOutput) {
	      
	    	for(Neuron neuron : current_neurons){
	            double delta = goodOutput.get(i) - neurons.get(i).getOutput();
	            neurons.get(i).adjustWeights(delta);
	        }
	    }
}

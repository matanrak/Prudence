package net.scyllamc.matan.prudence.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;

import net.scyllamc.matan.prudence.Main;
import net.scyllamc.matan.prudence.Sentence;
import net.scyllamc.matan.prudence.Word;
import net.scyllamc.matan.prudence.utils.Utils;


public class Neuron {

	
    public static final int BIAS = 1;
    public static final double LEARNING_RATE = 0.1;

    public static HashMap<Sentence, Neuron> cache = new HashMap<Sentence, Neuron>();
    
    
    public static Neuron getNeuron(Sentence sentence) {

		if (cache.containsKey(sentence)) {
			return cache.get(sentence);
		}

		File f = new File(Main.mainDirectory + File.separator + "neurons" + File.separator + sentence.getAsJsonArray().toString() + ".json");

		if (f.exists()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(f));	
				return new Gson().fromJson(reader, Neuron.class);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		return new Neuron(sentence);
	}

	
    private ArrayList<Integer> inputs;
    private HashMap<String, Double> weights;
    
    private double biasWeight;
    private double output;
    private Sentence sentence;
    
    
    private Neuron(Sentence sentence) {
    	
        this.inputs = new ArrayList<>();
        this.weights =  new HashMap<String, Double>();
        this.sentence = sentence;
        this.biasWeight = Math.random();
    }
    
    
    private Double getWeight(String pos) {
        
    	if(!this.weights.containsKey(pos)){
        	this.weights.put(pos, Math.random());
    	}
    	
    	return this.weights.get(pos);
    }
    
    
    public void addGoodExample(Word input){
    	
    }    
    
    
    public void calculateOutput() {
       
    	double sum = 0;

        for (int i = 0; i < inputs.size(); i++) {
            sum += inputs.get(i) * weights.get(i);
        }
        
        sum += BIAS * biasWeight;

        output = Utils.sigmoidValue(sum);
    }
    
    
    public void save() {

		try {
			FileWriter writer = new FileWriter(Main.mainDirectory + File.separator + "neurons" + File.separator + sentence.getAsJsonArray().toString() + ".json");
			writer.write(new Gson().toJson(this));
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
    
    

}

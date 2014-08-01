package com.contralabs.inmap.recommendation;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class PerformanceEvaluation {

	public static final PerformanceEvaluation instance = new PerformanceEvaluation(); // FIXME Change to NullPerformanceEvaluation
	
	private List<Long> executionTimes;
	private List<Long> memoryUsed;
	private int quantityStores;
	
	public PerformanceEvaluation(){
		executionTimes = new ArrayList<Long>();
		memoryUsed = new ArrayList<Long>();
		quantityStores = 50;
	}
	
	public void addExecutionTime(long time){
		executionTimes.add(time);
	}
	
	public void addMemoryUsed(long memory){
		memoryUsed.add(memory);
	}
	
	public double getMeanExecutionTime(){
		Long mean = Long.valueOf(0);
		for(Long et : executionTimes)
			mean += et;
		return mean.doubleValue()/executionTimes.size();
	}
	
	public double getMeanMemoryUsed(){
		Long mean = Long.valueOf(0);
		for(Long et : memoryUsed)
			mean += et;
		return mean.doubleValue()/memoryUsed.size();
	}
	
	public void logResults(){
		Log.d("PerformanceEvaluation", "MeanExecutionTime: " + getMeanExecutionTime() + " MeanMemoryUsed: " + getMeanMemoryUsed() + " Quantity Stores: " + getQuantityStore() + " DataLenght: " + memoryUsed.size());
	}
	
	public void clear(){
		memoryUsed.clear();
		executionTimes.clear();
	}
	
	public void increaseStores(){
		quantityStores += 50;
	}
	
	public String getQuantityStore(){
		return String.valueOf(quantityStores);
	}
	
	private class NullPerformanceEvaluation extends PerformanceEvaluation{
		private NullPerformanceEvaluation(){
		}
		
		@Override
		public void addExecutionTime(long time) {
		}
		
		@Override
		public void addMemoryUsed(long memory) {
		}
		
		@Override
		public double getMeanExecutionTime() {
			return 0;
		}
		
		@Override
		public double getMeanMemoryUsed() {
			return 0;
		}
		
		@Override
		public void logResults() {
		}
		
		@Override
		public void clear() {
		}
		
		@Override
		public void increaseStores() {
		}
		
		@Override
		public String getQuantityStore() {
			return null;
		}
	}
}

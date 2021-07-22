package com.fp;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class AssociationRules
{
	public HashMap<String, Double> associationRules = new HashMap<String, Double>();
	public boolean success = true;
	FrequentPatternGeneration frequentPatternGeneration;
	
	public AssociationRules(FrequentPatternGeneration frequentPatternGeneration,
							double confidence,
							int largestPatternCount)
	{
		this.frequentPatternGeneration = frequentPatternGeneration;
		long value;
		int size;
		double confidenceTemp, t;
		Iterator<Map.Entry<ArrayList<String>, Long>> iterator;
		ArrayList<String> temp;
		ArrayList<String> left = new ArrayList<String>();
		ArrayList<String> right = new ArrayList<String>();
		Map.Entry<ArrayList<String>, Long> entry;
		iterator = frequentPatternGeneration.frequentPattern.entrySet().iterator();
		while(iterator.hasNext())	
		{
			entry = iterator.next();
			temp = entry.getKey();
			size = temp.size();
			if(size == largestPatternCount)
			{
				if(size > 63)
				{
					success = false;
					return;
				}
				value = entry.getValue();
				for(long m = 1; m < ((1<<size)-1); m++) 
				{
					for(long n = 0; n < size; n++) 
					{
						if((m & (1 << n)) > 0) 
						{
							left.add(temp.get((int)n));
							continue;
						}
						right.add(temp.get((int)n));
					}
					t = frequentPatternGetCount(left);
					if(t != 0.0)
					{
						confidenceTemp = (value*100)/t;
						if((confidenceTemp >= confidence) && (confidenceTemp <= 100.0))
						{
							associationRules.put(left.toString() +
									" -> " + right.toString(), confidenceTemp);
						}
					}
					left.clear();
					right.clear();
				}
			}
		}
	}
	
	private double frequentPatternGetCount(ArrayList<String> left)
	{
		Long count = frequentPatternGeneration.frequentPattern.get(left);
		if(count == null)
		{
			ArrayList<String> temp = null;
			Iterator<Map.Entry<ArrayList<String>, Long>> iterator;
			Map.Entry<ArrayList<String>, Long> entry;
			iterator = frequentPatternGeneration.frequentPattern.entrySet().iterator();
			boolean flag = false;
			while(iterator.hasNext())	
			{
				entry = iterator.next();
				temp = entry.getKey();
				if(temp.size() == left.size())
				{
					for(int i = 0; i < left.size(); i++)
					{
						if((flag = temp.contains(left.get(i))) == false)
						{
							break;
						}
					}
					if(flag)
					{
						count = entry.getValue();
						break;
					}
				}
			}
		}
		if(count == null)
		{
			return 0.0;
		}
		return count;
	}
}
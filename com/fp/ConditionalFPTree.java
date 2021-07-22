package com.fp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class ConditionalFP
{
	HashMap<String, Long> map = new HashMap<String, Long>();
}

public class ConditionalFPTree
{
	ConditionalPatternBase pattern;
	public ConditionalFP[][] conditionalFP;
	HashMap<Integer, Integer> mapIndex = new HashMap<Integer, Integer>();
	
	public ConditionalFPTree(ConditionalPatternBase pattern, int support)
	{
		this.pattern = pattern;
		conditionalFP = new ConditionalFP[pattern.conditionalPattern.length][];
		int i, j, k, mi;
		for(i = 0; i < conditionalFP.length; i++)
		{
			if(pattern.conditionalPattern[i] != null)
			{
				subTreeIndex(i);
				for(j = 0; j < pattern.conditionalPattern[i].length; j++)
				{
					for(k = 0, mi = mapIndex.get(pattern.conditionalPattern[i][j].branch); 
						k < pattern.conditionalPattern[i][j].list.size();
						k++)
					{
						conditionalFP[i][mi].map.merge(
							pattern.conditionalPattern[i][j].list.get(k), 
							pattern.conditionalPattern[i][j].count,
							Long::sum);
					}
				}
				for(j = 0; j < conditionalFP[i].length; j++)
				{
					Iterator<Map.Entry<String, Long>> iterator = 
							conditionalFP[i][j].map.entrySet().iterator();
					Map.Entry<String, Long> entry;
					while(iterator.hasNext())	
					{
						entry = iterator.next();
						if(entry.getValue() < support)
						{
							iterator.remove();
						}
					}
				}
			}
		}
	}
	
	private void  subTreeIndex(int index)
	{
		mapIndex.clear();
		int i = 0;
		for(int j = 0; i < pattern.conditionalPattern[index].length; i++)
		{
			if(mapIndex.containsKey(pattern.conditionalPattern[index][i].branch) == false)
			{
				mapIndex.put(pattern.conditionalPattern[index][i].branch, j++);
			}
		}
		conditionalFP[index] = new ConditionalFP[mapIndex.size()];
		for(i = 0; i < mapIndex.size(); i++)
		{
			conditionalFP[index][i] = new ConditionalFP();
		}
	}
}
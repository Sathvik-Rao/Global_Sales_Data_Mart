package com.fp;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class FrequentPatternGeneration
{
	public HashMap<ArrayList<String>, Long> frequentPattern =
		new HashMap<ArrayList<String>, Long>();
	public boolean success = true;
	public int largestPatternCount = Integer.MIN_VALUE;
	int largestPatternCountTemp;
	
	public FrequentPatternGeneration(NodeLink link, ConditionalFPTree conditionalFP)
	{
		ArrayList<String> temp;
		Iterator<Map.Entry<String, Long>> iterator;
		Map.Entry<String, Long> entry;
		ArrayList<String> list1 = new ArrayList<String>();
		ArrayList<Long> list2 = new ArrayList<Long>();
		for(int i = 0; i < link.itemSetAscending.length; i++)
		{
			if(conditionalFP.conditionalFP[i] != null)
			{
				for(int j = 0; j < conditionalFP.conditionalFP[i].length; j++)
				{
					int size = conditionalFP.conditionalFP[i][j].map.size();
					if(size != 0)
					{
						if(size > 63)
						{
							success = false;
							return;
						}
						iterator = conditionalFP.conditionalFP[i][j].map.entrySet().iterator();
						while(iterator.hasNext())	
						{
							entry = iterator.next();
							list1.add(entry.getKey());
							list2.add(entry.getValue());
						}
						for(long m = 1, min; m < (1<<size); m++) 
						{
							min = Long.MAX_VALUE;
							largestPatternCountTemp = 1;
							temp = new ArrayList<String>();
							temp.add(link.itemSetAscending[i].key);
							for(long n = 0; n < size; n++) 
							{
								if((m & (1 << n)) > 0) 
								{
									largestPatternCountTemp++;
									temp.add(list1.get((int)n));
									if(min > list2.get((int)n))
									{
										min = list2.get((int)n);
									}
								}
							}
							if(largestPatternCount < largestPatternCountTemp)
							{
								largestPatternCount = largestPatternCountTemp;
							}
							frequentPattern.merge(temp, min, Long::sum);
						}
						list1.clear();
						list2.clear();
					}
				}
			}
		}
	}
}
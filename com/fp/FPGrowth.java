package com.fp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Arrays;
import java.util.Collections;


public class FPGrowth
{
	BufferedReader csvReader;
	public FrequentPatternGeneration frequentPatternGeneration;
	public AssociationRules associationRules;
	String csvPath;
	double support;
	int support0;
	
	public boolean success = true;
	boolean stop = false;
	public String errorMessage;
	
	ItemSet[] itemSetAscending;
	FPTree fpTree;
	NodeLink link;
	
	public FPGrowth(String csvPath, double support, double confidence)
	{
		try
		{
			csvReader = new BufferedReader(new FileReader(csvPath));
		}
		catch(FileNotFoundException e)
		{
			errorMessage = "DataSet(File) not found (" + csvPath + ") or inaccessible.";
			success = false;
			return;
		}
		this.csvPath = csvPath;
		this.support = support;
		
		//step1
		itemSetCount();
		if(success || stop)
		{
			//step2(Conditional Pattern Base)
			ConditionalPatternBase pattern = new ConditionalPatternBase(link, fpTree);
			fpTree = null;
			
			//step3(Conditional FP-tree)
			ConditionalFPTree conditionalFP = new ConditionalFPTree(pattern, support0);
			pattern = null;
			
			//step4(Frequent Patterns Generated)
			frequentPatternGeneration = 
				new FrequentPatternGeneration(link, conditionalFP);
			if(frequentPatternGeneration.success == false)
			{
				errorMessage = "Input(s) not compatible";
				success = false;
				return;
			}
			link = null;
			conditionalFP = null;
			
			//step5(Generating association rules)
			ArrayList<String> temp;
			for(ItemSet x : itemSetAscending)
			{
				temp = new ArrayList<String>(1);
				temp.add(x.key);
				frequentPatternGeneration.frequentPattern.put(temp, x.value);
			}
			itemSetAscending = null;
			associationRules = new AssociationRules(frequentPatternGeneration,
									confidence,
									frequentPatternGeneration.largestPatternCount);
			if(associationRules.success == false)
			{
				errorMessage = "Input(s) not compatible";
				success = false;
				return;
			}
		}
	}
	
	public HashMap<ArrayList<String>, Long> getFrequentItemSets()
	{
		return frequentPatternGeneration.frequentPattern;
	}
	
	public HashMap<String, Double> getAssociationRules()
	{
		return associationRules.associationRules;
	}
	
	private void itemSetCount()
	{
		//create itemset along with count
		HashMap<String, Long> itemSetCountMap = new HashMap<String, Long>();
		String row;
		int i, j, k;
		long no_of_transactions = 0;
		try
		{
			while((row = csvReader.readLine()) != null) 
			{
				String[] data = row.split(",");
				for(i = 0; i < data.length; i++)
				{
					itemSetCountMap.merge(data[i], 1L, Long::sum);
				}
				no_of_transactions++;
			}
			support0 = (int)java.lang.Math.ceil((no_of_transactions * support) / 100.0);
			
			//remove items with count less than min support count
			Iterator<Map.Entry<String, Long>> iterator = itemSetCountMap.entrySet().iterator();
			Map.Entry<String, Long> entry;
			j = 0;
			ItemSet[] itemSetAscendingTemp = new ItemSet[itemSetCountMap.size()];
			while(iterator.hasNext())	
			{
				entry = iterator.next();
				if(entry.getValue() < support0)
				{
					iterator.remove();
					continue;
				}
				itemSetAscendingTemp[j++] = new ItemSet(entry.getKey(), entry.getValue());
			}
			if(j == 0)
			{
				stop = true;
				return;
			}
			Arrays.sort(itemSetAscendingTemp, 0, j);
			itemSetAscending = new ItemSet[j];
			for(i = 0; i < j; i++)
			{
				itemSetAscending[i] = itemSetAscendingTemp[i];
			}
			link = new NodeLink(itemSetAscending);
			
			//decending order of each transaction
			csvReader.close();
			csvReader = new BufferedReader(new FileReader(csvPath));
			String[] itemSetDescendingLine, itemSetDescendingLineTemp, itLine;
			fpTree = new FPTree(link);
			Long valueTemp;
			while((row = csvReader.readLine()) != null) 
			{
				String[] data = row.split(",");
				itemSetDescendingLineTemp = new String[data.length];
				for(i = 0, j = 0; i < data.length; i++)
				{
					//decending order
					if((valueTemp = itemSetCountMap.get(data[i])) != null)
					{
						itemSetDescendingLineTemp[j++] = data[i];
					}
				}
				if(j == 0)
				{
					continue;
				}
				itemSetDescendingLine = new String[j];
				itLine = new String[j];
				for(i = 0; i < j; i++)
				{
					itemSetDescendingLine[i] = itemSetDescendingLineTemp[i];
				}
				for(i = itemSetAscending.length-1, k = 0; i > -1; i--)
				{
					for(j = 0; j < itemSetDescendingLine.length; j++)
					{
						if(itemSetAscending[i].key.equals(itemSetDescendingLine[j]))
						{
							itLine[k++] = itemSetDescendingLine[j];
							break;
						}
					}						
				}
				
				//create fp tree
				fpTree.add(itLine);
			}
			csvReader.close();
		}
		catch(IOException e)
		{
			errorMessage = "DataSet(File) not found (" + csvPath + 
							") or inaccessible or input/output error.";
			success = false;
			return;
		}
	}
}
package com.fp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class ConditionalPattern
{
	ArrayList<String> list = new ArrayList<String>();
	long count = 0;
	int branch;
}

public class ConditionalPatternBase
{
	NodeLink link;
	FPTree fpTree;
	HashMap<String, Integer> rootChildren = new HashMap<String, Integer>();
	public ConditionalPattern[][] conditionalPattern;
	
	public ConditionalPatternBase(NodeLink link, FPTree fpTree)
	{
		this.link = link;
		this.fpTree = fpTree;
		fillRootChildren();
		conditionalPattern = new ConditionalPattern[link.itemSetAscending.length][];
		Node ref, prev = null;
		for(int i = 0; i < link.itemSetAscending.length; i++)
		{
			if(link.link[i].size() != 0)
			{
				conditionalPattern[i] = new ConditionalPattern[link.link[i].size()];
				for(int j = 0; j < link.link[i].size(); j++)
				{
					ref = link.link[i].get(j);
					ConditionalPattern temp = new ConditionalPattern();
					temp.count = ref.count;
					ref = ref.parent;
					while(ref.value != null)
					{
						temp.list.add(ref.value);
						prev = ref;
						ref = ref.parent;
					}
					temp.list.trimToSize();
					temp.branch = rootChildren.get(prev.value);
					conditionalPattern[i][j] = temp;
				}
			}
			link.link[i] = null;
		}
		rootChildren = null;
	}
	
	private void fillRootChildren()
	{
		Iterator<Map.Entry<String, Node>> iterator = fpTree.root.children.entrySet().iterator();
		Map.Entry<String, Node> entry;
		int i = 0, index, index0;
		while(iterator.hasNext())	
		{
			entry = iterator.next();
			rootChildren.put(entry.getKey(), i++);
			index = LinearSearch.search0(link.itemSetAscending, entry.getKey());
			index0 = LinearSearch.search1(link.link[index], entry.getValue());
			link.link[index].remove(index0);
		}
		link.trim();
	}
}
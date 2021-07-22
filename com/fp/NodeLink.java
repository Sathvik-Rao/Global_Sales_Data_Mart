package com.fp;

import java.util.ArrayList;

public class NodeLink
{
	public ItemSet[] itemSetAscending;
	public ArrayList<Node>[] link;
	
	@SuppressWarnings("unchecked")
	public NodeLink(ItemSet[] itemSetAscending)
	{
		this.itemSetAscending = itemSetAscending;
		link = (ArrayList<Node>[])new ArrayList[itemSetAscending.length];
		for(int i = 0; i < itemSetAscending.length; i++)
		{
			link[i] = new ArrayList<Node>();
		}
	}
	
	public void add(Node node)
	{
		int index;
		if((index = LinearSearch.search0(itemSetAscending, node.value)) > -1)
		{
			link[index].add(node);
		}
	}
	
	public void trim()
	{
		for(int i = 0; i < itemSetAscending.length; i++)
		{
			link[i].trimToSize();
		}
	}
}
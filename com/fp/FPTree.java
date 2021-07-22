package com.fp;

import java.util.HashMap;

class Node
{
	String value = null;
	long count = 0;
	Node parent = null;
	HashMap<String, Node> children = null;
}

public class FPTree
{
	public Node root;
	NodeLink link;
	
	public FPTree(NodeLink link)
	{
		this.link = link;
		root = new Node();
	}
	
	public void add(String[] itemSetDescendingLine)
	{
		Node temp, ref = root;
		for(int i = 0; i < itemSetDescendingLine.length; i++)
		{
			if(ref.children == null)
			{
				ref.children = new HashMap<String, Node>();
				temp = new Node();
				temp.count++;
				temp.value = itemSetDescendingLine[i];
				ref.children.put(itemSetDescendingLine[i], temp);
				temp.parent = ref;
				link.add(temp);
			}
			else
			{
				if((temp = ref.children.get(itemSetDescendingLine[i])) != null)
				{
					temp.count++;
				}
				else
				{
					temp = new Node();
					temp.count++;
					temp.value = itemSetDescendingLine[i];
					ref.children.put(itemSetDescendingLine[i], temp);
					temp.parent = ref;
					link.add(temp);
				}
			}
			ref = temp;
		}
	}
}
package com.fp;

import java.util.ArrayList;

public class LinearSearch
{	
	public static int search0(ItemSet[] a, String key) 
	{
		for(int i = 0; i < a.length; i++)
		{
			if(a[i].key.equals(key))
			{
				return i;
			}
		}
		return -1;
	}
	
	public static int search1(ArrayList<Node> a, Node key) 
	{
		for(int i = 0; i < a.size(); i++)
		{
			if(a.get(i).equals(key))
			{
				return i;
			}
		}
		return -1;
	}
}

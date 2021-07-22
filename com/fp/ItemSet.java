package com.fp;

public class ItemSet implements Comparable<ItemSet>
{
	public String key;
	public long value;
	
	public ItemSet(String key, long value)
	{
		this.key = key;
		this.value = value;
	}
	
	@Override
	public int compareTo(ItemSet set) 
	{
		return (value < set.value) ? -1 : ((value == set.value) ? 0 : 1);
	}
}
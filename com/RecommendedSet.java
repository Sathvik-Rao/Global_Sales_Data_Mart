package com;

public class RecommendedSet implements Comparable<RecommendedSet>
{
	public String key;
	public double value;
	
	public RecommendedSet(String key, double value)
	{
		this.key = key;
		this.value = value;
	}
	
	@Override
	public int compareTo(RecommendedSet set) 
	{
		return (value > set.value) ? -1 : ((value == set.value) ? 0 : 1);
	}
}
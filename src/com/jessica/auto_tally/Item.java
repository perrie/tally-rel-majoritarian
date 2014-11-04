package com.jessica.auto_tally;

import java.util.Comparator;

/**
 * Item acts as an item in the ballot. 
 * @author perrie
 *
 */
public class Item implements Comparable<Item>{

	/**
	 * The ID of this item
	 */
	private int id;
	/**
	 * The number of "Do Not Wants" for this item
	 */
	private int numDoNotWant;
	/**
	 * The estimated cost for this item
	 */
	private double cost;
	/**
	 * The number of voters for this item per round
	 */
	private int numBaskets;
	/**
	 * Name of this item
	 */
	private String name;
	/**
	 * Is this item eliminated?
	 */
	private boolean isEliminated;
	/**
	 * Creates and intializes an instance of an item
	 * @param i the item's ID
	 * @param dnw the number of "Do Not Wants" for this item
	 * @param c the cost of the item
	 * @param v the number of voters for this item per round
	 */
	public Item(int i, int dnw, double c, int b, String n) {
		id = i;
		numDoNotWant = dnw;
		cost = c;
		numBaskets = b;
		name = n;
		isEliminated = false;
	}
	/**
	 * Sets that this items is eliminated
	 */
	public void isEliminated() {
		isEliminated = true;
	}
	/**
	 * Returns if this item is eliminated
	 * @return if this item is eliminated = true
	 */
	public boolean isDefinitelyEliminated() {
		return isEliminated;
	}
	/**
	 * Returns the name of this item
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the ID of this item
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the ID of this item
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Returns the number of "Do Not Wants" for this item
	 * @return the numDoNotWant
	 */
	public int getNumDoNotWant() {
		return numDoNotWant;
	}

	/**
	 * Sets the Number of "Do Not Wants" for this item
	 * @param numDoNotWant the numDoNotWant to set
	 */
	public void setNumDoNotWant(int numDoNotWant) {
		this.numDoNotWant = numDoNotWant;
	}
	/**
	 * Increments the Number of "Do Not Wants" for this item
	 */
	public void incNumDoNotWant() {
		this.numDoNotWant++;
	}

	/**
	 * Returns the cost of this item
	 * @return the cost
	 */
	public double getCost() {
		return cost;
	}

	/**
	 * Sets the cost of this item
	 * @param cost the cost to set
	 */
	public void setCost(double cost) {
		this.cost = cost;
	}

	/**
	 * Returns the number of voters (baskets) that include this item 
	 * in their baskets
	 * @return the numVoters
	 */
	public int getNumBaskets() {
		return numBaskets;
	}

	/**
	 * Resets the number of voters that include this item 
	 * in their baskets
	 * @param numVoters the numVoters to set
	 */
	public void resetNumBaskets() {
		this.numBaskets= 0;
	}
	/**
	 * Increments the number of voters that include this item 
	 * in their baskets
	 */
	public void incNumBaskets() {
		this.numBaskets++;
	}
	/**
	 * toString representation
	 */
	public String toString() {
		 return "\t" + id + "," + name + "," + cost + ","
				 + numDoNotWant + "," + numBaskets + "\n";
	}
	/**
	 * Compares: Returns the less costing
	 * @param arg0 the item to compare to (for step 3 - where we must
	 * sort the ArrayList by popularity in number of baskets)
	 */
	@Override
	public int compareTo(Item arg0) {
		return (int) (arg0.numBaskets - this.numBaskets);
	}
	
	/**
	 * Checks if this item equals another (for use in testing)
	 * @param arg other object
	 * @return if items (this and the other object) are equal
	 */
	@Override
	public boolean equals(Object arg) {
		if (arg instanceof Item) {
			Item arg0 = (Item) arg;
		return (id == arg0.getId()
				&& numDoNotWant == arg0.getNumDoNotWant()
				&& Math.abs(cost - arg0.getCost()) < 0.001
				&& numBaskets == arg0.getNumBaskets()
				&& name.equals(arg0.getName())
				&& isEliminated == arg0.isDefinitelyEliminated());
		}
		else return false;
	}
}

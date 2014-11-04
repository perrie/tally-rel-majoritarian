package com.jessica.auto_tally;

import java.util.ArrayList;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Voter acts as an individual resident whom
 * voted in the referendum
 * @author perrie
 *
 */
public class Voter {

	/**
	 * The initial rankings (should match voter's ballot) --
	 * excluding the items voted with "Do Not Want". Once an item
	 * is removed in Step 4, it will be removed from this list. As
	 * a SortedMap, it is ordered by it's ranking (E.g., first)
	 */
	private SortedMap<Integer,Item> initRankings;
	/**
	 * Round rankings -- may be changed depending on what
	 * items are left and their popularity
	 */
	private ArrayList<Item> basket;
	/**
	 * Creates an instance of a voter
	 */
	public Voter() {
		initRankings = new TreeMap<Integer,Item>();
		basket = new ArrayList<Item>();
	}
	
	/**
	 * toString representation of this Voter
	 */
	public String toString() {
		String returnString = "";
	//	returnString += "Initial Vote: " + initRankings + "\n";
		returnString += "Round Vote (Basket): " + basket + "\n";
		returnString += "/----------------------------/\n";
		return returnString;
	}

	/**
	 * Adds an item to list of the initial rankings. Does not include items
	 * that have been rated as "Do Not Want"
	 * @param item item
	 */
	public void addItem(Integer rank, Item item) {
		initRankings.put(rank, item);
		
	}
	/**
	 * Remove from initial rankings all votes that have eliminated item
	 * @param id
	 */
	public void remove(int id) {
		for (Integer rank : initRankings.keySet()) {
			Item item = initRankings.get(rank);
			if (item.getId() == id) {
				this.initRankings.remove(rank);
				return;
			}
		}
		
	}
	/**
	 * Generate round rankings based on the amount of budget left
	 * @param bUDGET budget entered
	 */
	public void generateRoundRankings(double budget) {
		basket.clear();
		if (initRankings.isEmpty())
			return;
		Integer[] keys =  (Integer[]) initRankings.keySet()
				.toArray(new Integer[initRankings.size()]);
		int counter = 0;
		while (basket.size() < initRankings.size()
				&& counter < keys.length) {
			Item addedItem = initRankings.get(keys[counter]);
			if (addedItem.getCost() <= budget
					&& !addedItem.isDefinitelyEliminated()) {
				addedItem.incNumBaskets();
				budget -= addedItem.getCost();
				basket.add(addedItem);
			}
			counter++;			
		}
		
	}

	/**
	 * Returns a nice string representation of the initial rankings (e.g., should reflect
	 * the paper ballot of the voter)
	 * @return string representation of intial rankings
	 */
	public String getInitRankingsToString() {
		String retString ="";
		for (Integer key : initRankings.keySet()) {
			retString += "\t" + key + "\t" + initRankings.get(key).getName() + "\n";
		}
		return retString;
	}
	
	/**
	 * Equals method -- overrides Object.equals(...)
	 * (for testing only)
	 * @param obj object to compare
	 * @return if object to compares matches this one
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Voter) {
			Voter voter = (Voter) obj;
			return (initRankings.equals(voter.initRankings)
					&& basket.equals(voter.basket));
		}
		return false;
		
	}

	/**
	 * Checks if initial ranking contains a certain rank (to make sure
	 * that the same rank is not entered twice in each ballot)
	 * @param rank rank to test
	 * @return true if the ballot contains does NOT contain rank
	 */
	public boolean doesNotHave(int rank) {
		return !(initRankings.containsKey(rank));
	}

	/**
	 * Returns the size of the voter's basket for a round
	 * @return the size of the basket
	 */
	public int getBasketSize() {
		return basket.size();
	}
	/**
	 * Returns the basket item given an inputed rank. If rank doesn't exist,
	 * this function return null.
	 * @param rank to get item in basket
	 * @return Item of rank; if rank doesn't exist, null.
	 */
	public Item getBasketItem(int rank) {
		if (rank < basket.size())
			return basket.get(rank);
		else return null;
	}
	
}

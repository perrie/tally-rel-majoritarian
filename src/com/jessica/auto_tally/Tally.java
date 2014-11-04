package com.jessica.auto_tally;

import java.util.ArrayList;
import java.util.Collections;

import com.jessica.IOfile.ReadFile;

/**
 * Tally acts as the main program that goes through the steps Will
 * outlined.
 * @author perrie
 *
 */
public class Tally {
	
	public static boolean DEBUG = false;
	public static boolean PRINTOUT = true;
	public static double BUDGET = 3500.00;
	public static String[] COLMAP = {"-", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
		"O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y","Z",
		"AA","AB","AC","AD","AE","AF","AG","AH","AI","AJ","AK","AL", "AM","AN",
		"AO","AP","AQ","AR","AS","AT","AU","AV","AW","AX","AY","AZ"};
	/**
	 * Runs the program
	 * @param args input parameters (non)
	 */
	public static void main(String[] args) {
		
		System.out.println("Possible arguments: java -jar thisjar.jar [vote_file] [cost_file] [budget]");
		
		ArrayList<Voter> votes = new ArrayList<Voter>();
		ArrayList<Item> items = new ArrayList<Item>();
		String filenameVotes = (args.length > 0) ? args[0] : "votes50.csv";
		String filenameCosts = (args.length > 1) ? args[1] : "costs50.csv";
		BUDGET = (args.length > 2) ? Integer.parseInt(args[2]) : BUDGET;
		ArrayList<Item> itemsToBuy = new ArrayList<Item>();
		System.out.println("Selected Votes file: " +  filenameVotes);
		System.out.println("Selected Costs file: " + filenameCosts);
		
		// READ COSTS
		readCosts(items, filenameCosts);
		if (DEBUG) System.out.println("READ IN COSTS: \n" + items);
		if (PRINTOUT) {
			System.out.println("READ IN COSTS:");
			for (Item item : items)
				System.out.println("\t" + item.getCost() + "\t" + item.getName());
			System.out.println();
		}
		
	
		// READ VOTES
		readVotes(votes, items, filenameVotes);
		if (DEBUG || PRINTOUT) { 
			System.out.println("READ IN VOTES:\nTotal Number of Voters: " + votes.size() + "\n");
			for (int v = 0; v < votes.size(); v++) {
				System.out.println("\tVOTER: " + COLMAP[v+1]);
				System.out.println(votes.get(v).getInitRankingsToString());
			}
		}
		
		// STEP 1
		if (DEBUG || PRINTOUT) System.out.println("\n =======> STEP 1:");
		step1RemoveItemsGT50DoNotWants(items, votes, votes.size(), itemsToBuy);
		if (!itemsToBuy.isEmpty())
			printItemsToBuy(itemsToBuy);
		
		int round = 0;
		while (itemExists(items)) {
			
			round++;
			if (DEBUG || PRINTOUT) {
				System.out.println("\n------------------------------------------------------------"
						+ "\n                     " + " ROUND " + round + "                     "
						+ "\n------------------------------------------------------------");
				System.out.println("REMAINING BUDGET: " + BUDGET + "\n");
			}
			
			// STEP 2
			step2generateRoundRanked(votes, items);
			if (DEBUG || PRINTOUT)
				System.out.println("\n =======> STEP 2:");
			if (DEBUG) {
				for (int v = 0; v < votes.size(); v++) {
					System.out.println("VOTER: " + COLMAP[v+1]);
					System.out.println(votes.get(v).toString());
				}
			}
			if (PRINTOUT) {
				System.out.println("Baskets generated for this round:");
				for (int v = 0; v < votes.size(); v++) {
					System.out.println("\tVOTER: " + COLMAP[v+1]);
					double costs = 0.0;
					for (int i = 0; i < votes.get(v).getBasketSize(); i++) {
						System.out.println("\t" + (i+1) + ". "
								+ votes.get(v).getBasketItem(i).getName()
								+ " (Cost: " + votes.get(v).getBasketItem(i).getCost() + ")");
						costs += votes.get(v).getBasketItem(i).getCost();
					}
					System.out.println("\tTotal cost: " + costs + "\n");
				}
			}
			
			// STEP 3
			if (DEBUG || PRINTOUT) System.out.println("\n =======> STEP 3:");
			boolean step3selects = step3selectedHighest(items,
					itemsToBuy, votes.size());
			
			// STEP 4
			if (!step3selects) { 
				if (DEBUG || PRINTOUT) System.out.println("\n =======> STEP 4:");
				step4eliminateFewestItem(items, votes);
			}
		
		}
		
		if (PRINTOUT) {
			System.out.println("\n******************** Terminating Loop ********************\n");
			if (items.isEmpty())
				System.out.println("No items remain.");
			else {
				System.out.println("All remaining items costs are above the remaining budget: " + BUDGET);
				for (Item item : items) 
					System.out.println("\t" + item.getName() + " (Cost: " + item.getCost() + ")");
			}
		}
		printItemsToBuy(itemsToBuy);
	}
	/**
	 * Checks if an item exists such that is under the amount of budget
	 * @param items list of items
	 * @return if an items still exists...
	 */
	private static boolean itemExists(ArrayList<Item> items) {
		for (Item item : items) {
			if (item.getCost() < BUDGET)
				return true;
		}			
		return false;

	}
	/**
	 * STEP 4
	 * If there were no items purchased in Step 3, eliminate whatever 
	 * item is in the fewest percentage of baskets (if there is a tie 
	 * eliminate the more expensive item, if there is still a tie 
	 * eliminate both items). (If there was an item purchased in Step 
	 * 3 skip this step).
	 * @param items list of items
	 * @param votes 
	 */
	private static void step4eliminateFewestItem(ArrayList<Item> items, 
			ArrayList<Voter> votes) {
		
		/* 
		 * Find the minimum
		 */
		Item minItem = null;
		System.out.println("#Baskets\tName");
		for (Item item : items) {
			if (minItem == null 
					|| item.getNumBaskets() < minItem.getNumBaskets() 
					|| (item.getNumBaskets() == minItem.getNumBaskets()
							&& item.getCost() > minItem.getCost()))
				minItem = item;
			if (PRINTOUT) System.out.println("\t" + item.getNumBaskets() 
					+ "\t" + item.getName() + "\t(Cost: " + item.getCost()
					+ ")");
		}
		/*
		 * Remove all items that fit this minimum
		 */
		ArrayList<Item> list = new ArrayList<Item>();
		for (Item item : items) {
			if (item.getNumBaskets() == minItem.getNumBaskets()
					&& item.getCost() == minItem.getCost())
				list.add(minItem);
		}
		for (Item item : list) {
			if (DEBUG || PRINTOUT) System.out.println("Eliminating ... " + item.getName());
			item.isEliminated();
			items.remove(item);
			for (Voter voter : votes) {
				voter.remove(item.getId());
			}
		}
		
		
	}

	/**
	 * (Helper method) prints end result
	 * @param itemsToBuy
	 */
	private static void printItemsToBuy(ArrayList<Item> itemsToBuy) {
		double costs = 0.0; 
		System.out.println("\nFINAL SELECTIONS:\n");
		for (int i=0; i<itemsToBuy.size(); i++) {
			Item item = itemsToBuy.get(i);
			System.out.println("\t" + (i+1) + ". " + item.getCost() + "\t" + item.getName());
			costs += item.getCost();
		}
		System.out.println("\nTotal Costs: " + costs);
		System.exit(0);
	}

	/**
	 * STEP 3
	 * If there is at least one item in greater than 50% of the baskets, 
	 * buy the item in the higest percentage of baskets and reduce the 
	 * remaining budget accoringly. (If there is a tie and the total 
	 * value of the tied items is less than the remaining budget buy all 
	 * of them. If the total value of the tied items exceeds the budget 
	 * don't buy anything in this step).
	 * @param items list of items
	 * @param itemsToBuy list of items to buy
	 * @return
	 */
	public static boolean step3selectedHighest(ArrayList<Item> items,
			ArrayList<Item> itemsToBuy, int numVoters) {
		
		boolean existsGT50Item = false;
		/*
		 * Check if one items exists such that it is in greater then 50%
		 * of baskets
		 */
		for (int i = 0; i < items.size() && !existsGT50Item; i++) {
			if (items.get(i).getNumBaskets() > numVoters / 2)
				existsGT50Item = true;
		}
		if (DEBUG) System.out.println("exists over 50% in votes: " 
				+ existsGT50Item);
		if (existsGT50Item) {
			ArrayList<Item> list = new ArrayList<Item>();
			for (Item item : items) {
				if (item.getNumBaskets() > numVoters / 2) {
					list.add(item);
//System.out.println("ADDing " + item.getName() + "\n" + item.getNumBaskets() + ">" + (numVoters / 2));

				}
			}
			if (DEBUG) System.out.println("Items with over 50% acceptance: " + list);
			Collections.sort(list);
			if (DEBUG) System.out.println("Items with over 50% acceptance (SORTED): " + list);
			if (PRINTOUT) {
				System.out.println("Items with over 50% acceptance (Sorted by #Baskets):\n#Baskets\tName");
				for (int i = 0; i < Math.min(list.size(), 9); i++) {
					System.out.println("\t" + list.get(i).getNumBaskets() + "\t" + list.get(i).getName()
							+ " (Cost: " + list.get(i).getCost() + ")");
				}
				if (list.size() >= 9) System.out.println("\t...");
			}
			
			/*
			 * If only 1 item in over 50% or has more baskets than
			 * the next item, add this to list of items to buy, return
			 */
			if (list.size() == 1 || list.get(0).getNumBaskets() 
					> list.get(1).getNumBaskets()) {
				itemsToBuy.add(list.get(0));
				BUDGET -= list.get(0).getCost();
				list.get(0).isEliminated();
				items.remove(list.get(0));
				if (DEBUG) System.out.println("SELECTED: " + list.get(0));
				if (PRINTOUT) System.out.println("Selected for purchase: " + list.get(0).getName()
						+ " (Cost: " + list.get(0).getCost() + ")"
						+ "\nRemaining Budget: " + BUDGET);
				return true;
			} else {
				
				if (PRINTOUT) System.out.println("\nTIES exist...Calculating if they all be purchased...");
				
				double costs = list.get(0).getCost();
				int j = 1;
				/*
				 * Gather all the ties 
				 */
				for (; j < list.size() &&
						list.get(j).getNumBaskets() 
							== list.get(j-1).getNumBaskets(); j++) {
					costs += list.get(j).getCost();
				}
				if (DEBUG) System.out.println("COSTS: " + costs + " vs " + "BUDGET: " + BUDGET);
				/*
				 * If tied and costs is under budget add them all
				*/
				if (costs < BUDGET) {
					if (PRINTOUT) System.out.println("Yes. Ties can be purchased! Selected for purchase: ");
					for (int k = 0; k < j; k ++) {
						if (PRINTOUT) System.out.println("\t" + list.get(k).getName()
								+ " (Cost: " + list.get(k).getCost() + ")");
						itemsToBuy.add(list.get(k));
						BUDGET -= list.get(k).getCost();
						list.get(k).isEliminated();
						items.remove(list.get(k));
					}
					if (PRINTOUT) System.out.println("Remaining Budget: " + BUDGET);
					return true;
				} else 
					if (PRINTOUT)
					System.out.println("Nope. Total cost of tied items is greater than budget: " 
							+ costs + " > " + BUDGET);
				
			}
		}
		if (PRINTOUT) System.out.println("Unable to select item in over 50% of baskets");
		return false;
		
	}

	/**
	 * (Helper method) Resets the number of baskets for this item to 0
	 * @param items list of items in system
	 */
	private static void clearItemBasketCounts(ArrayList<Item> items) {
		for (Item item : items) {
			item.resetNumBaskets();
		}
		
	}

	/**
	 * STEP 2
	 * For each voter create a “basket” of items starting with their 
	 * highest ranked item (that hasn't been eliminated or bought yet) 
	 * and adding the next highest ranked item such that the total value 
	 * of the basket is less than the budget and continue to do this until 
	 * the budget is exhausted or the voter runs out of ranked items.
	 * @param votes list of votes
	 * @param items 
	 */
	public static void step2generateRoundRanked(ArrayList<Voter> votes, ArrayList<Item> items) {
		clearItemBasketCounts(items);
		for (Voter voter : votes)
			voter.generateRoundRankings(BUDGET);
	}

	/**
	 * STEP 1
	 * Elimate all items for which "Do Not Want" received greater 
	 * than 50% of the vote. If the total value of the remaining items 
	 * is less than the budget just buy those items
	 * @param items list of items
	 * @param votes list of votes
	 * @param numVoters 
	 * @param itemsToBuy 
	 */
	public static void step1RemoveItemsGT50DoNotWants(ArrayList<Item> items,
			ArrayList<Voter> votes, int numVoters, ArrayList<Item> itemsToBuy) {
		if (PRINTOUT) System.out.println("Total number of voters: " + votes.size() + "\n#Do Not Wants\t\tItem");
		for (Item item : items) {
			if (PRINTOUT) System.out.println("\t" + item.getNumDoNotWant()
					+ "\t" + ((item.getNumDoNotWant() + 0.0)/votes.size()*100 + "").substring(0,4) + "%"
					+ "\t" + item.getName());
			if (item.getNumDoNotWant() > numVoters/2) {
				if (DEBUG || PRINTOUT) System.out.println("*** Removing " + item.getName()
						+ "\tNumber of voters: " + numVoters + " ***");
				/*
				 * Remove from initial rankings all votes that have
				 * eliminated item
				 */
				item.isEliminated();
				items.remove(item);
				for (Voter voter : votes)
					voter.remove(item.getId());
				
			} 
		}
		
		/*
		 * Check if we can terminate early & all items left can be purchased
		 */
		double costs = 0;
		for (Item item : items) {
			if (!item.isDefinitelyEliminated())
				costs += item.getCost();
		}
		if (costs <= BUDGET) {
			if (PRINTOUT) System.out.println("Remaining items are within the budget!");
			for (Item item : items) { 
				itemsToBuy.add(item);
			}
		}	
	}

	/**
	 * Reads in the votes of each voter from a file that should be ordered
	 * in the same way as the file with the costs
	 * FORMAT:
	 * ITEM_1,v_1,v_2,...v_N where v_i is a rank (e.g., 1 for first place) or
	 * "-1" or "" for "Do Not Want" 
	 * @param voters list of voters
	 * @param items list of items
	 * @param filenameVotes file that contains votes
	 */
	public static void readVotes(ArrayList<Voter> votes,
			ArrayList<Item> items, String filenameVotes) {
		ReadFile file = new ReadFile(filenameVotes);
		String input;
		int numVoters = 0, numIDs = 0;
		while ((input = file.getNext()) != null) {
			String[] parts = input.split(",");
			/*
			 * Make sure the number of voters is consistent
			 */
			if (numVoters != 0 && parts.length != numVoters + 1) {
				System.out.println("The number of voters is not "
						+ "consistent on line " + (numIDs+1));
				System.exit(0);
			/*
			 * Initialize the voters list
			 */
			} else if (numVoters == 0) {
				numVoters = parts.length-1;
				for (int i = 0; i < numVoters; i++)
					votes.add(new Voter());
			}
			/*
			 * Make sure the number of items is consistent
			 */
			if (numIDs >= items.size()) {
				System.out.println("The number of items exceeds the "
						+ "amount that was read in on line " + (numIDs+1));
				System.exit(0);
			}
			/*
			 * Make sure that we're on the right item
			 */
			if (!parts[0].equals(items.get(numIDs).getName())) {
				System.out.println("The items are not" +
						"ordered correctly on line " + (numIDs+1));
				System.exit(0);
			}
			/*
			 * Add rankings for each part
			 */
			for (int i = 1; i < parts.length; i++) {
				int rank = Integer.parseInt(parts[i]);
				if (rank == -1)
					items.get(numIDs).incNumDoNotWant();
				else {
					/* Make sure no duplicate rankings are added */
					if (votes.get(i-1).doesNotHave(rank)) 
					votes.get(i-1).addItem(rank,
						items.get(numIDs));
					else {
						System.out.println("Cannot add ranking " + rank 
								+ " for item " + items.get(numIDs).getName()
								+ " to voter " + COLMAP[i] + "; it already exists:");
						System.out.println(votes.get(i-1).getInitRankingsToString());
						System.exit(0);
					}
				}
			}
			numIDs++;
			
		}
		
	}

	/**
	 * Reads in the costs of each item from a file that should be ordered 
	 * in the same way as the file with vote results
	 * @param items list of items
	 * @param filenameCosts file that contains costs
	 */
	public static void readCosts(ArrayList<Item> items, String filenameCosts) {
		ReadFile file = new ReadFile(filenameCosts);
		String input;
		int ID = 0;
		while ((input = file.getNext()) != null) {
			String[] parts = input.split(",");
			double cost = Double.parseDouble(parts[1]);
			items.add(new Item(ID++, 0, cost, 0, parts[0]));
		}
		file.close();
		
	}

}

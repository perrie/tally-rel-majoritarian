package com.jessica.auto_tally.unit_test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.jessica.auto_tally.Item;
import com.jessica.auto_tally.Tally;
import com.jessica.auto_tally.Voter;

public class TallyTest {

	ArrayList<Voter> votes;
	ArrayList<Item> items;
	String filenameVotes;
	String filenameCosts;
	ArrayList<Item> itemsToBuy;
	
	@Before
	public void setup() {
		votes = new ArrayList<Voter>();
		items = new ArrayList<Item>();
		filenameVotes = "votes.csv";
		filenameCosts = "costs.csv";
		itemsToBuy = new ArrayList<Item>();
	}
	
	/**
	 * Test that costs are read in properly
	 */
	@Test
	public void testReadCosts() {
		// Set up
		Tally.readCosts(items, filenameCosts);
		ArrayList<Item> items2 = new ArrayList<Item>();
		items2.add(new Item(0, 0, 55.0, 0, "Pool"));
		items2.add(new Item(1, 0, 5.0, 0, "TV Remote"));
		items2.add(new Item(2, 0, 45.0, 0, "Pinball Machine"));
		items2.add(new Item(3, 0, 20.0, 0, "Blackboard"));
		items2.add(new Item(4, 0, 50.0, 0, "Sound System"));
		items2.add(new Item(5, 0, 40.0, 0, "Armchair"));
		
		// Test
		assertEquals(items2, items);
		
	}
	
	/**
	 * Test that votes are read in properly
	 */
	@Test
	public void testReadVotes() {
		// Set up
		Tally.readCosts(items, filenameCosts);
		Tally.readVotes(votes, items, filenameVotes);
		ArrayList<Voter> votes2 = new ArrayList<Voter>();
		for (int i=0; i<5; i++)
			votes2.add(new Voter());
		votes2.get(0).addItem(1, items.get(0));
		votes2.get(0).addItem(4, items.get(1));
		votes2.get(0).addItem(3, items.get(2));
		votes2.get(0).addItem(2, items.get(5));
		votes2.get(1).addItem(1, items.get(0));
		votes2.get(1).addItem(3, items.get(1));
		votes2.get(1).addItem(2, items.get(3));
		votes2.get(1).addItem(5, items.get(4));
		votes2.get(1).addItem(4, items.get(5));
		votes2.get(2).addItem(2, items.get(1));
		votes2.get(2).addItem(1, items.get(2));
		votes2.get(2).addItem(4, items.get(3));
		votes2.get(2).addItem(3, items.get(5));
		votes2.get(3).addItem(3, items.get(0));
		votes2.get(3).addItem(4, items.get(2));
		votes2.get(3).addItem(2, items.get(3));
		votes2.get(3).addItem(1, items.get(4));
		votes2.get(4).addItem(4, items.get(0));
		votes2.get(4).addItem(1, items.get(1));
		votes2.get(4).addItem(2, items.get(3));
		votes2.get(4).addItem(3, items.get(5));
		
		// Test
		assertEquals(votes2.get(0), votes.get(0));
	}
	

}

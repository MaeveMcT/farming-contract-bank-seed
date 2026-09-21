package com.farmingcontractbankseed;

import net.runelite.client.plugins.timetracking.farming.CropState;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ContractStatePolicyTest
{
	@Test
	public void doesNotPrioritizeForGrowingFullyGrownOrDiseasedCrop()
	{
		assertFalse(ContractStatePolicy.shouldPrioritizeSeed(CropState.GROWING));
		assertFalse(ContractStatePolicy.shouldPrioritizeSeed(CropState.HARVESTABLE));
		assertFalse(ContractStatePolicy.shouldPrioritizeSeed(CropState.DISEASED));
	}

	@Test
	public void prioritizesForEveryOtherPatchState()
	{
		assertTrue(ContractStatePolicy.shouldPrioritizeSeed(null));
		assertTrue(ContractStatePolicy.shouldPrioritizeSeed(CropState.DEAD));
		assertTrue(ContractStatePolicy.shouldPrioritizeSeed(CropState.EMPTY));
		assertTrue(ContractStatePolicy.shouldPrioritizeSeed(CropState.FILLING));
	}
}

package com.farmingcontractbankseed;

import java.util.List;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.plugins.timetracking.farming.CropState;
import net.runelite.client.plugins.timetracking.farming.FarmingContractManager;
import net.runelite.client.plugins.timetracking.farming.Produce;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FarmingHighlightsTest
{
	@Test
	public void contractAllotmentsHighlightOnlyReadyPatchesAndTheirSeed()
	{
		List<PreplantSeeds.Patch> patches = FarmingHighlights.contractPatches(Produce.WATERMELON, true,
			id -> id == VarbitID.FARMING_TRANSMIT_D ? 52 : 0);
		assertEquals(1, patches.size());
		assertEquals(VarbitID.FARMING_TRANSMIT_C, patches.get(0).varbit);
		assertEquals(FarmingHighlights.CONTRACT_COLOR, patches.get(0).color);
		assertTrue(patches.get(0).matches(ItemID.WATERMELON_SEED));
		assertFalse(patches.get(0).matches(ItemID.ONION_SEED));
		assertEquals(2, FarmingHighlights.contractPatches(Produce.WATERMELON, true, id -> 0).size());
		assertTrue(FarmingHighlights.contractPatches(Produce.WATERMELON, false, id -> 0).isEmpty());
	}

	@Test
	public void contractTreeHighlightsItsSeedAndSaplingOnlyWhileReady()
	{
		List<PreplantSeeds.Patch> patches = FarmingHighlights.contractPatches(Produce.APPLE, true, id -> 14);
		assertEquals(1, patches.size());
		assertEquals(VarbitID.FARMING_TRANSMIT_K, patches.get(0).varbit);
		assertTrue(patches.get(0).matches(ItemID.APPLE_TREE_SEED));
		assertTrue(patches.get(0).matches(ItemID.PLANTPOT_APPLE_SAPLING));
		assertTrue(FarmingHighlights.contractPatches(Produce.APPLE, true, id -> 34).isEmpty());
	}

	@Test
	public void contractTakesPriorityOnSamePatchButSharedSeedsRetainOtherPatchColours()
	{
		FarmingContractBankSeedConfig config = mock(FarmingContractBankSeedConfig.class, CALLS_REAL_METHODS);
		when(config.northAllotment()).thenReturn(PreplantChoice.Allotment.WATERMELON);
		when(config.southAllotment()).thenReturn(PreplantChoice.Allotment.WATERMELON);
		FarmingContractManager manager = mock(FarmingContractManager.class);
		when(manager.hasContract()).thenReturn(true);
		when(manager.getContract()).thenReturn(Produce.WATERMELON);

		List<PreplantSeeds.Patch> patches = FarmingHighlights.readyPatches(config, manager, id -> 0);
		assertEquals(2, patches.size());
		assertEquals(FarmingHighlights.CONTRACT_COLOR, patches.get(0).color);
		assertEquals(FarmingHighlights.CONTRACT_COLOR, patches.get(1).color);

		when(manager.getContract()).thenReturn(Produce.RANARR);
		patches = FarmingHighlights.readyPatches(config, manager, id -> 0);
		assertEquals(3, patches.size());
		assertEquals(VarbitID.FARMING_TRANSMIT_E, patches.get(0).varbit);
		assertEquals(PreplantSeeds.colorFor(VarbitID.FARMING_TRANSMIT_C), patches.get(1).color);

		when(manager.getContractCropState()).thenReturn(CropState.GROWING);
		assertEquals(2, FarmingHighlights.readyPatches(config, manager, id -> 0).size());
		when(config.highlightPreplantPatches()).thenReturn(false);
		assertTrue(FarmingHighlights.readyPatches(config, manager, id -> 0).isEmpty());
		when(manager.getContractCropState()).thenReturn(null);
		assertEquals(1, FarmingHighlights.readyPatches(config, manager, id -> 0).size());
		when(config.highlightContractPatch()).thenReturn(false);
		assertTrue(FarmingHighlights.readyPatches(config, manager, id -> 0).isEmpty());
	}
}

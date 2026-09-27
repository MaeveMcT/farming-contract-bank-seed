package com.farmingcontractbankseed;

import java.util.Arrays;
import java.util.List;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.plugins.timetracking.farming.PatchImplementation;
import net.runelite.client.plugins.timetracking.farming.Produce;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PreplantSeedsTest
{
	@Test
	public void everyOptionIsAContractCropForItsPatchAndHasASeed()
	{
		check(PreplantChoice.Allotment.values(), PatchImplementation.ALLOTMENT);
		check(PreplantChoice.Flower.values(), PatchImplementation.FLOWER);
		check(PreplantChoice.Herb.values(), PatchImplementation.HERB);
		check(PreplantChoice.Bush.values(), PatchImplementation.BUSH);
		check(PreplantChoice.Cactus.values(), PatchImplementation.CACTUS);
		check(PreplantChoice.Tree.values(), PatchImplementation.TREE);
		check(PreplantChoice.FruitTree.values(), PatchImplementation.FRUIT_TREE);
		check(PreplantChoice.Celastrus.values(), PatchImplementation.CELASTRUS);
		check(PreplantChoice.Redwood.values(), PatchImplementation.REDWOOD);
		int optionCount = PreplantChoice.Allotment.values().length + PreplantChoice.Flower.values().length
			+ PreplantChoice.Herb.values().length + PreplantChoice.Bush.values().length
			+ PreplantChoice.Cactus.values().length + PreplantChoice.Tree.values().length
			+ PreplantChoice.FruitTree.values().length + PreplantChoice.Celastrus.values().length
			+ PreplantChoice.Redwood.values().length - 9; // One None entry per patch type.
		assertEquals(ContractSeedCatalog.size(), optionCount);
	}

	@Test
	public void allPatchTypesOnlyAcceptEmptyOrDeadValues()
	{
		checkStates(GuildPatchState.Type.ALLOTMENT, 193, 52);
		checkStates(GuildPatchState.Type.HERB, 170, 4);
		checkStates(GuildPatchState.Type.FLOWER, 201, 8);
		checkStates(GuildPatchState.Type.BUSH, 134, 5);
		checkStates(GuildPatchState.Type.TREE, 137, 8);
		checkStates(GuildPatchState.Type.FRUIT_TREE, 27, 8);
		checkStates(GuildPatchState.Type.CACTUS, 25, 8);
		checkStates(GuildPatchState.Type.CELASTRUS, 23, 8);
		checkStates(GuildPatchState.Type.REDWOOD, 28, 8);
	}

	private static void checkStates(GuildPatchState.Type type, int dead, int growing)
	{
		assertTrue(type.isEmptyOrDead(0));
		assertTrue(type.isEmptyOrDead(dead));
		assertFalse(type.isEmptyOrDead(growing));
		assertFalse(type.isEmptyOrDead(-1));
	}

	private static void check(PreplantChoice[] choices, PatchImplementation type)
	{
		assertEquals("None", choices[0].label());
		for (int i = 1; i < choices.length; i++)
		{
			Produce crop = choices[i].produce();
			assertEquals(type, crop.getPatchImplementation());
			assertNotEquals(-1, ContractSeedCatalog.seedForProduce(crop.getItemID()));
		}
	}

	@Test
	public void defaultsSelectNoPreplantSeeds()
	{
		FarmingContractBankSeedConfig config = mock(FarmingContractBankSeedConfig.class, CALLS_REAL_METHODS);
		assertTrue(PreplantSeeds.itemIds(config, id -> 0).isEmpty());
	}

	@Test
	public void onlyFarmingGuildPatchVarbitsTriggerRefresh()
	{
		assertTrue(PreplantSeeds.isGuildPatchVarbit(VarbitID.FARMING_TRANSMIT_C));
		assertTrue(PreplantSeeds.isGuildPatchVarbit(VarbitID.FARMING_TRANSMIT_D));
		assertTrue(PreplantSeeds.isGuildPatchVarbit(VarbitID.FARMING_TRANSMIT_K));
		assertFalse(PreplantSeeds.isGuildPatchVarbit(VarbitID.BANK_CURRENTTAB));
		assertFalse(PreplantSeeds.isGuildPatchVarbit(VarbitID.FARMING_TRANSMIT_J));
	}

	@Test
	public void northAndSouthAllotmentsHaveIndependentStates()
	{
		FarmingContractBankSeedConfig config = mock(FarmingContractBankSeedConfig.class, CALLS_REAL_METHODS);
		when(config.northAllotment()).thenReturn(PreplantChoice.Allotment.WATERMELON);
		when(config.southAllotment()).thenReturn(PreplantChoice.Allotment.ONION);

		assertEquals(Arrays.asList(ItemID.ONION_SEED), PreplantSeeds.itemIds(config,
			id -> id == VarbitID.FARMING_TRANSMIT_C ? 52 : 0));
		assertEquals(Arrays.asList(ItemID.WATERMELON_SEED), PreplantSeeds.itemIds(config,
			id -> id == VarbitID.FARMING_TRANSMIT_D ? 52 : 0));
		assertTrue(PreplantSeeds.itemIds(config, id -> 52).isEmpty());
	}

	@Test
	public void selectionsIncludeSaplingsAndDeduplicateSameCropAcrossPatches()
	{
		FarmingContractBankSeedConfig config = mock(FarmingContractBankSeedConfig.class, CALLS_REAL_METHODS);
		when(config.northAllotment()).thenReturn(PreplantChoice.Allotment.WATERMELON);
		when(config.southAllotment()).thenReturn(PreplantChoice.Allotment.WATERMELON);
		when(config.fruitTree()).thenReturn(PreplantChoice.FruitTree.DRAGONFRUIT);

		List<Integer> ids = PreplantSeeds.itemIds(config, id -> 0);
		assertEquals(Arrays.asList(ItemID.WATERMELON_SEED, ItemID.DRAGONFRUIT_TREE_SEED,
			ItemID.PLANTPOT_DRAGONFRUIT_SAPLING), ids);
	}
}

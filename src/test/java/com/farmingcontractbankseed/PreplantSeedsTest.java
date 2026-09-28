package com.farmingcontractbankseed;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
		assertTrue(type.isReadyToPreplant(0));
		assertTrue(type.isReadyToPreplant(dead));
		assertFalse(type.isReadyToPreplant(growing));
		assertFalse(type.isReadyToPreplant(-1));
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
	public void checkedHealthPatchesCanBePreplantedButUncheckedOnesCannot()
	{
		assertTrue(GuildPatchState.Type.BUSH.isReadyToPreplant(10));
		assertTrue(GuildPatchState.Type.BUSH.isReadyToPreplant(205));
		assertFalse(GuildPatchState.Type.BUSH.isReadyToPreplant(250));
		assertTrue(GuildPatchState.Type.TREE.isReadyToPreplant(13));
		assertTrue(GuildPatchState.Type.TREE.isReadyToPreplant(192));
		assertFalse(GuildPatchState.Type.TREE.isReadyToPreplant(12));
		assertTrue(GuildPatchState.Type.FRUIT_TREE.isReadyToPreplant(14));
		assertTrue(GuildPatchState.Type.FRUIT_TREE.isReadyToPreplant(252));
		assertFalse(GuildPatchState.Type.FRUIT_TREE.isReadyToPreplant(34));
		assertTrue(GuildPatchState.Type.CACTUS.isReadyToPreplant(15));
		assertTrue(GuildPatchState.Type.CACTUS.isReadyToPreplant(39));
		assertFalse(GuildPatchState.Type.CACTUS.isReadyToPreplant(31));
		assertTrue(GuildPatchState.Type.CELASTRUS.isReadyToPreplant(14));
		assertTrue(GuildPatchState.Type.CELASTRUS.isReadyToPreplant(28));
		assertFalse(GuildPatchState.Type.CELASTRUS.isReadyToPreplant(13));
		assertTrue(GuildPatchState.Type.REDWOOD.isReadyToPreplant(18));
		assertTrue(GuildPatchState.Type.REDWOOD.isReadyToPreplant(55));
		assertFalse(GuildPatchState.Type.REDWOOD.isReadyToPreplant(37));
		assertFalse(GuildPatchState.Type.ALLOTMENT.isReadyToPreplant(10));
		assertFalse(GuildPatchState.Type.HERB.isReadyToPreplant(8));
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
	public void checkedFruitTreeShowsBothSeedAndSapling()
	{
		FarmingContractBankSeedConfig config = mock(FarmingContractBankSeedConfig.class, CALLS_REAL_METHODS);
		when(config.fruitTree()).thenReturn(PreplantChoice.FruitTree.APPLE);

		assertTrue(PreplantSeeds.itemIds(config, id -> id == VarbitID.FARMING_TRANSMIT_K ? 34 : 0).isEmpty());
		assertEquals(Arrays.asList(ItemID.APPLE_TREE_SEED, ItemID.PLANTPOT_APPLE_SAPLING),
			PreplantSeeds.itemIds(config, id -> id == VarbitID.FARMING_TRANSMIT_K ? 14 : 0));
	}

	@Test
	public void eachPatchHasADistinctColourSharedWithItsSeeds()
	{
		FarmingContractBankSeedConfig config = mock(FarmingContractBankSeedConfig.class, CALLS_REAL_METHODS);
		when(config.northAllotment()).thenReturn(PreplantChoice.Allotment.WATERMELON);
		when(config.southAllotment()).thenReturn(PreplantChoice.Allotment.WATERMELON);
		when(config.flower()).thenReturn(PreplantChoice.Flower.WHITE_LILY);
		when(config.herb()).thenReturn(PreplantChoice.Herb.RANARR);
		when(config.bush()).thenReturn(PreplantChoice.Bush.POISON_IVY);
		when(config.cactus()).thenReturn(PreplantChoice.Cactus.POTATO_CACTUS);
		when(config.tree()).thenReturn(PreplantChoice.Tree.OAK);
		when(config.fruitTree()).thenReturn(PreplantChoice.FruitTree.APPLE);
		when(config.celastrus()).thenReturn(PreplantChoice.Celastrus.CELASTRUS);
		when(config.redwood()).thenReturn(PreplantChoice.Redwood.REDWOOD);

		List<PreplantSeeds.Patch> patches = PreplantSeeds.readyPatches(config, id -> 0);
		Set<Color> colors = new HashSet<>();
		for (PreplantSeeds.Patch patch : patches)
		{
			assertTrue(colors.add(patch.color));
			assertEquals(PreplantSeeds.colorFor(patch.varbit), patch.color);
		}
		assertEquals(10, colors.size());
		assertEquals(new Color(0xFF9800), PreplantSeeds.colorFor(VarbitID.FARMING_TRANSMIT_B));
		assertEquals(patches.get(0).seedId, patches.get(1).seedId);
		assertNotEquals(patches.get(0).color, patches.get(1).color);
	}

	@Test
	public void readyPatchesKeepTheirOwnVarbitsAndMatchOnlyTheirSeeds()
	{
		FarmingContractBankSeedConfig config = mock(FarmingContractBankSeedConfig.class, CALLS_REAL_METHODS);
		when(config.northAllotment()).thenReturn(PreplantChoice.Allotment.WATERMELON);
		when(config.southAllotment()).thenReturn(PreplantChoice.Allotment.WATERMELON);
		when(config.fruitTree()).thenReturn(PreplantChoice.FruitTree.APPLE);

		List<PreplantSeeds.Patch> patches = PreplantSeeds.readyPatches(config,
			id -> id == VarbitID.FARMING_TRANSMIT_D ? 52 : 0);
		assertEquals(2, patches.size());
		assertEquals(VarbitID.FARMING_TRANSMIT_C, patches.get(0).varbit);
		assertTrue(patches.get(0).matches(ItemID.WATERMELON_SEED));
		assertFalse(patches.get(0).matches(ItemID.ONION_SEED));
		assertEquals(VarbitID.FARMING_TRANSMIT_K, patches.get(1).varbit);
		assertTrue(patches.get(1).matches(ItemID.PLANTPOT_APPLE_SAPLING));
		assertTrue(patches.get(1).matches(ItemID.APPLE_TREE_SEED));
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

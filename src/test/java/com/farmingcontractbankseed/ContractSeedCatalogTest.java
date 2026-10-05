package com.farmingcontractbankseed;

import net.runelite.api.gameval.ItemID;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ContractSeedCatalogTest
{
	@Test
	public void mapsEachSupportedContractCropToItsSeed()
	{
		assertEquals(51, ContractSeedCatalog.size());
		assertEquals(15, ContractSeedCatalog.saplingCount());
		assertEquals(ItemID.POTATO_SEED, ContractSeedCatalog.seedForProduce(ItemID.POTATO));
		assertEquals(ItemID.WHITE_LILY_SEED, ContractSeedCatalog.seedForProduce(ItemID.WHITELILLY));
		assertEquals(ItemID.WOAD_SEED, ContractSeedCatalog.seedForProduce(ItemID.WOADLEAF));
		assertEquals(ItemID.LIMPWURT_SEED, ContractSeedCatalog.seedForProduce(ItemID.LIMPWURT_ROOT));
		assertEquals(ItemID.TORSTOL_SEED, ContractSeedCatalog.seedForProduce(ItemID.TORSTOL));
		assertEquals(ItemID.ACORN, ContractSeedCatalog.seedForProduce(ItemID.OAK_LOGS));
		assertEquals(ItemID.DRAGONFRUIT_TREE_SEED, ContractSeedCatalog.seedForProduce(ItemID.DRAGONFRUIT));
		assertEquals(ItemID.PLANTPOT_DRAGONFRUIT_SAPLING, ContractSeedCatalog.saplingForProduce(ItemID.DRAGONFRUIT));
		assertEquals(ItemID.CELASTRUS_TREE_SEED, ContractSeedCatalog.seedForProduce(ItemID.BATTLESTAFF));
		assertEquals(ItemID.PLANTPOT_CELASTRUS_TREE_SAPLING, ContractSeedCatalog.saplingForProduce(ItemID.BATTLESTAFF));
		assertEquals(ItemID.REDWOOD_TREE_SEED, ContractSeedCatalog.seedForProduce(ItemID.REDWOOD_LOGS));
		assertEquals(ItemID.PLANTPOT_REDWOOD_TREE_SAPLING, ContractSeedCatalog.saplingForProduce(ItemID.REDWOOD_LOGS));
	}

	@Test
	public void returnsMissingSentinelForNonContractProduce()
	{
		assertEquals(-1, ContractSeedCatalog.seedForProduce(ItemID.COINS));
		assertEquals(-1, ContractSeedCatalog.saplingForProduce(ItemID.POTATO));
	}
}

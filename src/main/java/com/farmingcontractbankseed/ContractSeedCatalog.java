package com.farmingcontractbankseed;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.gameval.ItemID;

final class ContractSeedCatalog
{
	private static final Map<Integer, Integer> SEEDS_BY_PRODUCE = createSeedCatalog();
	private static final Map<Integer, Integer> SAPLINGS_BY_PRODUCE = createSaplingCatalog();

	private ContractSeedCatalog()
	{
	}

	static int seedForProduce(int produceItemId)
	{
		return SEEDS_BY_PRODUCE.getOrDefault(produceItemId, -1);
	}

	static int saplingForProduce(int produceItemId)
	{
		return SAPLINGS_BY_PRODUCE.getOrDefault(produceItemId, -1);
	}

	static int size()
	{
		return SEEDS_BY_PRODUCE.size();
	}

	static int saplingCount()
	{
		return SAPLINGS_BY_PRODUCE.size();
	}

	private static Map<Integer, Integer> createSeedCatalog()
	{
		Map<Integer, Integer> seeds = new HashMap<>();

		// Allotments
		seeds.put(ItemID.POTATO, ItemID.POTATO_SEED);
		seeds.put(ItemID.ONION, ItemID.ONION_SEED);
		seeds.put(ItemID.CABBAGE, ItemID.CABBAGE_SEED);
		seeds.put(ItemID.TOMATO, ItemID.TOMATO_SEED);
		seeds.put(ItemID.SWEETCORN, ItemID.SWEETCORN_SEED);
		seeds.put(ItemID.STRAWBERRY, ItemID.STRAWBERRY_SEED);
		seeds.put(ItemID.WATERMELON, ItemID.WATERMELON_SEED);
		seeds.put(ItemID.SNAPE_GRASS, ItemID.SNAPE_GRASS_SEED);

		// Flowers
		seeds.put(ItemID.MARIGOLD, ItemID.MARIGOLD_SEED);
		seeds.put(ItemID.ROSEMARY, ItemID.ROSEMARY_SEED);
		seeds.put(ItemID.NASTURTIUM, ItemID.NASTURTIUM_SEED);
		seeds.put(ItemID.WHITELILLY, ItemID.WHITE_LILY_SEED);

		// Bushes
		seeds.put(ItemID.REDBERRIES, ItemID.REDBERRY_BUSH_SEED);
		seeds.put(ItemID.CADAVABERRIES, ItemID.CADAVABERRY_BUSH_SEED);
		seeds.put(ItemID.DWELLBERRIES, ItemID.DWELLBERRY_BUSH_SEED);
		seeds.put(ItemID.JANGERBERRIES, ItemID.JANGERBERRY_BUSH_SEED);
		seeds.put(ItemID.WHITE_BERRIES, ItemID.WHITEBERRY_BUSH_SEED);
		seeds.put(ItemID.POISONIVY_BERRIES, ItemID.POISONIVY_BUSH_SEED);

		// Herbs
		seeds.put(ItemID.GUAM_LEAF, ItemID.GUAM_SEED);
		seeds.put(ItemID.MARENTILL, ItemID.MARRENTILL_SEED);
		seeds.put(ItemID.TARROMIN, ItemID.TARROMIN_SEED);
		seeds.put(ItemID.HARRALANDER, ItemID.HARRALANDER_SEED);
		seeds.put(ItemID.RANARR_WEED, ItemID.RANARR_SEED);
		seeds.put(ItemID.TOADFLAX, ItemID.TOADFLAX_SEED);
		seeds.put(ItemID.IRIT_LEAF, ItemID.IRIT_SEED);
		seeds.put(ItemID.AVANTOE, ItemID.AVANTOE_SEED);
		seeds.put(ItemID.KWUARM, ItemID.KWUARM_SEED);
		seeds.put(ItemID.SNAPDRAGON, ItemID.SNAPDRAGON_SEED);
		seeds.put(ItemID.CADANTINE, ItemID.CADANTINE_SEED);
		seeds.put(ItemID.LANTADYME, ItemID.LANTADYME_SEED);
		seeds.put(ItemID.DWARF_WEED, ItemID.DWARF_WEED_SEED);
		seeds.put(ItemID.TORSTOL, ItemID.TORSTOL_SEED);

		// Trees
		seeds.put(ItemID.OAK_LOGS, ItemID.ACORN);
		seeds.put(ItemID.WILLOW_LOGS, ItemID.WILLOW_SEED);
		seeds.put(ItemID.MAPLE_LOGS, ItemID.MAPLE_SEED);
		seeds.put(ItemID.YEW_LOGS, ItemID.YEW_SEED);
		seeds.put(ItemID.MAGIC_LOGS, ItemID.MAGIC_TREE_SEED);

		// Fruit trees
		seeds.put(ItemID.COOKING_APPLE, ItemID.APPLE_TREE_SEED);
		seeds.put(ItemID.BANANA, ItemID.BANANA_TREE_SEED);
		seeds.put(ItemID.ORANGE, ItemID.ORANGE_TREE_SEED);
		seeds.put(ItemID.CURRY_LEAF, ItemID.CURRY_TREE_SEED);
		seeds.put(ItemID.PINEAPPLE, ItemID.PINEAPPLE_TREE_SEED);
		seeds.put(ItemID.PAPAYA, ItemID.PAPAYA_TREE_SEED);
		seeds.put(ItemID.COCONUT, ItemID.PALM_TREE_SEED);
		seeds.put(ItemID.DRAGONFRUIT, ItemID.DRAGONFRUIT_TREE_SEED);

		// Special Farming Guild patches
		seeds.put(ItemID.CACTUS_SPINE, ItemID.CACTUS_SEED);
		seeds.put(ItemID.CACTUS_POTATO, ItemID.POTATO_CACTUS_SEED);
		seeds.put(ItemID.BATTLESTAFF, ItemID.CELASTRUS_TREE_SEED);
		seeds.put(ItemID.REDWOOD_LOGS, ItemID.REDWOOD_TREE_SEED);

		return Collections.unmodifiableMap(seeds);
	}

	private static Map<Integer, Integer> createSaplingCatalog()
	{
		Map<Integer, Integer> saplings = new HashMap<>();

		saplings.put(ItemID.OAK_LOGS, ItemID.PLANTPOT_OAK_SAPLING);
		saplings.put(ItemID.WILLOW_LOGS, ItemID.PLANTPOT_WILLOW_SAPLING);
		saplings.put(ItemID.MAPLE_LOGS, ItemID.PLANTPOT_MAPLE_SAPLING);
		saplings.put(ItemID.YEW_LOGS, ItemID.PLANTPOT_YEW_SAPLING);
		saplings.put(ItemID.MAGIC_LOGS, ItemID.PLANTPOT_MAGIC_TREE_SAPLING);

		saplings.put(ItemID.COOKING_APPLE, ItemID.PLANTPOT_APPLE_SAPLING);
		saplings.put(ItemID.BANANA, ItemID.PLANTPOT_BANANA_SAPLING);
		saplings.put(ItemID.ORANGE, ItemID.PLANTPOT_ORANGE_SAPLING);
		saplings.put(ItemID.CURRY_LEAF, ItemID.PLANTPOT_CURRY_SAPLING);
		saplings.put(ItemID.PINEAPPLE, ItemID.PLANTPOT_PINEAPPLE_SAPLING);
		saplings.put(ItemID.PAPAYA, ItemID.PLANTPOT_PAPAYA_SAPLING);
		saplings.put(ItemID.COCONUT, ItemID.PLANTPOT_PALM_SAPLING);
		saplings.put(ItemID.DRAGONFRUIT, ItemID.PLANTPOT_DRAGONFRUIT_SAPLING);

		saplings.put(ItemID.BATTLESTAFF, ItemID.PLANTPOT_CELASTRUS_TREE_SAPLING);
		saplings.put(ItemID.REDWOOD_LOGS, ItemID.PLANTPOT_REDWOOD_TREE_SAPLING);

		return Collections.unmodifiableMap(saplings);
	}
}

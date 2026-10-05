package com.farmingcontractbankseed;

import net.runelite.client.plugins.timetracking.farming.Produce;

public interface PreplantChoice
{
	default Produce produce()
	{
		String name = ((Enum<?>) this).name();
		return "NONE".equals(name) ? null : Produce.valueOf(name);
	}

	default String label()
	{
		Produce produce = produce();
		return produce == null ? "None" : produce.getName();
	}

	enum Allotment implements PreplantChoice
	{
		NONE, POTATO, ONION, CABBAGE, TOMATO, SWEETCORN, STRAWBERRY, WATERMELON, SNAPE_GRASS;

		@Override public String toString() { return label(); }
	}

	enum Flower implements PreplantChoice
	{
		NONE, MARIGOLD, ROSEMARY, NASTURTIUM, WOAD, LIMPWURT, WHITE_LILY;

		@Override public String toString() { return label(); }
	}

	enum Bush implements PreplantChoice
	{
		NONE, REDBERRIES, CADAVABERRIES, DWELLBERRIES, JANGERBERRIES, WHITEBERRIES, POISON_IVY;

		@Override public String toString() { return label(); }
	}

	enum Herb implements PreplantChoice
	{
		NONE, GUAM, MARRENTILL, TARROMIN, HARRALANDER, RANARR, TOADFLAX, IRIT, AVANTOE, KWUARM,
		SNAPDRAGON, CADANTINE, LANTADYME, DWARF_WEED, TORSTOL;

		@Override public String toString() { return label(); }
	}

	enum Tree implements PreplantChoice
	{
		NONE, OAK, WILLOW, MAPLE, YEW, MAGIC;

		@Override public String toString() { return label(); }
	}

	enum FruitTree implements PreplantChoice
	{
		NONE, APPLE, BANANA, ORANGE, CURRY, PINEAPPLE, PAPAYA, PALM, DRAGONFRUIT;

		@Override public String toString() { return label(); }
	}

	enum Cactus implements PreplantChoice
	{
		NONE, CACTUS, POTATO_CACTUS;

		@Override public String toString() { return label(); }
	}

	enum Celastrus implements PreplantChoice
	{
		NONE, CELASTRUS;

		@Override public String toString() { return label(); }
	}

	enum Redwood implements PreplantChoice
	{
		NONE, REDWOOD;

		@Override public String toString() { return label(); }
	}
}

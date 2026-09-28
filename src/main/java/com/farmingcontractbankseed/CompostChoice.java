package com.farmingcontractbankseed;

import net.runelite.api.gameval.ItemID;

public enum CompostChoice
{
	NONE(),
	COMPOST(ItemID.BUCKET_COMPOST),
	SUPERCOMPOST(ItemID.BUCKET_SUPERCOMPOST),
	ULTRACOMPOST(ItemID.BUCKET_ULTRACOMPOST),
	BOTTOMLESS_COMPOST_BUCKET(ItemID.BOTTOMLESS_COMPOST_BUCKET_FILLED, ItemID.BOTTOMLESS_COMPOST_BUCKET);

	private final int[] itemIds;

	CompostChoice(int... itemIds)
	{
		this.itemIds = itemIds;
	}

	int[] itemIds()
	{
		return itemIds;
	}

	@Override
	public String toString()
	{
		switch (this)
		{
			case SUPERCOMPOST:
				return "Supercompost";
			case ULTRACOMPOST:
				return "Ultracompost";
			case BOTTOMLESS_COMPOST_BUCKET:
				return "Bottomless compost bucket";
			case COMPOST:
				return "Compost";
			default:
				return "None";
		}
	}
}

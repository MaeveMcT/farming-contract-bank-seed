package com.farmingcontractbankseed;

import javax.annotation.Nullable;
import net.runelite.client.plugins.timetracking.farming.CropState;

final class ContractStatePolicy
{
	private ContractStatePolicy()
	{
	}

	static boolean shouldPrioritizeSeed(@Nullable CropState state)
	{
		return state != CropState.GROWING
			&& state != CropState.HARVESTABLE
			&& state != CropState.DISEASED;
	}
}

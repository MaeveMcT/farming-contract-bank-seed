package com.farmingcontractbankseed;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.IntUnaryOperator;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.plugins.timetracking.farming.Produce;

final class PreplantSeeds
{
	private PreplantSeeds()
	{
	}

	static boolean isGuildPatchVarbit(int id)
	{
		switch (id)
		{
			case VarbitID.FARMING_TRANSMIT_B:
			case VarbitID.FARMING_TRANSMIT_C:
			case VarbitID.FARMING_TRANSMIT_D:
			case VarbitID.FARMING_TRANSMIT_E:
			case VarbitID.FARMING_TRANSMIT_F:
			case VarbitID.FARMING_TRANSMIT_G:
			case VarbitID.FARMING_TRANSMIT_H:
			case VarbitID.FARMING_TRANSMIT_I:
			case VarbitID.FARMING_TRANSMIT_K:
			case VarbitID.FARMING_TRANSMIT_L:
				return true;
			default:
				return false;
		}
	}

	static List<Integer> itemIds(FarmingContractBankSeedConfig config, IntUnaryOperator varbits)
	{
		Set<Integer> ids = new LinkedHashSet<>();
		addIfReady(ids, config.northAllotment(), GuildPatchState.Type.ALLOTMENT, VarbitID.FARMING_TRANSMIT_C, varbits);
		addIfReady(ids, config.southAllotment(), GuildPatchState.Type.ALLOTMENT, VarbitID.FARMING_TRANSMIT_D, varbits);
		addIfReady(ids, config.flower(), GuildPatchState.Type.FLOWER, VarbitID.FARMING_TRANSMIT_H, varbits);
		addIfReady(ids, config.herb(), GuildPatchState.Type.HERB, VarbitID.FARMING_TRANSMIT_E, varbits);
		addIfReady(ids, config.bush(), GuildPatchState.Type.BUSH, VarbitID.FARMING_TRANSMIT_B, varbits);
		addIfReady(ids, config.cactus(), GuildPatchState.Type.CACTUS, VarbitID.FARMING_TRANSMIT_F, varbits);
		addIfReady(ids, config.tree(), GuildPatchState.Type.TREE, VarbitID.FARMING_TRANSMIT_G, varbits);
		addIfReady(ids, config.fruitTree(), GuildPatchState.Type.FRUIT_TREE, VarbitID.FARMING_TRANSMIT_K, varbits);
		addIfReady(ids, config.celastrus(), GuildPatchState.Type.CELASTRUS, VarbitID.FARMING_TRANSMIT_L, varbits);
		addIfReady(ids, config.redwood(), GuildPatchState.Type.REDWOOD, VarbitID.FARMING_TRANSMIT_I, varbits);
		return new ArrayList<>(ids);
	}

	private static void addIfReady(Set<Integer> ids, PreplantChoice choice, GuildPatchState.Type type,
		int varbit, IntUnaryOperator varbits)
	{
		Produce crop = choice.produce();
		if (crop == null || !type.isReadyToPreplant(varbits.applyAsInt(varbit)))
		{
			return;
		}
		int produceId = crop.getItemID();
		int seedId = ContractSeedCatalog.seedForProduce(produceId);
		int saplingId = ContractSeedCatalog.saplingForProduce(produceId);
		if (seedId >= 0)
		{
			ids.add(seedId);
		}
		if (saplingId >= 0)
		{
			ids.add(saplingId);
		}
	}
}

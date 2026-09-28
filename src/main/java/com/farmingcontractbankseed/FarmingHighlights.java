package com.farmingcontractbankseed;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.IntUnaryOperator;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.plugins.timetracking.farming.FarmingContractManager;
import net.runelite.client.plugins.timetracking.farming.PatchImplementation;
import net.runelite.client.plugins.timetracking.farming.Produce;

final class FarmingHighlights
{
	static final Color CONTRACT_COLOR = Color.CYAN;

	private FarmingHighlights()
	{
	}

	static List<PreplantSeeds.Patch> readyPatches(FarmingContractBankSeedConfig config,
		FarmingContractManager contractManager, IntUnaryOperator varbits)
	{
		List<PreplantSeeds.Patch> patches = new ArrayList<>();
		if (config.highlightContractPatch() && contractManager.hasContract())
		{
			patches.addAll(contractPatches(contractManager.getContract(),
				ContractStatePolicy.shouldPrioritizeSeed(contractManager.getContractCropState()), varbits));
		}
		if (config.highlightPreplantPatches())
		{
			Set<Integer> contractVarbits = new HashSet<>();
			for (PreplantSeeds.Patch patch : patches)
			{
				contractVarbits.add(patch.varbit);
			}
			for (PreplantSeeds.Patch patch : PreplantSeeds.readyPatches(config, varbits))
			{
				// On a shared patch the contract takes precedence; other patches keep their own colours.
				if (!contractVarbits.contains(patch.varbit))
				{
					patches.add(patch);
				}
			}
		}
		return patches;
	}

	static List<PreplantSeeds.Patch> contractPatches(Produce contract, boolean needsPlanting, IntUnaryOperator varbits)
	{
		List<PreplantSeeds.Patch> patches = new ArrayList<>();
		if (contract == null || !needsPlanting)
		{
			return patches;
		}

		int seedId = ContractSeedCatalog.seedForProduce(contract.getItemID());
		int saplingId = ContractSeedCatalog.saplingForProduce(contract.getItemID());
		if (seedId < 0 && saplingId < 0)
		{
			return patches;
		}

		PatchImplementation type = contract.getPatchImplementation();
		if (type == PatchImplementation.ALLOTMENT)
		{
			addIfReady(patches, VarbitID.FARMING_TRANSMIT_C, GuildPatchState.Type.ALLOTMENT, seedId, saplingId, varbits);
			addIfReady(patches, VarbitID.FARMING_TRANSMIT_D, GuildPatchState.Type.ALLOTMENT, seedId, saplingId, varbits);
		}
		else if (type == PatchImplementation.FLOWER)
		{
			addIfReady(patches, VarbitID.FARMING_TRANSMIT_H, GuildPatchState.Type.FLOWER, seedId, saplingId, varbits);
		}
		else if (type == PatchImplementation.HERB)
		{
			addIfReady(patches, VarbitID.FARMING_TRANSMIT_E, GuildPatchState.Type.HERB, seedId, saplingId, varbits);
		}
		else if (type == PatchImplementation.BUSH)
		{
			addIfReady(patches, VarbitID.FARMING_TRANSMIT_B, GuildPatchState.Type.BUSH, seedId, saplingId, varbits);
		}
		else if (type == PatchImplementation.CACTUS)
		{
			addIfReady(patches, VarbitID.FARMING_TRANSMIT_F, GuildPatchState.Type.CACTUS, seedId, saplingId, varbits);
		}
		else if (type == PatchImplementation.TREE)
		{
			addIfReady(patches, VarbitID.FARMING_TRANSMIT_G, GuildPatchState.Type.TREE, seedId, saplingId, varbits);
		}
		else if (type == PatchImplementation.FRUIT_TREE)
		{
			addIfReady(patches, VarbitID.FARMING_TRANSMIT_K, GuildPatchState.Type.FRUIT_TREE, seedId, saplingId, varbits);
		}
		else if (type == PatchImplementation.CELASTRUS)
		{
			addIfReady(patches, VarbitID.FARMING_TRANSMIT_L, GuildPatchState.Type.CELASTRUS, seedId, saplingId, varbits);
		}
		else if (type == PatchImplementation.REDWOOD)
		{
			addIfReady(patches, VarbitID.FARMING_TRANSMIT_I, GuildPatchState.Type.REDWOOD, seedId, saplingId, varbits);
		}
		return patches;
	}

	private static void addIfReady(List<PreplantSeeds.Patch> patches, int varbit, GuildPatchState.Type type,
		int seedId, int saplingId, IntUnaryOperator varbits)
	{
		if (type.isReadyToPreplant(varbits.applyAsInt(varbit)))
		{
			patches.add(new PreplantSeeds.Patch(varbit, CONTRACT_COLOR, seedId, saplingId));
		}
	}
}

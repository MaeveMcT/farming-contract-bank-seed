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
		if (config.highlightContractPatch() && contractManager != null && contractManager.hasContract())
		{
			patches.addAll(contractPatches(contractManager.getContract(),
				ContractStatePolicy.shouldPrioritizeSeed(contractManager.getContractCropState())));
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

	static List<PreplantSeeds.Patch> contractPatches(Produce contract, boolean shouldHighlight)
	{
		List<PreplantSeeds.Patch> patches = new ArrayList<>();
		if (contract == null || !shouldHighlight)
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
			addPatch(patches, VarbitID.FARMING_TRANSMIT_C, seedId, saplingId);
			addPatch(patches, VarbitID.FARMING_TRANSMIT_D, seedId, saplingId);
		}
		else if (type == PatchImplementation.FLOWER)
		{
			addPatch(patches, VarbitID.FARMING_TRANSMIT_H, seedId, saplingId);
		}
		else if (type == PatchImplementation.HERB)
		{
			addPatch(patches, VarbitID.FARMING_TRANSMIT_E, seedId, saplingId);
		}
		else if (type == PatchImplementation.BUSH)
		{
			addPatch(patches, VarbitID.FARMING_TRANSMIT_B, seedId, saplingId);
		}
		else if (type == PatchImplementation.CACTUS)
		{
			addPatch(patches, VarbitID.FARMING_TRANSMIT_F, seedId, saplingId);
		}
		else if (type == PatchImplementation.TREE)
		{
			addPatch(patches, VarbitID.FARMING_TRANSMIT_G, seedId, saplingId);
		}
		else if (type == PatchImplementation.FRUIT_TREE)
		{
			addPatch(patches, VarbitID.FARMING_TRANSMIT_K, seedId, saplingId);
		}
		else if (type == PatchImplementation.CELASTRUS)
		{
			addPatch(patches, VarbitID.FARMING_TRANSMIT_L, seedId, saplingId);
		}
		else if (type == PatchImplementation.REDWOOD)
		{
			addPatch(patches, VarbitID.FARMING_TRANSMIT_I, seedId, saplingId);
		}
		return patches;
	}

	private static void addPatch(List<PreplantSeeds.Patch> patches, int varbit, int seedId, int saplingId)
	{
		patches.add(new PreplantSeeds.Patch(varbit, CONTRACT_COLOR, seedId, saplingId));
	}
}

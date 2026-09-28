package com.farmingcontractbankseed;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.IntUnaryOperator;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.plugins.timetracking.farming.Produce;

final class PreplantSeeds
{
	static final class Patch
	{
		final int varbit;
		final Color color;
		final int seedId;
		final int saplingId;

		private Patch(int varbit, int seedId, int saplingId)
		{
			this(varbit, colorFor(varbit), seedId, saplingId);
		}

		Patch(int varbit, Color color, int seedId, int saplingId)
		{
			this.varbit = varbit;
			this.color = color;
			this.seedId = seedId;
			this.saplingId = saplingId;
		}

		boolean matches(int itemId)
		{
			return itemId == seedId || itemId == saplingId && saplingId >= 0;
		}
	}

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

	static Color colorFor(int varbit)
	{
		switch (varbit)
		{
			case VarbitID.FARMING_TRANSMIT_C: return new Color(0x4FC3F7); // North allotment: blue
			case VarbitID.FARMING_TRANSMIT_D: return new Color(0xB39DDB); // South allotment: purple
			case VarbitID.FARMING_TRANSMIT_H: return new Color(0xF06292); // Flower: pink
			case VarbitID.FARMING_TRANSMIT_E: return new Color(0x9CCC65); // Herb: green
			case VarbitID.FARMING_TRANSMIT_B: return new Color(0xFF9800); // Bush: orange
			case VarbitID.FARMING_TRANSMIT_F: return new Color(0xFDD835); // Cactus: yellow
			case VarbitID.FARMING_TRANSMIT_G: return new Color(0x26A69A); // Tree: teal
			case VarbitID.FARMING_TRANSMIT_K: return new Color(0xEF5350); // Fruit tree: red
			case VarbitID.FARMING_TRANSMIT_L: return new Color(0x7E57C2); // Celastrus: violet
			case VarbitID.FARMING_TRANSMIT_I: return new Color(0xEEEEEE); // Redwood: white
			default: throw new IllegalArgumentException("Not a preplant patch varbit: " + varbit);
		}
	}

	static List<Integer> itemIds(FarmingContractBankSeedConfig config, IntUnaryOperator varbits)
	{
		Set<Integer> ids = new LinkedHashSet<>();
		for (Patch patch : readyPatches(config, varbits))
		{
			if (patch.seedId >= 0)
			{
				ids.add(patch.seedId);
			}
			if (patch.saplingId >= 0)
			{
				ids.add(patch.saplingId);
			}
		}
		return new ArrayList<>(ids);
	}

	static List<Patch> readyPatches(FarmingContractBankSeedConfig config, IntUnaryOperator varbits)
	{
		List<Patch> patches = new ArrayList<>();
		addIfReady(patches, config.northAllotment(), GuildPatchState.Type.ALLOTMENT, VarbitID.FARMING_TRANSMIT_C, varbits);
		addIfReady(patches, config.southAllotment(), GuildPatchState.Type.ALLOTMENT, VarbitID.FARMING_TRANSMIT_D, varbits);
		addIfReady(patches, config.flower(), GuildPatchState.Type.FLOWER, VarbitID.FARMING_TRANSMIT_H, varbits);
		addIfReady(patches, config.herb(), GuildPatchState.Type.HERB, VarbitID.FARMING_TRANSMIT_E, varbits);
		addIfReady(patches, config.bush(), GuildPatchState.Type.BUSH, VarbitID.FARMING_TRANSMIT_B, varbits);
		addIfReady(patches, config.cactus(), GuildPatchState.Type.CACTUS, VarbitID.FARMING_TRANSMIT_F, varbits);
		addIfReady(patches, config.tree(), GuildPatchState.Type.TREE, VarbitID.FARMING_TRANSMIT_G, varbits);
		addIfReady(patches, config.fruitTree(), GuildPatchState.Type.FRUIT_TREE, VarbitID.FARMING_TRANSMIT_K, varbits);
		addIfReady(patches, config.celastrus(), GuildPatchState.Type.CELASTRUS, VarbitID.FARMING_TRANSMIT_L, varbits);
		addIfReady(patches, config.redwood(), GuildPatchState.Type.REDWOOD, VarbitID.FARMING_TRANSMIT_I, varbits);
		return patches;
	}

	private static void addIfReady(List<Patch> patches, PreplantChoice choice, GuildPatchState.Type type,
		int varbit, IntUnaryOperator varbits)
	{
		Produce crop = choice.produce();
		if (crop == null || !type.isReadyToPreplant(varbits.applyAsInt(varbit)))
		{
			return;
		}
		int produceId = crop.getItemID();
		patches.add(new Patch(varbit, ContractSeedCatalog.seedForProduce(produceId),
			ContractSeedCatalog.saplingForProduce(produceId)));
	}
}

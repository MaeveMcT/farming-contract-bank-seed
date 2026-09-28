package com.farmingcontractbankseed;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.timetracking.farming.FarmingContractManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

class PreplantInventoryOverlay extends WidgetItemOverlay
{
	private final Client client;
	private final FarmingContractBankSeedConfig config;
	private final FarmingContractBankSeedPlugin plugin;
	private final FarmingContractManager contractManager;
	private final ItemManager itemManager;

	@Inject
	PreplantInventoryOverlay(Client client, FarmingContractBankSeedConfig config,
		FarmingContractBankSeedPlugin plugin, FarmingContractManager contractManager, ItemManager itemManager)
	{
		this.client = client;
		this.config = config;
		this.plugin = plugin;
		this.contractManager = contractManager;
		this.itemManager = itemManager;
		showOnInterfaces(InterfaceID.INVENTORY, InterfaceID.BANKSIDE);
	}

	@Override
	public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem)
	{
		if ((!config.highlightPreplantPatches() && !config.highlightContractPatch()) || !plugin.isInFarmingGuild())
		{
			return;
		}
		List<PreplantSeeds.Patch> matches = new ArrayList<>();
		for (PreplantSeeds.Patch patch : FarmingHighlights.readyPatches(config, contractManager, client::getVarbitValue))
		{
			if (patch.matches(itemId))
			{
				matches.add(patch);
			}
		}
		if (matches.isEmpty())
		{
			return;
		}

		Rectangle bounds = widgetItem.getCanvasBounds();
		for (int i = 0; i < matches.size(); i++)
		{
			// Shared contract and preplant seeds may need multiple patches. Show every matching colour.
			Graphics2D slice = (Graphics2D) graphics.create();
			try
			{
				if (matches.size() > 1)
				{
					int left = bounds.x + i * bounds.width / matches.size();
					int right = bounds.x + (i + 1) * bounds.width / matches.size();
					slice.clipRect(left, bounds.y, right - left, bounds.height);
				}
				slice.drawImage(itemManager.getItemOutline(itemId, widgetItem.getQuantity(),
					matches.get(i).color), bounds.x, bounds.y, null);
			}
			finally
			{
				slice.dispose();
			}
		}
	}
}

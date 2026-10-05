package com.farmingcontractbankseed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.ItemContainer;
import net.runelite.api.Player;
import net.runelite.api.ScriptID;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.events.ConfigChanged;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.timetracking.TimeTrackingPlugin;
import net.runelite.client.plugins.timetracking.farming.FarmingContractManager;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "Farming Contract Bank Seed",
	description = "Shows banked Farming Guild contract and preplant seeds and saplings",
	tags = {"farming", "contracts", "bank", "seeds", "saplings"}
)
public class FarmingContractBankSeedPlugin extends Plugin
{
	private static final Set<Integer> FARMING_GUILD_REGIONS = new HashSet<>(Arrays.asList(
		4665, 4666, 4667,
		4921, 4922, 4923,
		5177, 5178, 5179
	));

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private PluginManager pluginManager;

	private FarmingContractManager contractManager;

	@Inject
	private FarmingContractBankSeedConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private PreplantPatchOverlay patchOverlay;

	@Inject
	private PreplantInventoryOverlay inventoryOverlay;

	@Provides
	FarmingContractBankSeedConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(FarmingContractBankSeedConfig.class);
	}

	private final List<WidgetPosition> shiftedWidgets = new ArrayList<>();
	private Widget modifiedContainer;
	private int originalChildrenCount = -1;
	private int originalScrollHeight;
	private boolean bankViewFiltered;
	private boolean running;

	@Override
	protected void startUp()
	{
		// Time Tracking does not expose a public Guice module in RuneLite 1.13.
		// Use its existing manager, rather than constructing a second, unsynchronised one.
		contractManager = pluginManager.getPlugins().stream()
			.filter(TimeTrackingPlugin.class::isInstance)
			.map(plugin -> plugin.getInjector().getInstance(FarmingContractManager.class))
			.findFirst().orElse(null);
		running = true;
		overlayManager.add(patchOverlay);
		overlayManager.add(inventoryOverlay);
	}

	@Override
	protected void shutDown()
	{
		running = false;
		overlayManager.remove(patchOverlay);
		overlayManager.remove(inventoryOverlay);
		patchOverlay.clear();
		contractManager = null;
		restoreLayout();
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		patchOverlay.refresh();
	}

	@Subscribe
	public void onScriptPreFired(ScriptPreFired event)
	{
		if (event.getScriptId() == ScriptID.BANKMAIN_BUILD)
		{
			// Restore before the game lays out and hides items for the selected tab.
			restoreLayout();
		}
	}

	@Subscribe(priority = -100)
	public void onScriptPostFired(ScriptPostFired event)
	{
		if (event.getScriptId() == ScriptID.BANKMAIN_SEARCHING)
		{
			// Observe the final script result after Bank Tags, Quest Helper, and other
			// custom views have had an opportunity to identify themselves as searches.
			int stackSize = client.getIntStackSize();
			bankViewFiltered = stackSize > 0 && client.getIntStack()[stackSize - 1] != 0;
		}
		else if (event.getScriptId() == ScriptID.BANKMAIN_FINISHBUILDING)
		{
			clientThread.invokeAtTickEnd(this::showBankSections);
		}
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		if (running && PreplantSeeds.isGuildPatchVarbit(event.getVarbitId())
			&& client.getWidget(InterfaceID.Bankmain.ITEMS) != null
			&& client.getItemContainer(InventoryID.BANK) != null)
		{
			clientThread.invokeAtTickEnd(this::showBankSections);
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (FarmingContractBankSeedConfig.GROUP.equals(event.getGroup()))
		{
			patchOverlay.clear();
			clientThread.invokeAtTickEnd(this::showBankSections);
		}
	}

	private void showBankSections()
	{
		if (!running || bankViewFiltered)
		{
			return;
		}

		restoreLayout();

		if (!isInFarmingGuild())
		{
			return;
		}

		Widget itemContainer = client.getWidget(InterfaceID.Bankmain.ITEMS);
		ItemContainer bank = client.getItemContainer(InventoryID.BANK);
		if (itemContainer == null || bank == null || itemContainer.getChildren() == null)
		{
			return;
		}

		Widget[] originalChildren = itemContainer.getChildren();
		if (!BankSectionLayout.hasStandardBankItem(originalChildren))
		{
			return;
		}

		// Capture original children before creating display-only missing-item widgets.
		modifiedContainer = itemContainer;
		originalChildrenCount = originalChildren.length;
		originalScrollHeight = itemContainer.getScrollHeight();

		List<Integer> contractIds = new ArrayList<>(2);
		boolean hasContractToPlant = contractManager != null && contractManager.hasContract()
			&& ContractStatePolicy.shouldPrioritizeSeed(contractManager.getContractCropState());
		if (hasContractToPlant)
		{
			int produceItemId = contractManager.getContract().getItemID();
			addIfValid(contractIds, ContractSeedCatalog.seedForProduce(produceItemId));
			addIfValid(contractIds, ContractSeedCatalog.saplingForProduce(produceItemId));
		}
		List<Widget> contractWidgets = ContractSectionWidgets.findOrCreateMissing(itemContainer, originalChildren, contractIds, bank, client);

		List<Integer> preplantIds = PreplantSeeds.itemIds(config, client::getVarbitValue);
		boolean hasReadyPreplant = !preplantIds.isEmpty();
		preplantIds.removeAll(contractIds); // The contract section owns shared seeds and saplings.
		List<Widget> preplantWidgets = ContractSectionWidgets.findOrCreateMissing(itemContainer, originalChildren, preplantIds, bank, client);
		if (hasContractToPlant || hasReadyPreplant)
		{
			for (int compostId : config.compost().itemIds())
			{
				if (bank.find(compostId) < 0)
				{
					continue;
				}
				List<Widget> compostWidgets = ContractSectionWidgets.findExisting(originalChildren, Arrays.asList(compostId));
				if (!compostWidgets.isEmpty())
				{
					(!contractWidgets.isEmpty() ? contractWidgets : preplantWidgets).addAll(compostWidgets);
					break;
				}
			}
		}
		if (contractWidgets.isEmpty() && preplantWidgets.isEmpty())
		{
			restoreLayout();
			return;
		}

		int contractHeight = contractWidgets.isEmpty() ? 0 : BankSectionLayout.sectionHeight(contractWidgets.size());
		int preplantHeight = preplantWidgets.isEmpty() ? 0 : BankSectionLayout.sectionHeight(preplantWidgets.size());
		int totalHeight = contractHeight + preplantHeight;

		for (Widget child : originalChildren)
		{
			shiftedWidgets.add(new WidgetPosition(child));
			child.setOriginalY(child.getOriginalY() + totalHeight);
			child.revalidate();
		}

		if (!contractWidgets.isEmpty())
		{
			BankSectionLayout.placeItems(contractWidgets, 0);
			BankSectionLayout.createHeader(itemContainer, "Farming contract", 0, contractWidgets.size());
		}
		if (!preplantWidgets.isEmpty())
		{
			BankSectionLayout.placeItems(preplantWidgets, contractHeight);
			BankSectionLayout.createHeader(itemContainer, "Preplant seeds/saplings", contractHeight, preplantWidgets.size());
		}

		itemContainer.setScrollHeight(originalScrollHeight + totalHeight);
		itemContainer.revalidate();
	}

	private static void addIfValid(List<Integer> items, int itemId)
	{
		if (itemId >= 0)
		{
			items.add(itemId);
		}
	}

	FarmingContractManager getContractManager()
	{
		return contractManager;
	}

	boolean isInFarmingGuild()
	{
		Player player = client.getLocalPlayer();
		return player != null && FARMING_GUILD_REGIONS.contains(player.getWorldLocation().getRegionID());
	}

	private void restoreLayout()
	{
		for (WidgetPosition position : shiftedWidgets)
		{
			position.widget.setOriginalX(position.x);
			position.widget.setOriginalY(position.y);
			position.widget.revalidate();
		}
		shiftedWidgets.clear();

		if (modifiedContainer != null)
		{
			Widget[] children = modifiedContainer.getChildren();
			if (children != null && originalChildrenCount >= 0 && children.length >= originalChildrenCount)
			{
				modifiedContainer.setChildren(Arrays.copyOf(children, originalChildrenCount));
			}
			modifiedContainer.setScrollHeight(originalScrollHeight);
			modifiedContainer.revalidate();
		}

		modifiedContainer = null;
		originalChildrenCount = -1;
	}

	private static final class WidgetPosition
	{
		private final Widget widget;
		private final int x;
		private final int y;
		private WidgetPosition(Widget widget)
		{
			this.widget = widget;
			this.x = widget.getOriginalX();
			this.y = widget.getOriginalY();
		}
	}
}

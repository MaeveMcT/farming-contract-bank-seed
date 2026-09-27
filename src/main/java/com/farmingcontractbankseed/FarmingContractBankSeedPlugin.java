package com.farmingcontractbankseed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.ItemContainer;
import net.runelite.api.Player;
import net.runelite.api.ScriptID;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.timetracking.TimeTrackingPlugin;
import net.runelite.client.plugins.timetracking.farming.FarmingContractManager;

@PluginDescriptor(
	name = "Farming Contract Bank Seed",
	description = "Shows banked seeds and saplings for an unplanted Farming Guild contract",
	tags = {"farming", "contracts", "bank", "seeds", "saplings"}
)
@PluginDependency(TimeTrackingPlugin.class)
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
	private FarmingContractManager contractManager;

	private final List<WidgetPosition> shiftedWidgets = new ArrayList<>();
	private Widget modifiedContainer;
	private int originalChildrenCount = -1;
	private int originalScrollHeight;
	private boolean bankViewFiltered;
	private boolean running;

	@Override
	protected void startUp()
	{
		running = true;
	}

	@Override
	protected void shutDown()
	{
		running = false;
		restoreLayout();
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
			clientThread.invokeAtTickEnd(this::showContractSection);
		}
	}

	private void showContractSection()
	{
		if (!running || bankViewFiltered)
		{
			return;
		}

		restoreLayout();

		if (!isInFarmingGuild()
			|| !contractManager.hasContract()
			|| !ContractStatePolicy.shouldPrioritizeSeed(contractManager.getContractCropState()))
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

		int produceItemId = contractManager.getContract().getItemID();
		List<Integer> contractItems = new ArrayList<>(2);
		addIfBanked(contractItems, bank, ContractSeedCatalog.seedForProduce(produceItemId));
		addIfBanked(contractItems, bank, ContractSeedCatalog.saplingForProduce(produceItemId));
		if (contractItems.isEmpty())
		{
			return;
		}

		List<Widget> sectionItems = ContractSectionWidgets.findExisting(originalChildren, contractItems);
		if (sectionItems.isEmpty())
		{
			return;
		}

		modifiedContainer = itemContainer;
		originalChildrenCount = originalChildren.length;
		originalScrollHeight = itemContainer.getScrollHeight();

		for (Widget child : originalChildren)
		{
			shiftedWidgets.add(new WidgetPosition(child));
			child.setOriginalY(child.getOriginalY() + BankSectionLayout.SECTION_HEIGHT);
			child.revalidate();
		}

		BankSectionLayout.placeItems(sectionItems);
		BankSectionLayout.createHeader(itemContainer, "Farming contract");

		itemContainer.setScrollHeight(originalScrollHeight + BankSectionLayout.SECTION_HEIGHT);
		itemContainer.revalidate();
	}

	private static void addIfBanked(List<Integer> items, ItemContainer bank, int itemId)
	{
		if (itemId >= 0 && bank.find(itemId) >= 0)
		{
			items.add(itemId);
		}
	}

	private boolean isInFarmingGuild()
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

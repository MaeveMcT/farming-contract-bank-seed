package com.farmingcontractbankseed;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.FontID;
import net.runelite.api.ItemContainer;
import net.runelite.api.Player;
import net.runelite.api.ScriptID;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.SpriteID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetType;
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
	private static final int BANK_ITEM_WIDTH = 36;
	private static final int BANK_ITEM_HEIGHT = 32;
	private static final int ITEM_HORIZONTAL_SPACING = 48;
	private static final int ITEM_ROW_START = 51;
	private static final int SECTION_HEADER_HEIGHT = 20;
	private static final int SECTION_HEIGHT = SECTION_HEADER_HEIGHT + 36;
	private static final int SECTION_WIDTH = 8 * ITEM_HORIZONTAL_SPACING;
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
		if (event.getScriptId() == ScriptID.BANKMAIN_FINISHBUILDING)
		{
			restoreLayout();
		}
	}

	@Subscribe
	public void onScriptPostFired(ScriptPostFired event)
	{
		if (event.getScriptId() == ScriptID.BANKMAIN_FINISHBUILDING)
		{
			clientThread.invokeAtTickEnd(this::showContractSection);
		}
	}

	private void showContractSection()
	{
		if (!running)
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
		Widget interactionTemplate = findStandardBankItem(originalChildren);
		if (interactionTemplate == null)
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

		modifiedContainer = itemContainer;
		originalChildrenCount = originalChildren.length;
		originalScrollHeight = itemContainer.getScrollHeight();

		List<Widget> sectionItems = ContractSectionWidgets.findExisting(originalChildren, contractItems);
		if (sectionItems.isEmpty())
		{
			return;
		}

		for (Widget child : originalChildren)
		{
			shiftedWidgets.add(new WidgetPosition(child));
			child.setOriginalY(child.getOriginalY() + SECTION_HEIGHT);
			child.revalidate();
		}

		for (int i = 0; i < sectionItems.size(); i++)
		{
			Widget item = sectionItems.get(i);
			item.setHidden(false);
			item.setOriginalX(ITEM_ROW_START + i * ITEM_HORIZONTAL_SPACING);
			item.setOriginalY(SECTION_HEADER_HEIGHT);
			item.revalidate();
		}
		createSectionHeader(itemContainer);

		itemContainer.setScrollHeight(originalScrollHeight + SECTION_HEIGHT);
		itemContainer.revalidate();
	}

	private static Widget findStandardBankItem(Widget[] children)
	{
		for (Widget child : children)
		{
			if (!child.isHidden()
				&& child.getItemId() >= 0
				&& child.getOriginalWidth() == BANK_ITEM_WIDTH
				&& child.getOriginalHeight() == BANK_ITEM_HEIGHT)
			{
				return child;
			}
		}
		return null;
	}

	private static void addIfBanked(List<Integer> items, ItemContainer bank, int itemId)
	{
		if (itemId >= 0 && bank.find(itemId) >= 0)
		{
			items.add(itemId);
		}
	}

	private void createSectionHeader(Widget container)
	{
		Widget divider = container.createChild(-1, WidgetType.GRAPHIC);
		divider.setOriginalWidth(SECTION_WIDTH);
		divider.setOriginalHeight(2);
		divider.setOriginalX(ITEM_ROW_START);
		divider.setOriginalY(SECTION_HEIGHT - 2);
		divider.setSpriteId(SpriteID.TRADEBACKING_DARK);
		divider.revalidate();

		Widget title = container.createChild(-1, WidgetType.TEXT);
		title.setOriginalWidth(SECTION_WIDTH);
		title.setOriginalHeight(15);
		title.setOriginalX(ITEM_ROW_START);
		title.setOriginalY(5);
		title.setText("Farming contract");
		title.setFontId(FontID.PLAIN_11);
		title.setTextColor(new Color(228, 216, 162).getRGB());
		title.setTextShadowed(true);
		title.revalidate();
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
			position.widget.setHidden(position.hidden);
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
		private final boolean hidden;

		private WidgetPosition(Widget widget)
		{
			this.widget = widget;
			this.x = widget.getOriginalX();
			this.y = widget.getOriginalY();
			this.hidden = widget.isSelfHidden();
		}
	}
}

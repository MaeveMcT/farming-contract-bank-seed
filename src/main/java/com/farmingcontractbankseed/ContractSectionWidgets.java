package com.farmingcontractbankseed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.ItemContainer;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.ItemQuantityMode;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.plugins.banktags.BankTagsPlugin;

final class ContractSectionWidgets
{
	private ContractSectionWidgets()
	{
	}

	static List<Widget> findOrCreateMissing(Widget container, Widget[] bankWidgets,
		List<Integer> itemIds, ItemContainer bank, Client client)
	{
		List<Widget> result = new ArrayList<>(itemIds.size());
		for (int itemId : itemIds)
		{
			List<Widget> existing = findExisting(bankWidgets, Collections.singletonList(itemId));
			if (!existing.isEmpty())
			{
				result.addAll(existing);
			}
			else if (bank.count(itemId) == 0)
			{
				// No real bank slot: only a local information action, never withdrawal.
				Widget missing = container.createChild(-1, WidgetType.GRAPHIC);
				missing.setOriginalWidth(BankTagsPlugin.BANK_ITEM_WIDTH);
				missing.setOriginalHeight(BankTagsPlugin.BANK_ITEM_HEIGHT);
				missing.setItemId(itemId);
				missing.setItemQuantity(0);
				missing.setItemQuantityMode(ItemQuantityMode.ALWAYS);
				missing.setOpacity(120);
				String itemName = client.getItemDefinition(itemId).getName();
				missing.setName("<col=ff9040>" + itemName + "</col>");
				missing.setAction(0, "Check");
				missing.setHasListener(true);
				missing.setOnOpListener((JavaScriptCallback) event ->
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", itemName + ": 0 in bank.", null));
				result.add(missing);
			}
		}
		return result;
	}

	static List<Widget> findExisting(Widget[] bankWidgets, List<Integer> itemIds)
	{
		List<Widget> result = new ArrayList<>(itemIds.size());
		for (int itemId : itemIds)
		{
			for (Widget bankWidget : bankWidgets)
			{
				if (!bankWidget.isHidden() && bankWidget.getItemId() == itemId)
				{
					result.add(bankWidget);
					break;
				}
			}
		}
		return result;
	}
}

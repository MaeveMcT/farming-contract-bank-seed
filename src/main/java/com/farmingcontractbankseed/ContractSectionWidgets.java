package com.farmingcontractbankseed;

import java.util.ArrayList;
import java.util.List;
import net.runelite.api.widgets.Widget;

final class ContractSectionWidgets
{
	private ContractSectionWidgets()
	{
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

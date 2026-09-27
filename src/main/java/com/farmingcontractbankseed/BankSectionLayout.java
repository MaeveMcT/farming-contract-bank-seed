package com.farmingcontractbankseed;

import java.awt.Color;
import java.util.List;
import net.runelite.api.FontID;
import net.runelite.api.gameval.SpriteID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.plugins.banktags.BankTagsPlugin;

/**
 * Shared geometry and widget creation for a titled section in the bank item grid.
 * RuneLite does not currently expose a composable bank-section API.
 */
final class BankSectionLayout
{
	private static final int ITEM_HORIZONTAL_SPACING =
		BankTagsPlugin.BANK_ITEM_WIDTH + BankTagsPlugin.BANK_ITEM_X_PADDING;
	private static final int ITEM_VERTICAL_SPACING =
		BankTagsPlugin.BANK_ITEM_HEIGHT + BankTagsPlugin.BANK_ITEM_Y_PADDING;
	private static final int HEADER_HEIGHT = 20;
	private static final int SECTION_WIDTH = BankTagsPlugin.BANK_ITEMS_PER_ROW * ITEM_HORIZONTAL_SPACING;
	private static final int SECTION_CONTENT_HEIGHT = HEADER_HEIGHT + ITEM_VERTICAL_SPACING;
	private static final int BANK_ITEMS_GAP = 12;

	static final int SECTION_HEIGHT = SECTION_CONTENT_HEIGHT + BANK_ITEMS_GAP;

	private BankSectionLayout()
	{
	}

	static boolean hasStandardBankItem(Widget[] children)
	{
		for (Widget child : children)
		{
			if (!child.isHidden()
				&& child.getItemId() >= 0
				&& child.getOriginalWidth() == BankTagsPlugin.BANK_ITEM_WIDTH
				&& child.getOriginalHeight() == BankTagsPlugin.BANK_ITEM_HEIGHT)
			{
				return true;
			}
		}
		return false;
	}

	static void placeItems(List<Widget> items)
	{
		for (int i = 0; i < items.size(); i++)
		{
			Widget item = items.get(i);
			item.setOriginalX(BankTagsPlugin.BANK_ITEM_START_X + i * ITEM_HORIZONTAL_SPACING);
			item.setOriginalY(HEADER_HEIGHT);
			item.revalidate();
		}
	}

	static void createHeader(Widget container, String text)
	{
		Widget divider = container.createChild(-1, WidgetType.GRAPHIC);
		divider.setOriginalWidth(SECTION_WIDTH);
		divider.setOriginalHeight(2);
		divider.setOriginalX(BankTagsPlugin.BANK_ITEM_START_X);
		divider.setOriginalY(SECTION_CONTENT_HEIGHT - 2);
		divider.setSpriteId(SpriteID.TRADEBACKING_DARK);
		divider.revalidate();

		Widget title = container.createChild(-1, WidgetType.TEXT);
		title.setOriginalWidth(SECTION_WIDTH);
		title.setOriginalHeight(15);
		title.setOriginalX(BankTagsPlugin.BANK_ITEM_START_X);
		title.setOriginalY(5);
		title.setText(text);
		title.setFontId(FontID.PLAIN_11);
		title.setTextColor(new Color(228, 216, 162).getRGB());
		title.setTextShadowed(true);
		title.revalidate();
	}
}

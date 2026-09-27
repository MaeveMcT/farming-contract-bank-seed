package com.farmingcontractbankseed;

import java.util.Arrays;
import java.util.List;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.widgets.Widget;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ContractSectionWidgetsTest
{
	@Test
	public void returnsTheExistingBankWidgetsForSeedAndSapling()
	{
		Widget unrelated = itemWidget(ItemID.COINS);
		Widget seed = itemWidget(ItemID.MAGIC_TREE_SEED);
		Widget sapling = itemWidget(ItemID.PLANTPOT_MAGIC_TREE_SAPLING);

		List<Widget> result = ContractSectionWidgets.findExisting(
			new Widget[]{unrelated, sapling, seed},
			Arrays.asList(ItemID.MAGIC_TREE_SEED, ItemID.PLANTPOT_MAGIC_TREE_SAPLING));

		assertEquals(2, result.size());
		assertSame(seed, result.get(0));
		assertSame(sapling, result.get(1));
	}

	@Test
	public void doesNotSelectAnItemHiddenByTheActiveBankTab()
	{
		Widget hiddenSeed = itemWidget(ItemID.MAGIC_TREE_SEED);
		when(hiddenSeed.isHidden()).thenReturn(true);

		List<Widget> result = ContractSectionWidgets.findExisting(
			new Widget[]{hiddenSeed}, Arrays.asList(ItemID.MAGIC_TREE_SEED));

		assertEquals(0, result.size());
	}

	private static Widget itemWidget(int itemId)
	{
		Widget widget = mock(Widget.class);
		when(widget.getItemId()).thenReturn(itemId);
		return widget;
	}
}

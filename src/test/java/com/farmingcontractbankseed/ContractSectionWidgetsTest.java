package com.farmingcontractbankseed;

import java.util.Arrays;
import java.util.List;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.ScriptEvent;
import net.runelite.api.widgets.JavaScriptCallback;
import org.mockito.ArgumentCaptor;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.widgets.ItemQuantityMode;
import net.runelite.api.widgets.WidgetType;
import net.runelite.api.widgets.Widget;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.anyInt;

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

	@Test
	public void createsZeroQuantityDisplayForMissingSapling()
	{
		Widget container = mock(Widget.class);
		Widget missing = mock(Widget.class);
		when(container.createChild(-1, WidgetType.GRAPHIC)).thenReturn(missing);
		ItemContainer bank = mock(ItemContainer.class);
		Client client = mock(Client.class);
		ItemComposition definition = mock(ItemComposition.class);
		when(definition.getName()).thenReturn("Dragonfruit sapling");
		when(client.getItemDefinition(ItemID.PLANTPOT_DRAGONFRUIT_SAPLING)).thenReturn(definition);

		List<Widget> result = ContractSectionWidgets.findOrCreateMissing(container, new Widget[0],
			Arrays.asList(ItemID.PLANTPOT_DRAGONFRUIT_SAPLING), bank, client);

		assertEquals(1, result.size());
		assertSame(missing, result.get(0));
		verify(missing).setItemId(ItemID.PLANTPOT_DRAGONFRUIT_SAPLING);
		verify(missing).setItemQuantity(0);
		verify(missing).setItemQuantityMode(ItemQuantityMode.ALWAYS);
		verify(missing).setOpacity(120);
		verify(missing).setName("<col=ff9040>Dragonfruit sapling</col>");
		verify(missing).setAction(0, "Check");
		verify(missing).setHasListener(true);
		ArgumentCaptor<Object[]> listener = ArgumentCaptor.forClass(Object[].class);
		verify(missing).setOnOpListener(listener.capture());
		((JavaScriptCallback) listener.getValue()[0]).run(mock(ScriptEvent.class));
		verify(client).addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Dragonfruit sapling: 0 in bank.", null);
	}

	@Test
	public void doesNotShowFalseZeroForBankedItemInAnotherTab()
	{
		Widget container = mock(Widget.class);
		Widget hidden = itemWidget(ItemID.MAGIC_TREE_SEED);
		when(hidden.isHidden()).thenReturn(true);
		ItemContainer bank = mock(ItemContainer.class);
		when(bank.count(ItemID.MAGIC_TREE_SEED)).thenReturn(4);

		List<Widget> result = ContractSectionWidgets.findOrCreateMissing(container, new Widget[]{hidden},
			Arrays.asList(ItemID.MAGIC_TREE_SEED), bank, mock(Client.class));

		assertEquals(0, result.size());
		verify(container, never()).createChild(anyInt(), anyInt());
	}

	@Test
	public void reusesVisibleWidgetInsteadOfCreatingDuplicateZero()
	{
		Widget container = mock(Widget.class);
		Widget seed = itemWidget(ItemID.MAGIC_TREE_SEED);
		List<Widget> result = ContractSectionWidgets.findOrCreateMissing(container, new Widget[]{seed},
			Arrays.asList(ItemID.MAGIC_TREE_SEED), mock(ItemContainer.class), mock(Client.class));

		assertSame(seed, result.get(0));
		verify(container, never()).createChild(anyInt(), anyInt());
	}

	private static Widget itemWidget(int itemId)
	{
		Widget widget = mock(Widget.class);
		when(widget.getItemId()).thenReturn(itemId);
		return widget;
	}
}

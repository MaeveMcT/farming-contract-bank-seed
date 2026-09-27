package com.farmingcontractbankseed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BankSectionLayoutTest
{
	@Test
	public void gapIsBelowTheUnchangedContractSection()
	{
		Widget container = mock(Widget.class);
		Widget divider = mock(Widget.class);
		Widget title = mock(Widget.class);
		Widget seed = mock(Widget.class);
		when(container.createChild(-1, WidgetType.GRAPHIC)).thenReturn(divider);
		when(container.createChild(-1, WidgetType.TEXT)).thenReturn(title);

		BankSectionLayout.placeItems(Collections.singletonList(seed), 0);
		BankSectionLayout.createHeader(container, "Farming contract", 0, 1);

		verify(seed).setOriginalY(20);
		verify(divider).setOriginalY(54);
		assertEquals(68, BankSectionLayout.sectionHeight(1));
	}

	@Test
	public void preplantSectionStartsAfterContractAndWrapsAfterEightItems()
	{
		List<Widget> items = new ArrayList<>();
		for (int i = 0; i < 9; i++)
		{
			items.add(mock(Widget.class));
		}
		int contractHeight = BankSectionLayout.sectionHeight(1);
		BankSectionLayout.placeItems(items, contractHeight);

		verify(items.get(0)).setOriginalY(contractHeight + 20);
		verify(items.get(8)).setOriginalY(contractHeight + 20 + 36);
		assertEquals(104, BankSectionLayout.sectionHeight(9));
	}
}

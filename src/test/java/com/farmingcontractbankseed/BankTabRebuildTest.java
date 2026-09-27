package com.farmingcontractbankseed;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import net.runelite.api.ScriptID;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.widgets.Widget;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BankTabRebuildTest
{
	@Test
	@SuppressWarnings("unchecked")
	public void rebuildingSelectedTabKeepsClientVisibilityAndPositions() throws Exception
	{
		FarmingContractBankSeedPlugin plugin = new FarmingContractBankSeedPlugin();
		AtomicBoolean hidden = new AtomicBoolean(false);
		AtomicInteger x = new AtomicInteger(10);
		Widget item = mock(Widget.class);
		when(item.isSelfHidden()).thenAnswer(invocation -> hidden.get());
		when(item.getOriginalX()).thenAnswer(invocation -> x.get());
		doAnswer(invocation -> {
			hidden.set(invocation.getArgument(0));
			return item;
		}).when(item).setHidden(anyBoolean());
		doAnswer(invocation -> {
			x.set(invocation.getArgument(0));
			return item;
		}).when(item).setOriginalX(anyInt());

		// The plugin saved the all-items position before shifting this widget.
		Class<?> positionClass = Class.forName(FarmingContractBankSeedPlugin.class.getName() + "$WidgetPosition");
		Constructor<?> constructor = positionClass.getDeclaredConstructor(Widget.class);
		constructor.setAccessible(true);
		Field positionsField = FarmingContractBankSeedPlugin.class.getDeclaredField("shiftedWidgets");
		positionsField.setAccessible(true);
		((List<Object>) positionsField.get(plugin)).add(constructor.newInstance(item));
		x.set(20);

		// Restore our layout BEFORE the bank script builds the selected tab.
		ScriptPreFired event = mock(ScriptPreFired.class);
		when(event.getScriptId()).thenReturn(ScriptID.BANKMAIN_BUILD, ScriptID.BANKMAIN_FINISHBUILDING);
		plugin.onScriptPreFired(event);
		assertEquals(10, x.get());

		// The bank script hides and repositions items outside the selected tab.
		hidden.set(true);
		x.set(40);
		plugin.onScriptPreFired(event);

		assertTrue("The selected bank tab must keep out-of-tab items hidden", hidden.get());
		assertEquals("The selected bank tab must keep its own layout", 40, x.get());
	}
}

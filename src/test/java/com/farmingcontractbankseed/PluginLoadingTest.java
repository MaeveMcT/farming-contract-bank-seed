package com.farmingcontractbankseed;

import com.google.inject.Injector;
import java.lang.reflect.Field;
import java.util.Collections;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.timetracking.TimeTrackingPlugin;
import net.runelite.client.plugins.timetracking.farming.FarmingContractManager;
import net.runelite.client.ui.overlay.OverlayManager;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PluginLoadingTest
{
	@Test
	public void doesNotDependOnTimeTrackingPluginWhichExposesNoServices()
	{
		assertNull(FarmingContractBankSeedPlugin.class.getAnnotation(PluginDependency.class));
	}

	@Test
	public void usesTheExistingTimeTrackingContractManager() throws Exception
	{
		FarmingContractBankSeedPlugin plugin = new FarmingContractBankSeedPlugin();
		FarmingContractManager manager = mock(FarmingContractManager.class);
		TimeTrackingPlugin timeTracking = mock(TimeTrackingPlugin.class);
		Injector timeTrackingInjector = mock(Injector.class);
		when(timeTracking.getInjector()).thenReturn(timeTrackingInjector);
		when(timeTrackingInjector.getInstance(FarmingContractManager.class)).thenReturn(manager);
		PluginManager pluginManager = mock(PluginManager.class);
		when(pluginManager.getPlugins()).thenReturn(Collections.singletonList(timeTracking));
		OverlayManager overlayManager = mock(OverlayManager.class);
		PreplantPatchOverlay patchOverlay = mock(PreplantPatchOverlay.class);
		PreplantInventoryOverlay inventoryOverlay = mock(PreplantInventoryOverlay.class);
		inject(plugin, "pluginManager", pluginManager);
		inject(plugin, "overlayManager", overlayManager);
		inject(plugin, "patchOverlay", patchOverlay);
		inject(plugin, "inventoryOverlay", inventoryOverlay);

		plugin.startUp();
		assertSame(manager, plugin.getContractManager());
		plugin.shutDown();
		assertNull(plugin.getContractManager());
		verify(overlayManager).remove(patchOverlay);
		verify(overlayManager).remove(inventoryOverlay);
	}

	@Test
	public void preplantFeaturesCanStartWithoutTimeTracking() throws Exception
	{
		FarmingContractBankSeedPlugin plugin = new FarmingContractBankSeedPlugin();
		PluginManager pluginManager = mock(PluginManager.class);
		when(pluginManager.getPlugins()).thenReturn(Collections.emptyList());
		inject(plugin, "pluginManager", pluginManager);
		inject(plugin, "overlayManager", mock(OverlayManager.class));
		inject(plugin, "patchOverlay", mock(PreplantPatchOverlay.class));
		inject(plugin, "inventoryOverlay", mock(PreplantInventoryOverlay.class));

		plugin.startUp();
		assertNull(plugin.getContractManager());
		plugin.shutDown();
	}

	private static void inject(FarmingContractBankSeedPlugin plugin, String name, Object value) throws Exception
	{
		Field field = FarmingContractBankSeedPlugin.class.getDeclaredField(name);
		field.setAccessible(true);
		field.set(plugin, value);
	}
}

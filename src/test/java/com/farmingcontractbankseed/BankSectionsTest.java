package com.farmingcontractbankseed;

import com.google.inject.Injector;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.runelite.api.Client;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemComposition;
import net.runelite.api.Player;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.timetracking.TimeTrackingPlugin;
import net.runelite.client.plugins.timetracking.farming.FarmingContractManager;
import net.runelite.client.plugins.timetracking.farming.Produce;
import net.runelite.client.ui.overlay.OverlayManager;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BankSectionsTest
{
	@Test
	public void compostChoiceIsAccessibleThroughConfigProxy()
	{
		FarmingContractBankSeedConfig config = (FarmingContractBankSeedConfig) Proxy.newProxyInstance(
			FarmingContractBankSeedConfig.class.getClassLoader(),
			new Class<?>[]{FarmingContractBankSeedConfig.class},
			(proxy, method, args) -> CompostChoice.NONE);

		org.junit.Assert.assertEquals(CompostChoice.NONE, config.compost());
	}

	@Test
	public void preplantSectionShowsMissingSaplingButDoesNotUnhideBankedSeedInAnotherTab() throws Exception
	{
		FarmingContractBankSeedPlugin plugin = new FarmingContractBankSeedPlugin();
		Client client = mock(Client.class);
		Player player = mock(Player.class);
		when(client.getLocalPlayer()).thenReturn(player);
		when(player.getWorldLocation()).thenReturn(new WorldPoint(1248, 3730, 0));
		Widget container = mock(Widget.class);
		Widget seed = mock(Widget.class);
		Widget hidden = mock(Widget.class);
		when(seed.getItemId()).thenReturn(ItemID.WATERMELON_SEED);
		when(seed.getOriginalWidth()).thenReturn(36);
		when(seed.getOriginalHeight()).thenReturn(32);
		when(hidden.getItemId()).thenReturn(ItemID.DRAGONFRUIT_TREE_SEED);
		when(hidden.isHidden()).thenReturn(true);
		when(container.getChildren()).thenReturn(new Widget[]{seed, hidden});
		when(container.getScrollHeight()).thenReturn(200);
		when(container.createChild(-1, WidgetType.GRAPHIC)).thenReturn(mock(Widget.class));
		Widget title = mock(Widget.class);
		when(container.createChild(-1, WidgetType.TEXT)).thenReturn(title);
		when(client.getWidget(InterfaceID.Bankmain.ITEMS)).thenReturn(container);
		ItemContainer bank = mock(ItemContainer.class);
		when(client.getItemContainer(InventoryID.BANK)).thenReturn(bank);
		when(bank.find(anyInt())).thenReturn(-1);
		when(bank.find(ItemID.WATERMELON_SEED)).thenReturn(1);
		when(bank.find(ItemID.DRAGONFRUIT_TREE_SEED)).thenReturn(2);
		when(bank.count(ItemID.DRAGONFRUIT_TREE_SEED)).thenReturn(1);

		FarmingContractBankSeedConfig config = mock(FarmingContractBankSeedConfig.class, CALLS_REAL_METHODS);
		when(config.northAllotment()).thenReturn(PreplantChoice.Allotment.WATERMELON);
		when(config.fruitTree()).thenReturn(PreplantChoice.FruitTree.DRAGONFRUIT);
		inject(plugin, "client", client);
		inject(plugin, "config", config);
		inject(plugin, "contractManager", mock(FarmingContractManager.class));
		start(plugin);

		Method show = FarmingContractBankSeedPlugin.class.getDeclaredMethod("showBankSections");
		show.setAccessible(true);
		show.invoke(plugin);

		verify(container).setScrollHeight(200 + BankSectionLayout.sectionHeight(2));
		verify(title).setText("Preplant seeds/saplings");
		verify(seed).setOriginalY(20);
		verify(hidden, never()).setHidden(false);
	}

	@Test
	public void contractSectionPrecedesPreplantAndOwnsSharedSeed() throws Exception
	{
		FarmingContractBankSeedPlugin plugin = new FarmingContractBankSeedPlugin();
		Client client = mock(Client.class);
		Player player = mock(Player.class);
		when(client.getLocalPlayer()).thenReturn(player);
		when(player.getWorldLocation()).thenReturn(new WorldPoint(1248, 3730, 0));
		Widget container = mock(Widget.class);
		Widget contractSeed = mock(Widget.class);
		Widget preplantSeed = mock(Widget.class);
		Widget compost = mock(Widget.class);
		when(contractSeed.getItemId()).thenReturn(ItemID.WATERMELON_SEED);
		when(contractSeed.getOriginalWidth()).thenReturn(36);
		when(contractSeed.getOriginalHeight()).thenReturn(32);
		when(preplantSeed.getItemId()).thenReturn(ItemID.ONION_SEED);
		when(compost.getItemId()).thenReturn(ItemID.BUCKET_COMPOST);
		when(container.getChildren()).thenReturn(new Widget[]{contractSeed, preplantSeed, compost});
		when(container.getScrollHeight()).thenReturn(200);
		when(container.createChild(-1, WidgetType.GRAPHIC)).thenReturn(mock(Widget.class));
		when(container.createChild(-1, WidgetType.TEXT)).thenReturn(mock(Widget.class));
		when(client.getWidget(InterfaceID.Bankmain.ITEMS)).thenReturn(container);
		ItemContainer bank = mock(ItemContainer.class);
		when(client.getItemContainer(InventoryID.BANK)).thenReturn(bank);
		when(bank.find(anyInt())).thenReturn(-1);
		when(bank.find(ItemID.WATERMELON_SEED)).thenReturn(1);
		when(bank.find(ItemID.ONION_SEED)).thenReturn(2);
		when(bank.find(ItemID.BUCKET_COMPOST)).thenReturn(3);
		FarmingContractBankSeedConfig config = mock(FarmingContractBankSeedConfig.class, CALLS_REAL_METHODS);
		when(config.northAllotment()).thenReturn(PreplantChoice.Allotment.WATERMELON);
		when(config.southAllotment()).thenReturn(PreplantChoice.Allotment.ONION);
		when(config.compost()).thenReturn(CompostChoice.COMPOST);
		FarmingContractManager contracts = mock(FarmingContractManager.class);
		when(contracts.hasContract()).thenReturn(true);
		when(contracts.getContract()).thenReturn(Produce.WATERMELON);
		inject(plugin, "client", client);
		inject(plugin, "config", config);
		inject(plugin, "contractManager", contracts);
		start(plugin);

		Method show = FarmingContractBankSeedPlugin.class.getDeclaredMethod("showBankSections");
		show.setAccessible(true);
		show.invoke(plugin);

		verify(contractSeed).setOriginalY(20);
		verify(compost).setOriginalY(20);
		verify(preplantSeed).setOriginalY(BankSectionLayout.sectionHeight(2) + 20);
		verify(container).setScrollHeight(200 + BankSectionLayout.sectionHeight(2) + BankSectionLayout.sectionHeight(1));
	}

	@Test
	public void selectedCompostJoinsContractSectionOnlyWhenVisibleAndBanked() throws Exception
	{
		FarmingContractBankSeedPlugin plugin = new FarmingContractBankSeedPlugin();
		Client client = mock(Client.class);
		Player player = mock(Player.class);
		when(client.getLocalPlayer()).thenReturn(player);
		when(player.getWorldLocation()).thenReturn(new WorldPoint(1248, 3730, 0));
		Widget container = mock(Widget.class);
		Widget seed = mock(Widget.class);
		Widget compost = mock(Widget.class);
		when(seed.getItemId()).thenReturn(ItemID.WATERMELON_SEED);
		when(seed.getOriginalWidth()).thenReturn(36);
		when(seed.getOriginalHeight()).thenReturn(32);
		when(compost.getItemId()).thenReturn(ItemID.BUCKET_ULTRACOMPOST);
		when(container.getChildren()).thenReturn(new Widget[]{seed, compost});
		when(container.createChild(-1, WidgetType.GRAPHIC)).thenReturn(mock(Widget.class));
		when(container.createChild(-1, WidgetType.TEXT)).thenReturn(mock(Widget.class));
		when(client.getWidget(InterfaceID.Bankmain.ITEMS)).thenReturn(container);
		ItemContainer bank = mock(ItemContainer.class);
		when(client.getItemContainer(InventoryID.BANK)).thenReturn(bank);
		when(bank.find(anyInt())).thenReturn(-1);
		when(bank.find(ItemID.WATERMELON_SEED)).thenReturn(1);
		when(bank.find(ItemID.BUCKET_ULTRACOMPOST)).thenReturn(2);
		FarmingContractBankSeedConfig config = mock(FarmingContractBankSeedConfig.class, CALLS_REAL_METHODS);
		when(config.compost()).thenReturn(CompostChoice.ULTRACOMPOST);
		FarmingContractManager contracts = mock(FarmingContractManager.class);
		when(contracts.hasContract()).thenReturn(true);
		when(contracts.getContract()).thenReturn(Produce.WATERMELON);
		inject(plugin, "client", client);
		inject(plugin, "config", config);
		inject(plugin, "contractManager", contracts);
		start(plugin);

		Method show = FarmingContractBankSeedPlugin.class.getDeclaredMethod("showBankSections");
		show.setAccessible(true);
		show.invoke(plugin);

		verify(compost).setOriginalY(20);
		verify(container).setScrollHeight(BankSectionLayout.sectionHeight(2));
	}

	@Test
	public void filledBottomlessBucketJoinsPreplantSectionWithoutContract() throws Exception
	{
		FarmingContractBankSeedPlugin plugin = new FarmingContractBankSeedPlugin();
		Client client = mock(Client.class);
		Player player = mock(Player.class);
		when(client.getLocalPlayer()).thenReturn(player);
		when(player.getWorldLocation()).thenReturn(new WorldPoint(1248, 3730, 0));
		Widget container = mock(Widget.class);
		Widget seed = mock(Widget.class);
		Widget bucket = mock(Widget.class);
		when(seed.getItemId()).thenReturn(ItemID.ONION_SEED);
		when(seed.getOriginalWidth()).thenReturn(36);
		when(seed.getOriginalHeight()).thenReturn(32);
		when(bucket.getItemId()).thenReturn(ItemID.BOTTOMLESS_COMPOST_BUCKET_FILLED);
		when(container.getChildren()).thenReturn(new Widget[]{seed, bucket});
		when(container.createChild(-1, WidgetType.GRAPHIC)).thenReturn(mock(Widget.class));
		when(container.createChild(-1, WidgetType.TEXT)).thenReturn(mock(Widget.class));
		when(client.getWidget(InterfaceID.Bankmain.ITEMS)).thenReturn(container);
		ItemContainer bank = mock(ItemContainer.class);
		when(client.getItemContainer(InventoryID.BANK)).thenReturn(bank);
		when(bank.find(anyInt())).thenReturn(-1);
		when(bank.find(ItemID.ONION_SEED)).thenReturn(1);
		when(bank.find(ItemID.BOTTOMLESS_COMPOST_BUCKET_FILLED)).thenReturn(2);
		FarmingContractBankSeedConfig config = mock(FarmingContractBankSeedConfig.class, CALLS_REAL_METHODS);
		when(config.northAllotment()).thenReturn(PreplantChoice.Allotment.ONION);
		when(config.compost()).thenReturn(CompostChoice.BOTTOMLESS_COMPOST_BUCKET);
		inject(plugin, "client", client);
		inject(plugin, "config", config);
		inject(plugin, "contractManager", mock(FarmingContractManager.class));
		start(plugin);

		Method show = FarmingContractBankSeedPlugin.class.getDeclaredMethod("showBankSections");
		show.setAccessible(true);
		show.invoke(plugin);

		verify(bucket).setOriginalY(20);
		verify(container).setScrollHeight(BankSectionLayout.sectionHeight(2));
	}

	@Test
	public void missingSeedsKeepBothSectionsAndCompostJoinsContract() throws Exception
	{
		FarmingContractBankSeedPlugin plugin = new FarmingContractBankSeedPlugin();
		Client client = mock(Client.class);
		Player player = mock(Player.class);
		when(client.getLocalPlayer()).thenReturn(player);
		when(player.getWorldLocation()).thenReturn(new WorldPoint(1248, 3730, 0));
		Widget container = mock(Widget.class);
		Widget compost = mock(Widget.class);
		when(compost.getItemId()).thenReturn(ItemID.BUCKET_SUPERCOMPOST);
		when(compost.getOriginalWidth()).thenReturn(36);
		when(compost.getOriginalHeight()).thenReturn(32);
		when(container.getChildren()).thenReturn(new Widget[]{compost});
		Widget title = mock(Widget.class);
		when(container.createChild(-1, WidgetType.GRAPHIC)).thenReturn(mock(Widget.class));
		when(container.createChild(-1, WidgetType.TEXT)).thenReturn(title);
		when(client.getWidget(InterfaceID.Bankmain.ITEMS)).thenReturn(container);
		ItemContainer bank = mock(ItemContainer.class);
		when(client.getItemContainer(InventoryID.BANK)).thenReturn(bank);
		when(bank.find(anyInt())).thenReturn(-1);
		when(bank.find(ItemID.BUCKET_SUPERCOMPOST)).thenReturn(2);
		FarmingContractBankSeedConfig config = mock(FarmingContractBankSeedConfig.class, CALLS_REAL_METHODS);
		when(config.northAllotment()).thenReturn(PreplantChoice.Allotment.ONION);
		when(config.compost()).thenReturn(CompostChoice.SUPERCOMPOST);
		FarmingContractManager contracts = mock(FarmingContractManager.class);
		when(contracts.hasContract()).thenReturn(true);
		when(contracts.getContract()).thenReturn(Produce.WATERMELON);
		inject(plugin, "client", client);
		inject(plugin, "config", config);
		inject(plugin, "contractManager", contracts);
		start(plugin);

		Method show = FarmingContractBankSeedPlugin.class.getDeclaredMethod("showBankSections");
		show.setAccessible(true);
		show.invoke(plugin);

		verify(title).setText("Preplant seeds/saplings");
		verify(title).setText("Farming contract");
		verify(compost).setOriginalY(20);
		verify(container).setScrollHeight(BankSectionLayout.sectionHeight(2) + BankSectionLayout.sectionHeight(1));
	}

	@Test
	public void missingWidgetsAreReplacedOnRebuildAndRemovedOnShutdown() throws Exception
	{
		FarmingContractBankSeedPlugin plugin = new FarmingContractBankSeedPlugin();
		Client client = mock(Client.class);
		Player player = mock(Player.class);
		when(client.getLocalPlayer()).thenReturn(player);
		when(player.getWorldLocation()).thenReturn(new WorldPoint(1248, 3730, 0));
		Widget container = mock(Widget.class);
		Widget coins = mock(Widget.class);
		when(coins.getItemId()).thenReturn(ItemID.COINS);
		when(coins.getOriginalWidth()).thenReturn(36);
		when(coins.getOriginalHeight()).thenReturn(32);
		List<Widget> children = new ArrayList<>(Collections.singletonList(coins));
		when(container.getChildren()).thenAnswer(invocation -> children.toArray(new Widget[0]));
		when(container.createChild(anyInt(), anyInt())).thenAnswer(invocation -> {
			Widget child = mock(Widget.class);
			children.add(child);
			return child;
		});
		org.mockito.Mockito.doAnswer(invocation -> {
			children.clear();
			children.addAll(Arrays.asList((Widget[]) invocation.getArgument(0)));
			return null;
		}).when(container).setChildren(any(Widget[].class));
		when(client.getWidget(InterfaceID.Bankmain.ITEMS)).thenReturn(container);
		when(client.getItemContainer(InventoryID.BANK)).thenReturn(mock(ItemContainer.class));
		FarmingContractBankSeedConfig config = mock(FarmingContractBankSeedConfig.class, CALLS_REAL_METHODS);
		when(config.fruitTree()).thenReturn(PreplantChoice.FruitTree.DRAGONFRUIT);
		inject(plugin, "client", client);
		inject(plugin, "config", config);
		inject(plugin, "contractManager", mock(FarmingContractManager.class));
		start(plugin);

		Method show = FarmingContractBankSeedPlugin.class.getDeclaredMethod("showBankSections");
		show.setAccessible(true);
		show.invoke(plugin);
		assertEquals(5, children.size()); // Original item, seed, sapling, divider, title.
		show.invoke(plugin);
		assertEquals(5, children.size());
		plugin.shutDown();
		assertEquals(Collections.singletonList(coins), children);
	}

	private static void start(FarmingContractBankSeedPlugin plugin) throws Exception
	{
		Field clientField = FarmingContractBankSeedPlugin.class.getDeclaredField("client");
		clientField.setAccessible(true);
		Client client = (Client) clientField.get(plugin);
		ItemComposition definition = mock(ItemComposition.class);
		when(definition.getName()).thenReturn("Seed or sapling");
		when(client.getItemDefinition(anyInt())).thenReturn(definition);
		inject(plugin, "overlayManager", mock(OverlayManager.class));
		inject(plugin, "patchOverlay", mock(PreplantPatchOverlay.class));
		inject(plugin, "inventoryOverlay", mock(PreplantInventoryOverlay.class));
		TimeTrackingPlugin timeTrackingPlugin = mock(TimeTrackingPlugin.class);
		Injector injector = mock(Injector.class);
		when(timeTrackingPlugin.getInjector()).thenReturn(injector);
		when(injector.getInstance(FarmingContractManager.class)).thenReturn(plugin.getContractManager());
		PluginManager pluginManager = mock(PluginManager.class);
		when(pluginManager.getPlugins()).thenReturn(Collections.singletonList(timeTrackingPlugin));
		inject(plugin, "pluginManager", pluginManager);
		plugin.startUp();
	}

	private static void inject(Object object, String fieldName, Object value) throws Exception
	{
		Field field = object.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(object, value);
	}
}

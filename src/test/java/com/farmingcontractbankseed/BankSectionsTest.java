package com.farmingcontractbankseed;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicBoolean;
import net.runelite.api.Client;
import net.runelite.api.ItemContainer;
import net.runelite.api.Player;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.plugins.timetracking.farming.Produce;
import net.runelite.client.plugins.timetracking.farming.FarmingContractManager;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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
	public void preplantSectionAppearsWithoutActiveContractAndMovesOnlyVisibleBankedSeed() throws Exception
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

		FarmingContractBankSeedConfig config = mock(FarmingContractBankSeedConfig.class, CALLS_REAL_METHODS);
		when(config.northAllotment()).thenReturn(PreplantChoice.Allotment.WATERMELON);
		when(config.fruitTree()).thenReturn(PreplantChoice.FruitTree.DRAGONFRUIT);
		inject(plugin, "client", client);
		inject(plugin, "config", config);
		inject(plugin, "contractManager", mock(FarmingContractManager.class));
		plugin.startUp();

		Method show = FarmingContractBankSeedPlugin.class.getDeclaredMethod("showBankSections");
		show.setAccessible(true);
		show.invoke(plugin);

		verify(container).setScrollHeight(200 + BankSectionLayout.sectionHeight(1));
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
		plugin.startUp();

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
		plugin.startUp();

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
		plugin.startUp();

		Method show = FarmingContractBankSeedPlugin.class.getDeclaredMethod("showBankSections");
		show.setAccessible(true);
		show.invoke(plugin);

		verify(bucket).setOriginalY(20);
		verify(container).setScrollHeight(BankSectionLayout.sectionHeight(2));
	}

	@Test
	public void compostKeepsPreplantSectionAfterContractAndPreplantSeedsAreWithdrawn() throws Exception
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
		AtomicBoolean compostBanked = new AtomicBoolean(true);
		when(bank.find(ItemID.BUCKET_SUPERCOMPOST)).thenAnswer(invocation -> compostBanked.get() ? 2 : -1);
		FarmingContractBankSeedConfig config = mock(FarmingContractBankSeedConfig.class, CALLS_REAL_METHODS);
		when(config.northAllotment()).thenReturn(PreplantChoice.Allotment.ONION);
		when(config.compost()).thenReturn(CompostChoice.SUPERCOMPOST);
		FarmingContractManager contracts = mock(FarmingContractManager.class);
		when(contracts.hasContract()).thenReturn(true);
		when(contracts.getContract()).thenReturn(Produce.WATERMELON);
		inject(plugin, "client", client);
		inject(plugin, "config", config);
		inject(plugin, "contractManager", contracts);
		plugin.startUp();

		Method show = FarmingContractBankSeedPlugin.class.getDeclaredMethod("showBankSections");
		show.setAccessible(true);
		show.invoke(plugin);

		verify(title).setText("Preplant seeds/saplings");
		verify(compost).setOriginalY(20);
		verify(container).setScrollHeight(BankSectionLayout.sectionHeight(1));

		compostBanked.set(false);
		show.invoke(plugin);
		verify(title, times(1)).setText("Preplant seeds/saplings");
		verify(container, times(1)).createChild(-1, WidgetType.TEXT);
	}

	private static void inject(Object object, String fieldName, Object value) throws Exception
	{
		Field field = object.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(object, value);
	}
}

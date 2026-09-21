package com.farmingcontractbankseed;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class FarmingContractBankSeedPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(FarmingContractBankSeedPlugin.class);
		RuneLite.main(args);
	}
}

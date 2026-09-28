package com.farmingcontractbankseed;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(FarmingContractBankSeedConfig.GROUP)
public interface FarmingContractBankSeedConfig extends Config
{
	String GROUP = "farmingcontractbankseed";

	@ConfigItem(keyName = "northAllotment", name = "North allotment", description = "Crop to preplant in the north allotment", position = 1)
	default PreplantChoice.Allotment northAllotment() { return PreplantChoice.Allotment.NONE; }

	@ConfigItem(keyName = "southAllotment", name = "South allotment", description = "Crop to preplant in the south allotment", position = 2)
	default PreplantChoice.Allotment southAllotment() { return PreplantChoice.Allotment.NONE; }

	@ConfigItem(keyName = "flower", name = "Flower", description = "Crop to preplant in the flower patch", position = 3)
	default PreplantChoice.Flower flower() { return PreplantChoice.Flower.NONE; }

	@ConfigItem(keyName = "herb", name = "Herb", description = "Crop to preplant in the herb patch", position = 4)
	default PreplantChoice.Herb herb() { return PreplantChoice.Herb.NONE; }

	@ConfigItem(keyName = "bush", name = "Bush", description = "Crop to preplant in the bush patch", position = 5)
	default PreplantChoice.Bush bush() { return PreplantChoice.Bush.NONE; }

	@ConfigItem(keyName = "cactus", name = "Cactus", description = "Crop to preplant in the cactus patch", position = 6)
	default PreplantChoice.Cactus cactus() { return PreplantChoice.Cactus.NONE; }

	@ConfigItem(keyName = "tree", name = "Tree", description = "Crop to preplant in the tree patch", position = 7)
	default PreplantChoice.Tree tree() { return PreplantChoice.Tree.NONE; }

	@ConfigItem(keyName = "fruitTree", name = "Fruit tree", description = "Crop to preplant in the fruit tree patch", position = 8)
	default PreplantChoice.FruitTree fruitTree() { return PreplantChoice.FruitTree.NONE; }

	@ConfigItem(keyName = "celastrus", name = "Celastrus", description = "Preplant a celastrus tree", position = 9)
	default PreplantChoice.Celastrus celastrus() { return PreplantChoice.Celastrus.NONE; }

	@ConfigItem(keyName = "redwood", name = "Redwood", description = "Preplant a redwood tree", position = 10)
	default PreplantChoice.Redwood redwood() { return PreplantChoice.Redwood.NONE; }

	@ConfigItem(keyName = "compost", name = "Compost", description = "Compost item to show alongside contract or preplant seeds", position = 11)
	default CompostChoice compost() { return CompostChoice.NONE; }
}

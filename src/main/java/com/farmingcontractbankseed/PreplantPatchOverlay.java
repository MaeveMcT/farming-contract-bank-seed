package com.farmingcontractbankseed;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Perspective;
import net.runelite.api.Tile;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

class PreplantPatchOverlay extends Overlay
{
	private final Client client;
	private final FarmingContractBankSeedConfig config;
	private final FarmingContractBankSeedPlugin plugin;
	private final Map<GameObject, Color> patches = new LinkedHashMap<>();

	@Inject
	PreplantPatchOverlay(Client client, FarmingContractBankSeedConfig config, FarmingContractBankSeedPlugin plugin)
	{
		this.client = client;
		this.config = config;
		this.plugin = plugin;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	void refresh()
	{
		patches.clear();
		if ((!config.highlightPreplantPatches() && !config.highlightContractPatch())
			|| !plugin.isInFarmingGuild() || client.getScene() == null)
		{
			return;
		}

		Map<Integer, Color> colors = new LinkedHashMap<>();
		for (PreplantSeeds.Patch patch : FarmingHighlights.readyPatches(config, plugin.getContractManager(), client::getVarbitValue))
		{
			colors.put(patch.varbit, patch.color);
		}
		if (colors.isEmpty())
		{
			return;
		}

		Set<GameObject> seen = new HashSet<>();
		Tile[][] tiles = client.getScene().getTiles()[client.getPlane()];
		for (Tile[] row : tiles)
		{
			for (Tile tile : row)
			{
				if (tile == null)
				{
					continue;
				}
				for (GameObject object : tile.getGameObjects())
				{
					if (object != null && seen.add(object))
					{
						Color color = colors.get(client.getObjectDefinition(object.getId()).getVarbitId());
						if (color != null)
						{
							patches.put(object, color);
						}
					}
				}
			}
		}
	}

	void clear()
	{
		patches.clear();
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if ((!config.highlightPreplantPatches() && !config.highlightContractPatch()) || !plugin.isInFarmingGuild())
		{
			return null;
		}
		for (Map.Entry<GameObject, Color> entry : patches.entrySet())
		{
			GameObject object = entry.getKey();
			Polygon polygon = Perspective.getCanvasTileAreaPoly(client, object.getLocalLocation(),
				object.sizeX(), object.sizeY(), client.getPlane(), 0);
			if (polygon != null)
			{
				OverlayUtil.renderPolygon(graphics, polygon, entry.getValue());
			}
		}
		return null;
	}
}

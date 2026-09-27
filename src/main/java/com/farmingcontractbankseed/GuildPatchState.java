package com.farmingcontractbankseed;

/**
 * Farming Guild patch states as decoded by RuneLite's PatchImplementation.
 * Its per-patch prediction API is package-private; these are the WEEDS (unplanted)
 * and DEAD varbit ranges for the contract-eligible patch types. Update these
 * ranges if RuneLite changes its farming state decoding.
 */
final class GuildPatchState
{
	enum Type
	{
		ALLOTMENT(
			new int[]{0, 5, 74, 76, 81, 83, 88, 90, 95, 97, 104, 106, 113, 115, 124, 127,
				141, 141, 145, 148, 152, 155, 159, 162, 168, 171, 177, 180, 188, 192,
				205, 205, 212, 212, 216, 219, 223, 226, 232, 235, 241, 244, 252, 255},
			new int[]{193, 195, 199, 201, 206, 211, 213, 215, 220, 222, 227, 231, 236, 240, 245, 251}),
		HERB(
			new int[]{0, 3, 67, 67, 176, 191, 204, 219, 221, 255},
			new int[]{170, 172, 201, 203}),
		FLOWER(
			new int[]{0, 7, 42, 71, 76, 76, 81, 81, 86, 86, 91, 91, 96, 100, 105, 136,
				140, 141, 145, 146, 150, 151, 155, 156, 160, 165, 169, 200, 205, 205,
				210, 210, 215, 215, 220, 220, 225, 229, 234, 255},
			new int[]{201, 204, 206, 209, 211, 214, 216, 219, 221, 224, 230, 233}),
		BUSH(
			new int[]{0, 4, 64, 69, 75, 79, 86, 90, 98, 102, 111, 115, 124, 133,
				139, 143, 150, 154, 162, 166, 175, 179, 188, 196, 226, 249},
			new int[]{134, 138, 144, 149, 155, 161, 167, 174, 180, 187, 217, 224}),
		TREE(
			new int[]{0, 7, 63, 72, 78, 79, 87, 88, 98, 99, 111, 112, 126, 136,
				142, 143, 151, 152, 162, 163, 175, 176, 190, 191, 198, 255},
			new int[]{137, 139, 141, 141, 144, 148, 150, 150, 153, 159, 161, 161,
				164, 172, 174, 174, 177, 187, 189, 189}),
		FRUIT_TREE(
			new int[]{0, 7, 62, 71, 126, 135, 190, 199, 254, 255},
			new int[]{27, 32, 54, 59, 91, 96, 118, 123, 155, 160, 182, 187, 219, 224, 246, 251}),
		CACTUS(
			new int[]{0, 7, 59, 255},
			new int[]{25, 30, 52, 57}),
		CELASTRUS(
			new int[]{0, 7, 29, 255},
			new int[]{23, 27}),
		REDWOOD(
			new int[]{0, 7},
			new int[]{28, 36});

		private final int[] empty;
		private final int[] dead;

		Type(int[] empty, int[] dead)
		{
			this.empty = empty;
			this.dead = dead;
		}

		boolean isEmptyOrDead(int value)
		{
			return inRanges(value, empty) || inRanges(value, dead);
		}
	}

	private GuildPatchState()
	{
	}

	private static boolean inRanges(int value, int[] ranges)
	{
		for (int i = 0; i < ranges.length; i += 2)
		{
			if (value >= ranges[i] && value <= ranges[i + 1])
			{
				return true;
			}
		}
		return false;
	}
}

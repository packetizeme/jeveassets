/*
 * Copyright 2009-2021 Contributors (see credits.txt)
 *
 * This file is part of jEveAssets.
 *
 * jEveAssets is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * jEveAssets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with jEveAssets; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package net.nikr.eve.jeveasset.data.settings;

import java.util.Map;
import net.nikr.eve.jeveasset.i18n.DataColors;


public class ColorThemeColorblind extends ColorTheme {

	protected ColorThemeColorblind() {}

	@Override
	public String getName() {
		return DataColors.get().colorThemeColorblind();
	}

	@Override
	public ColorThemeTypes getType() {
		return ColorThemeTypes.COLORBLIND;
	}

	@Override
	protected void createColors(Map<ColorEntry, ColorThemeEntry> colors) {
		colors.put(ColorEntry.ASSETS_REPROCESSING_EQUAL, new ColorThemeEntry(Colors.COLORBLIND_ORANGE));
		colors.put(ColorEntry.ASSETS_REPROCESSING_REPROCES, new ColorThemeEntry(Colors.COLORBLIND_MAGENTA));
		colors.put(ColorEntry.ASSETS_REPROCESSING_SELL, new ColorThemeEntry(Colors.COLORBLIND_YELLOW));
		colors.put(ColorEntry.ASSETS_REPROCESS, new ColorThemeEntry(Colors.COLORBLIND_ORANGE));
		colors.put(ColorEntry.CUSTOM_PRICE, new ColorThemeEntry(Colors.COLORBLIND_GRAY));
		colors.put(ColorEntry.CUSTOM_ASSET_NAME, new ColorThemeEntry(Colors.COLORBLIND_GRAY));
		colors.put(ColorEntry.CUSTOM_USER_LOCATION, new ColorThemeEntry(Colors.COLORBLIND_GRAY));
		colors.put(ColorEntry.CONTRACTS_COURIER, new ColorThemeEntry(Colors.COLORBLIND_ORANGE));
		colors.put(ColorEntry.CONTRACTS_INCLUDED, new ColorThemeEntry(Colors.COLORBLIND_YELLOW));
		colors.put(ColorEntry.CONTRACTS_EXCLUDED, new ColorThemeEntry(Colors.COLORBLIND_MAGENTA));
		colors.put(ColorEntry.OVERVIEW_GROUPED_LOCATIONS, new ColorThemeEntry(Colors.COLORBLIND_YELLOW));
		colors.put(ColorEntry.STOCKPILE_TABLE_BELOW_THRESHOLD, new ColorThemeEntry(Colors.COLORBLIND_MAGENTA));
		colors.put(ColorEntry.STOCKPILE_ICON_BELOW_THRESHOLD, new ColorThemeEntry(Colors.COLORBLIND_MAGENTA));
		colors.put(ColorEntry.STOCKPILE_TABLE_BELOW_THRESHOLD_2ND, new ColorThemeEntry(Colors.COLORBLIND_ORANGE));
		colors.put(ColorEntry.STOCKPILE_ICON_BELOW_THRESHOLD_2ND, new ColorThemeEntry(Colors.COLORBLIND_ORANGE));
		colors.put(ColorEntry.STOCKPILE_TABLE_OVER_THRESHOLD, new ColorThemeEntry(Colors.COLORBLIND_YELLOW));
		colors.put(ColorEntry.STOCKPILE_ICON_OVER_THRESHOLD, new ColorThemeEntry(Colors.COLORBLIND_YELLOW));
		colors.put(ColorEntry.MARKET_ORDERS_OUTBID_NOT_BEST, new ColorThemeEntry(Colors.COLORBLIND_MAGENTA));
		colors.put(ColorEntry.MARKET_ORDERS_OUTBID_BEST, new ColorThemeEntry(Colors.COLORBLIND_YELLOW));
		colors.put(ColorEntry.MARKET_ORDERS_OUTBID_UNKNOWN, new ColorThemeEntry(Colors.COLORBLIND_GRAY));
		colors.put(ColorEntry.MARKET_ORDERS_EXPIRED, new ColorThemeEntry(Colors.COLORBLIND_MAGENTA));
		colors.put(ColorEntry.MARKET_ORDERS_NEAR_EXPIRED, new ColorThemeEntry(Colors.COLORBLIND_YELLOW));
		colors.put(ColorEntry.INDUSTRY_JOBS_DELIVERED, new ColorThemeEntry(Colors.COLORBLIND_ORANGE));
		colors.put(ColorEntry.INDUSTRY_JOBS_DONE, new ColorThemeEntry(Colors.COLORBLIND_YELLOW));
		colors.put(ColorEntry.INDUSTRY_SLOTS_FREE, new ColorThemeEntry(Colors.COLORBLIND_YELLOW));
		colors.put(ColorEntry.INDUSTRY_SLOTS_DONE, new ColorThemeEntry(Colors.COLORBLIND_ORANGE));
		colors.put(ColorEntry.INDUSTRY_SLOTS_FULL, new ColorThemeEntry(Colors.COLORBLIND_MAGENTA));
		colors.put(ColorEntry.TRANSACTIONS_BOUGHT, new ColorThemeEntry(Colors.COLORBLIND_MAGENTA));
		colors.put(ColorEntry.TRANSACTIONS_SOLD, new ColorThemeEntry(Colors.COLORBLIND_YELLOW));
		colors.put(ColorEntry.GLOBAL_BPC, new ColorThemeEntry(Colors.COLORBLIND_MAGENTA));
		colors.put(ColorEntry.GLOBAL_BPO, new ColorThemeEntry(Colors.COLORBLIND_ORANGE));
		colors.put(ColorEntry.GLOBAL_VALUE_NEGATIVE, new ColorThemeEntry(null, Colors.COLORBLIND_FOREGROUND_RED));
		colors.put(ColorEntry.GLOBAL_VALUE_POSITIVE, new ColorThemeEntry(null, Colors.COLORBLIND_FOREGROUND_GREEN));
		colors.put(ColorEntry.GLOBAL_ENTRY_INVALID, new ColorThemeEntry(Colors.COLORBLIND_MAGENTA));
		colors.put(ColorEntry.GLOBAL_ENTRY_WARNING, new ColorThemeEntry(Colors.COLORBLIND_ORANGE));
		colors.put(ColorEntry.GLOBAL_ENTRY_VALID, new ColorThemeEntry(Colors.COLORBLIND_YELLOW));
		colors.put(ColorEntry.GLOBAL_GRAND_TOTAL, new ColorThemeEntry(Colors.COLORBLIND_GRAY));
		colors.put(ColorEntry.GLOBAL_SELECTED_ROW_HIGHLIGHTING, new ColorThemeEntry(Colors.COLORBLIND_BLUE));
		colors.put(ColorEntry.FILTER_OR_GROUP_1, new ColorThemeEntry(Colors.COLORBLIND_GRAY));
		colors.put(ColorEntry.FILTER_OR_GROUP_2, new ColorThemeEntry(Colors.COLORBLIND_MAGENTA));
		colors.put(ColorEntry.FILTER_OR_GROUP_3, new ColorThemeEntry(Colors.COLORBLIND_GREEN));
		colors.put(ColorEntry.FILTER_OR_GROUP_4, new ColorThemeEntry(Colors.COLORBLIND_YELLOW));
		colors.put(ColorEntry.FILTER_OR_GROUP_5, new ColorThemeEntry(Colors.COLORBLIND_BLUE));
		colors.put(ColorEntry.FILTER_OR_GROUP_6, new ColorThemeEntry(Colors.COLORBLIND_ORANGE));
		colors.put(ColorEntry.FILTER_OR_GROUP_7, new ColorThemeEntry(Colors.COLORBLIND_GRAY));
		colors.put(ColorEntry.REPROCESSED_SELL, new ColorThemeEntry(Colors.COLORBLIND_YELLOW));
		colors.put(ColorEntry.REPROCESSED_REPROCESS, new ColorThemeEntry(Colors.COLORBLIND_MAGENTA));
		colors.put(ColorEntry.REPROCESSED_EQUAL, new ColorThemeEntry(Colors.COLORBLIND_GRAY));
		colors.put(ColorEntry.CONTAINER_LOG_ADDED, new ColorThemeEntry(Colors.COLORBLIND_YELLOW));
		colors.put(ColorEntry.CONTAINER_LOG_REMOVED, new ColorThemeEntry(Colors.COLORBLIND_MAGENTA));
		colors.put(ColorEntry.CONTAINER_LOG_MOVED, new ColorThemeEntry(Colors.COLORBLIND_BLUE));
		colors.put(ColorEntry.CONTAINER_LOG_MULTIPLE_CHANGES, new ColorThemeEntry(Colors.COLORBLIND_ORANGE));
	}
}

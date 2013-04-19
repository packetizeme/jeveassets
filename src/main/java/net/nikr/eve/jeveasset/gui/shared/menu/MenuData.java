/*
 * Copyright 2009-2013 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.shared.menu;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JComponent;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.*;
import net.nikr.eve.jeveasset.data.types.BlueprintType;
import net.nikr.eve.jeveasset.data.types.ItemType;
import net.nikr.eve.jeveasset.data.types.LocationType;
import net.nikr.eve.jeveasset.data.types.PriceType;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class MenuData<T> {

	private final Set<Integer> typeIDs = new HashSet<Integer>();
	private final Map<Integer, Double> prices = new HashMap<Integer, Double>();
	private final Set<String> typeNames = new HashSet<String>();
	private final Set<String> stations = new HashSet<String>();
	private final Set<String> systems = new HashSet<String>();
	private final Set<String> regions = new HashSet<String>();
	private final Set<Integer> marketTypeIDs = new HashSet<Integer>();
	private final Set<Integer> blueprintTypeIDs = new HashSet<Integer>();
	private boolean priceSupported = false;
	private boolean itemSupported = false;
	private boolean locationSupported = false;
	private boolean assets = false;
	private boolean stockpile = false;

	public MenuData(final List<T> items, final Settings settings, final Class<T> clazz) {
		if (items == null) { //Skip null
			return;
		}

		assets = Asset.class.isAssignableFrom(clazz);
		stockpile = StockpileItem.class.isAssignableFrom(clazz);
		locationSupported = LocationType.class.isAssignableFrom(clazz);
		itemSupported = ItemType.class.isAssignableFrom(clazz);
		priceSupported = PriceType.class.isAssignableFrom(clazz) || Item.class.isAssignableFrom(clazz);

		for (T t : items) {
			if (t == null) { //Skip null
				continue;
			}

			Location location = null;
			if (t instanceof LocationType) {
				LocationType type = (LocationType) t;
				location = type.getLocation();
			}

			Item itemType = null;
			if (t instanceof ItemType) {
				ItemType type = (ItemType) t;
				itemType = type.getItem();
			}

			BlueprintType blueprint = null;
			if (t instanceof BlueprintType) {
				blueprint = (BlueprintType) t;
			}

			Double price = null;
			if (t instanceof PriceType) {
				PriceType priceType = (PriceType) t;
				price = priceType.getDynamicPrice();
			}

			if (t instanceof Item) {
				Item item = (Item) t;
				if (items.size() == 1) { //Always zero for multiple items
					price = ApiIdConverter.getPrice(item.getTypeID(), false, settings.getUserPrices(), settings.getPriceData());
				}
				if (price == null || price == 0) {
					price = (double) item.getPriceBase();
				}
			}

			add(itemType, location, price, blueprint);
		}
	}

	public boolean addAssetFilter(Program program, JComponent jComponent) {
		if (!assets && (itemSupported || locationSupported)) {
			jComponent.add(new JMenuAssetFilter<T>(program, this));
			return true;
		}
		return false;
	}
	public boolean addStockpile(Program program, JComponent jComponent) {
		if (!stockpile && itemSupported) {
			jComponent.add(new JMenuStockpile<T>(program, this));
			return true;
		}
		return false;
	}

	public boolean addLookup(Program program, JComponent jComponent) {
		if (itemSupported || locationSupported) {
			jComponent.add(new JMenuLookup<T>(program, this));
			return true;
		}
		return false;
	}

	public boolean addPrice(Program program, JComponent jComponent) {
		if (priceSupported) {
			jComponent.add(new JMenuPrice<T>(program, this));
			return true;
		}
		return false;
	}

	public boolean addReprocessed(Program program, JComponent jComponent) {
		if (itemSupported) {
			jComponent.add(new JMenuReprocessed<T>(program, this));
			return true;
		}
		return false;
	}

	private void add(final Item item, final Location location, final Double price, final BlueprintType blueprintType) {
		if (item != null && !item.isEmpty()) {
			//Type Name
			typeNames.add(item.getTypeName());
			//TypeID
			int typeID = item.getTypeID();
			typeIDs.add(typeID);
			
			//Market TypeID
			if (item.isMarketGroup()) {
				marketTypeIDs.add(typeID);
			}
			//Blueprint TypeID
			int blueprintTypeID;
			if (blueprintType != null && blueprintType.isBPC()) {
				blueprintTypeID = -typeID;
			} else {
				blueprintTypeID = typeID;
			}
			blueprintTypeIDs.add(blueprintTypeID);
			//Price TypeID
			if (price != null) { //Not unique
				prices.put(blueprintTypeID, price);
			}
		}
		//Locations
		if (location != null && !location.isEmpty()) {
			if (location.isStation()) {
				stations.add(location.getStation());
			}
			if (location.isStation() || location.isSystem()) {
				systems.add(location.getSystem());
			}
			if (location.isStation() || location.isSystem() || location.isRegion()) {
				regions.add(location.getRegion());
			}
		}
	}

	public Set<Integer> getTypeIDs() {
		return typeIDs;
	}

	public Map<Integer, Double> getPrices() {
		return prices;
	}

	public Set<String> getTypeNames() {
		return typeNames;
	}

	public Set<String> getStations() {
		return stations;
	}

	public Set<String> getSystems() {
		return systems;
	}

	public Set<String> getRegions() {
		return regions;
	}

	public Set<Integer> getMarketTypeIDs() {
		return marketTypeIDs;
	}

	public Set<Integer> getBlueprintTypeIDs() {
		return blueprintTypeIDs;
	}
}

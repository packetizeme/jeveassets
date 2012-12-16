/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.dialogs.update;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.i18n.DialoguesUpdate;
import net.nikr.eve.jeveasset.io.eveapi.*;


public class UpdateDialog extends JDialogCentered implements ActionListener {


	private static final String ACTION_CANCEL = "ACTION_CANCEL";
	private static final String ACTION_UPDATE = "ACTION_UPDATE";
	private static final String ACTION_CHANGED = "ACTION_CHANGED";
	private static final String ACTION_CHECK_ALL = "ACTION_CHECK_ALL";

	private JCheckBox jCheckAll;
	private JCheckBox jMarketOrders;
	private JLabel jMarketOrdersUpdate;
	private JCheckBox jIndustryJobs;
	private JLabel jIndustryJobsUpdate;
	private JCheckBox jAccounts;
	private JLabel jAccountsUpdate;
	private JCheckBox jAccountBalance;
	private JLabel jAccountBalanceUpdate;
	private JCheckBox jContracts;
	private JLabel jContractsUpdate;
	private JCheckBox jAssets;
	private JLabel jAssetsUpdate;
	private JCheckBox jPriceData;
	private JLabel jPriceDataUpdate;
	private JButton jUpdate;
	private JButton jCancel;
	private List<JCheckBox> jCheckBoxes = new ArrayList<JCheckBox>();

	public UpdateDialog(final Program program) {
		super(program, DialoguesUpdate.get().update(), Images.DIALOG_UPDATE.getImage());

		jCheckAll = new JCheckBox(DialoguesUpdate.get().all());
		jCheckAll.setActionCommand(ACTION_CHECK_ALL);
		jCheckAll.addActionListener(this);

		jMarketOrders = new JCheckBox(DialoguesUpdate.get().marketOrders());
		jIndustryJobs = new JCheckBox(DialoguesUpdate.get().industryJobs());
		jAccounts = new JCheckBox(DialoguesUpdate.get().accounts());
		jAccountBalance = new JCheckBox(DialoguesUpdate.get().accountBlances());
		jContracts = new JCheckBox(DialoguesUpdate.get().contracts());
		jAssets = new JCheckBox(DialoguesUpdate.get().assets());
		jPriceData = new JCheckBox(DialoguesUpdate.get().priceData());

		jCheckBoxes.add(jMarketOrders);
		jCheckBoxes.add(jIndustryJobs);
		jCheckBoxes.add(jAccounts);
		jCheckBoxes.add(jAccountBalance);
		jCheckBoxes.add(jContracts);
		jCheckBoxes.add(jAssets);
		jCheckBoxes.add(jPriceData);
		for (JCheckBox jCheckBox : jCheckBoxes) {
			jCheckBox.setActionCommand(ACTION_CHANGED);
			jCheckBox.addActionListener(this);
		}
		JLabel jNextUpdateLabel = new JLabel(DialoguesUpdate.get().nextUpdate());
		jMarketOrdersUpdate = new JLabel();
		jIndustryJobsUpdate = new JLabel();
		jAccountsUpdate = new JLabel();
		jAccountBalanceUpdate = new JLabel();
		jContractsUpdate = new JLabel();
		jAssetsUpdate = new JLabel();
		jPriceDataUpdate = new JLabel();

		jUpdate = new JButton(DialoguesUpdate.get().update());
		jUpdate.setActionCommand(ACTION_UPDATE);
		jUpdate.addActionListener(this);

		jCancel = new JButton(DialoguesUpdate.get().cancel());
		jCancel.setActionCommand(ACTION_CANCEL);
		jCancel.addActionListener(this);

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup()
						.addComponent(jCheckAll)
						.addComponent(jMarketOrders)
						.addComponent(jIndustryJobs)
						.addComponent(jAccounts)
						.addComponent(jAccountBalance)
						.addComponent(jContracts)
						.addComponent(jAssets)
						.addComponent(jPriceData)
					)
					.addGroup(layout.createParallelGroup()
						.addComponent(jNextUpdateLabel, 110, 110, 110)
						.addComponent(jMarketOrdersUpdate)
						.addComponent(jIndustryJobsUpdate)
						.addComponent(jAccountsUpdate)
						.addComponent(jAccountBalanceUpdate)
						.addComponent(jContractsUpdate)
						.addComponent(jAssetsUpdate)
						.addComponent(jPriceDataUpdate)
					)
				)
				.addGroup(Alignment.TRAILING, layout.createSequentialGroup()
					.addComponent(jUpdate, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jCancel, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				)

		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jCheckAll, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jNextUpdateLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jMarketOrders, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jMarketOrdersUpdate, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jIndustryJobs, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jIndustryJobsUpdate, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jAccounts, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jAccountsUpdate, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jAccountBalance, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jAccountBalanceUpdate, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jContracts, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jContractsUpdate, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jAssets, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jAssetsUpdate, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jPriceData, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jPriceDataUpdate, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGap(30)
				.addGroup(layout.createParallelGroup()
					//.addComponent(jCheckAll, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jUpdate, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCancel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
		);
	}

	private void changed() {
		boolean allChecked = true;
		boolean noneChecked = false;
		boolean allDiabled = true;
		for (JCheckBox jCheckBox : jCheckBoxes) {
			if (jCheckBox.isEnabled() && !jCheckBox.isSelected()) {
				allChecked = false;
			}
			if (jCheckBox.isEnabled() && jCheckBox.isSelected()) {
				noneChecked = true;
			}
			if (jCheckBox.isEnabled()) {
				allDiabled = false;
			}

		}
		jUpdate.setEnabled(noneChecked);
		jCheckAll.setSelected(allChecked && !allDiabled);
	}

	public void update() {
		List<Account> accounts = program.getSettings().getAccounts();
		Date accountsNextUpdate = null;
		Date industryJobsNextUpdate = null;
		Date marketOrdersNextUpdate = null;
		Date contractsNextUpdate = null;
		Date assetsNextUpdate = null;
		Date accountBalanceNextUpdate = null;

		boolean bAccountsUpdateAll = true;
		boolean bIndustryJobsUpdateAll = true;
		boolean bMarketOrdersUpdateAll = true;
		boolean bContractsUpdateAll = true;
		boolean bAssetsUpdateAll = true;
		boolean bAccountBalanceUpdateAll = true;

		Date priceDataNextUpdate = program.getSettings().getPriceDataNextUpdate();
		for (int a = 0; a < accounts.size(); a++) {
			Account account = accounts.get(a);
			//Account
			accountsNextUpdate = nextUpdate(accountsNextUpdate, account.getAccountNextUpdate());
			bAccountsUpdateAll = updateAll(bAccountsUpdateAll, accountsNextUpdate);
			List<Human> humans = account.getHumans();
			for (int b = 0; b < humans.size(); b++) {
				Human human = humans.get(b);
				if (human.isShowAssets()) {
					industryJobsNextUpdate = nextUpdate(industryJobsNextUpdate, human.getIndustryJobsNextUpdate());
					marketOrdersNextUpdate = nextUpdate(marketOrdersNextUpdate, human.getMarketOrdersNextUpdate());
					contractsNextUpdate = nextUpdate(contractsNextUpdate, human.getContractsNextUpdate());
					assetsNextUpdate = nextUpdate(assetsNextUpdate, human.getAssetNextUpdate());
					accountBalanceNextUpdate = nextUpdate(accountBalanceNextUpdate, human.getBalanceNextUpdate());
					bIndustryJobsUpdateAll = updateAll(bIndustryJobsUpdateAll, human.getIndustryJobsNextUpdate());
					bMarketOrdersUpdateAll = updateAll(bMarketOrdersUpdateAll, human.getMarketOrdersNextUpdate());
					bContractsUpdateAll = updateAll(bContractsUpdateAll, human.getContractsNextUpdate());
					bAssetsUpdateAll = updateAll(bAssetsUpdateAll, human.getAssetNextUpdate());
					bAccountBalanceUpdateAll = updateAll(bAccountBalanceUpdateAll, human.getBalanceNextUpdate());

				}
			}
		}
		setUpdateLabel(jMarketOrdersUpdate, jMarketOrders, marketOrdersNextUpdate, bMarketOrdersUpdateAll);
		setUpdateLabel(jIndustryJobsUpdate, jIndustryJobs, industryJobsNextUpdate, bIndustryJobsUpdateAll);
		setUpdateLabel(jAccountsUpdate, jAccounts, accountsNextUpdate, bAccountsUpdateAll);
		setUpdateLabel(jAccountBalanceUpdate, jAccountBalance, accountBalanceNextUpdate, bAccountBalanceUpdateAll);
		setUpdateLabel(jContractsUpdate, jContracts, contractsNextUpdate, bContractsUpdateAll);
		setUpdateLabel(jAssetsUpdate, jAssets, assetsNextUpdate, bAssetsUpdateAll);
		setUpdateLabel(jPriceDataUpdate, jPriceData, priceDataNextUpdate, true, false);
		changed();
		jUpdate.setEnabled(false);
		jCheckAll.setEnabled(false);
		setUpdatableButton(marketOrdersNextUpdate);
		setUpdatableButton(industryJobsNextUpdate);
		setUpdatableButton(accountsNextUpdate);
		setUpdatableButton(accountBalanceNextUpdate);
		setUpdatableButton(contractsNextUpdate);
		setUpdatableButton(assetsNextUpdate);
		setUpdatableButton(priceDataNextUpdate, false);
	}

	private void setUpdateLabel(final JLabel jLabel, final JCheckBox jCheckBox, final Date nextUpdate, final boolean updateAll) {
		this.setUpdateLabel(jLabel, jCheckBox, nextUpdate, updateAll, true);
	}

	private void setUpdateLabel(final JLabel jLabel, final JCheckBox jCheckBox, Date nextUpdate, final boolean updateAll, final boolean ignoreOnProxy) {
		if (nextUpdate == null) {
			nextUpdate = Settings.getNow();
		}
		if (program.getSettings().isUpdatable(nextUpdate, ignoreOnProxy)) {
			if (updateAll) {
				jLabel.setText(DialoguesUpdate.get().nowAll());
			} else {
				jLabel.setText(DialoguesUpdate.get().nowSome());
			}
			jCheckBox.setSelected(true);
			jCheckBox.setEnabled(true);
		} else {
			jLabel.setText(Formater.weekdayAndTime(nextUpdate));
			jCheckBox.setSelected(false);
			jCheckBox.setEnabled(false);
		}
	}

	private void setUpdatableButton(final Date nextUpdate) {
		setUpdatableButton(nextUpdate, true);
	}

	private void setUpdatableButton(Date nextUpdate, final boolean ignoreOnProxy) {
		if (nextUpdate == null) {
			nextUpdate = Settings.getNow();
		}
		if (program.getSettings().isUpdatable(nextUpdate, ignoreOnProxy)) {
			jUpdate.setEnabled(true);
			jCheckAll.setEnabled(true);
		}
	}

	private Date nextUpdate(Date nextUpdate, Date thisUpdate) {
		if (nextUpdate == null) {
				nextUpdate = thisUpdate;
		}
		if (thisUpdate.before(nextUpdate)) {
			nextUpdate = thisUpdate;
		}
		return nextUpdate;
	}

	private boolean updateAll(final boolean updateAll, final Date nextUpdate) {
		return updateAll && program.getSettings().isUpdatable(nextUpdate, true);
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jUpdate;
	}

	@Override
	protected JButton getDefaultButton() {
		return jUpdate;
	}

	@Override
	protected void windowShown() {
		update();
	}

	@Override
	protected void save() {

	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (ACTION_UPDATE.equals(e.getActionCommand())) {
			this.setVisible(false);
			List<UpdateTask> updateTasks = new ArrayList<UpdateTask>();
			if (jMarketOrders.isSelected() || jIndustryJobs.isSelected()
					|| jAssets.isSelected() || jContracts.isSelected()) {
				updateTasks.add(new ConquerableStationsTask()); //Should properly always be first
			}
			if (jAccounts.isSelected()) {
				updateTasks.add(new AccountsTask());
			}
			if (jMarketOrders.isSelected()) {
				updateTasks.add(new MarketOrdersTask());
			}
			if (jIndustryJobs.isSelected()) {
				updateTasks.add(new IndustryJobsTask());
			}
			if (jAccountBalance.isSelected()) {
				updateTasks.add(new BalanceTask());
			}
			if (jContracts.isSelected()) {
				updateTasks.add(new ContractsTask());
				updateTasks.add(new ContractItemsTask());
			}
			if (jAssets.isSelected()) {
				updateTasks.add(new AssetsTask());
			}
			if (jPriceData.isSelected()
					|| jMarketOrders.isSelected()
					|| jIndustryJobs.isSelected()
					|| jAssets.isSelected()
					) {
				updateTasks.add(new PriceDataTask(jPriceData.isSelected()));
			}
			if (!updateTasks.isEmpty()) {
				TaskDialog taskDialog = new TaskDialog(program, updateTasks);
			}
		}
		if (ACTION_CANCEL.equals(e.getActionCommand())) {
			setVisible(false);
		}
		if (ACTION_CHANGED.equals(e.getActionCommand())) {
			changed();
		}
		if (ACTION_CHECK_ALL.equals(e.getActionCommand())) {
			boolean checked = jCheckAll.isSelected();
			for (JCheckBox jCheckBox : jCheckBoxes) {
				if (jCheckBox.isEnabled()) {
					jCheckBox.setSelected(checked);
				}
			}
			changed();
		}
	}

	public class ConquerableStationsTask extends UpdateTask {

		public ConquerableStationsTask() {
			super(DialoguesUpdate.get().conqStations());
		}

		@Override
		public void update() {
			ConquerableStationsGetter conquerableStationsGetter = new ConquerableStationsGetter();
			conquerableStationsGetter.load(this, program.getSettings());
		}
	}

	public class AccountsTask extends UpdateTask {

		public AccountsTask() {
			super(DialoguesUpdate.get().accounts());
		}

		@Override
		public void update() {
			HumansGetter humansGetter = new HumansGetter();
			humansGetter.load(this, program.getSettings().isForceUpdate(), program.getSettings().getAccounts());
		}
	}

	public class AssetsTask extends UpdateTask {

		public AssetsTask() {
			super(DialoguesUpdate.get().assets());
		}

		@Override
		public void update() {
			AssetsGetter assetsGetter = new AssetsGetter();
			assetsGetter.load(this, program.getSettings());
		}
	}

	public class BalanceTask extends UpdateTask {

		public BalanceTask() {
			super(DialoguesUpdate.get().balance());
		}

		@Override
		public void update() {
			AccountBalanceGetter accountBalanceGetter = new AccountBalanceGetter();
			accountBalanceGetter.load(this, program.getSettings().isForceUpdate(), program.getSettings().getAccounts());
		}
	}

	public class IndustryJobsTask extends UpdateTask {

		public IndustryJobsTask() {
			super(DialoguesUpdate.get().industryJobs());
		}

		@Override
		public void update() {
			IndustryJobsGetter industryJobsGetter = new IndustryJobsGetter();
			industryJobsGetter.load(this, program.getSettings().isForceUpdate(), program.getSettings().getAccounts());
		}
	}

	public class MarketOrdersTask extends UpdateTask {

		public MarketOrdersTask() {
			super(DialoguesUpdate.get().marketOrders());
		}

		@Override
		public void update() {
			MarketOrdersGetter marketOrdersGetter = new MarketOrdersGetter();
			marketOrdersGetter.load(this, program.getSettings().isForceUpdate(), program.getSettings().getAccounts());
		}
	}

	public class ContractsTask extends UpdateTask {

		public ContractsTask() {
			super(DialoguesUpdate.get().contracts());
		}

		@Override
		public void update() {
			ContractsGetter contractsGetter = new ContractsGetter();
			contractsGetter.load(this, program.getSettings().isForceUpdate(), program.getSettings().getAccounts());
		}
	}

	public class ContractItemsTask extends UpdateTask {

		public ContractItemsTask() {
			super(DialoguesUpdate.get().contractItem());
		}

		@Override
		public void update() {
			ContractItemsGetter itemsGetter = new ContractItemsGetter();
			itemsGetter.load(this, program.getSettings().isForceUpdate(), program.getSettings().getAccounts());
		}
	}

	public class PriceDataTask extends UpdateTask {
		private boolean update;

		public PriceDataTask(final boolean update) {
			super(DialoguesUpdate.get().priceData() + " (" + (program.getSettings().getPriceDataSettings().getSource().toString()) + ")");
			this.update = update;
		}

		@Override
		public void update() {
			program.getSettings().clearEveAssetList();
			if (update) {
				program.getSettings().getPriceDataGetter().update(this);
			} else {
				program.getSettings().getPriceDataGetter().load(this);
			}
		}
	}
}

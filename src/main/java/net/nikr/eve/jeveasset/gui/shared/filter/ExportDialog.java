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

package net.nikr.eve.jeveasset.gui.shared.filter;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.matchers.Matcher;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.ExportSettings.ColumnSelection;
import net.nikr.eve.jeveasset.data.settings.ExportSettings.DecimalSeparator;
import net.nikr.eve.jeveasset.data.settings.ExportSettings.ExportFormat;
import net.nikr.eve.jeveasset.data.settings.ExportSettings.FieldDelimiter;
import net.nikr.eve.jeveasset.data.settings.ExportSettings.FilterSelection;
import net.nikr.eve.jeveasset.data.settings.ExportSettings.LineDelimiter;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.tag.Tags;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.CaseInsensitiveComparator;
import net.nikr.eve.jeveasset.gui.shared.DocumentFactory;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JCustomFileChooser;
import net.nikr.eve.jeveasset.gui.shared.components.JDefaultField;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.components.JMultiSelectionList;
import net.nikr.eve.jeveasset.gui.shared.components.ListComboBoxModel;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor.SimpleColumn;
import net.nikr.eve.jeveasset.gui.shared.table.View;
import net.nikr.eve.jeveasset.gui.shared.table.containers.HierarchyColumn;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeAsset;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeTab;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeTab.AssetTreeComparator;
import net.nikr.eve.jeveasset.i18n.DialoguesExport;
import net.nikr.eve.jeveasset.io.local.CsvWriter;
import net.nikr.eve.jeveasset.io.local.HtmlWriter;
import net.nikr.eve.jeveasset.io.local.SqlWriter;
import org.supercsv.prefs.CsvPreference;


public class ExportDialog<E> extends JDialogCentered {

	private enum ExportAction {
		OK,
		CANCEL,
		DEFAULT,
		FILTER_CHANGED,
		VIEW_CHANGED,
		FORMAT_CHANGED
	}

	//Filter
	private final JRadioButton jNoFilter;
	private final JRadioButton jSavedFilter;
	private final JRadioButton jCurrentFilter;
	private final JComboBox<String> jFilters;
	//Columns
	private final JRadioButton jViewCurrent;
	private final JRadioButton jViewSelect;
	private final JRadioButton jViewSaved;
	private final JComboBox<String> jViews;
	private final JMultiSelectionList<EnumTableColumn<E>> jColumnSelection;
	private final JButton jViewSelectAll;
	//Format
	private final JRadioButton jCsv;
	private final JRadioButton jHtml;
	private final JRadioButton jSql;
	//Options
	private final CardLayout cardLayout;
	private final JPanel jOptionPanel;
	//CSV
	private final JComboBox<FieldDelimiter> jFieldDelimiter;
	private final JComboBox<LineDelimiter> jLineDelimiter;
	private final JComboBox<DecimalSeparator> jDecimalSeparator;
	//Html
	private final JCheckBox jHtmlStyle;
	private final JCheckBox jHtmlIGB;
	private final JSlider jHtmlHeaderRepeat;
	//SQL
	private final JTextField jTableName;
	private final JCheckBox jDropTable;
	private final JCheckBox jCreateTable;
	private final JCheckBox jExtendedInserts;

	private final JButton jOK;

	private static final DecimalFormat HTML_EN_NUMBER_FORMAT  = new DecimalFormat("#,##0.####", new DecimalFormatSymbols(new Locale("en")));
	private static final DecimalFormat HTML_EU_NUMBER_FORMAT  = new DecimalFormat("#,##0.####", new DecimalFormatSymbols(new Locale("da")));

	private final JCustomFileChooser jFileChooser;

	private final List<EventList<E>> eventLists;
	private final Map<String, EnumTableColumn<E>> columns = new HashMap<>();
	private final List<EnumTableColumn<E>> columnIndex = new ArrayList<>();
	private final FilterControl<E> filterControl;
	private final ExportFilterControl<E> exportFilterControl;
	private final String toolName;

	public ExportDialog(final JFrame jFrame, final String toolName, final FilterControl<E> filterControl, ExportFilterControl<E> exportFilterControl, final List<EventList<E>> eventLists, final List<EnumTableColumn<E>> enumColumns) {
		super(null, DialoguesExport.get().export(), jFrame, Images.DIALOG_CSV_EXPORT.getImage());
		this.toolName = toolName;
		this.filterControl = filterControl;
		this.exportFilterControl = exportFilterControl;
		this.eventLists = eventLists;

		ListenerClass listener = new ListenerClass();
		layout.setAutoCreateContainerGaps(false);

		jFileChooser = JCustomFileChooser.createFileChooser(jFrame, Settings.get().getExportSettings(toolName).getExportFormat().getExtension());
	//Format
		JPanel jFormatPanel = new JPanel();
		jFormatPanel.setBorder(BorderFactory.createTitledBorder(DialoguesExport.get().format()));
		GroupLayout formatLayout = new GroupLayout(jFormatPanel);
		jFormatPanel.setLayout(formatLayout);
		formatLayout.setAutoCreateGaps(true);
		formatLayout.setAutoCreateContainerGaps(true);

		jCsv = new JRadioButton(DialoguesExport.get().csv());
		jCsv.setActionCommand(ExportAction.FORMAT_CHANGED.name());
		jCsv.addActionListener(listener);

		jHtml = new JRadioButton(DialoguesExport.get().html());
		jHtml.setActionCommand(ExportAction.FORMAT_CHANGED.name());
		jHtml.addActionListener(listener);

		jSql = new JRadioButton(DialoguesExport.get().sql());
		jSql.setActionCommand(ExportAction.FORMAT_CHANGED.name());
		jSql.addActionListener(listener);

		ButtonGroup jFormatButtonGroup = new ButtonGroup();
		jFormatButtonGroup.add(jCsv);
		jFormatButtonGroup.add(jHtml);
		jFormatButtonGroup.add(jSql);

		formatLayout.setHorizontalGroup(
			formatLayout.createSequentialGroup()
					.addComponent(jCsv)
					.addComponent(jHtml)
					.addComponent(jSql)
		);
		formatLayout.setVerticalGroup(
			formatLayout.createParallelGroup()
					.addComponent(jCsv, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jHtml, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jSql, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
		);
	//Options
		cardLayout = new CardLayout();
		jOptionPanel = new JPanel(cardLayout);
	//Csv
		JOptionPanel jCsvPanel = new JOptionPanel();
		jOptionPanel.add(jCsvPanel, ExportFormat.CSV.name());

		JLabel jFieldDelimiterLabel = new JLabel(DialoguesExport.get().fieldTerminated());
		jFieldDelimiter = new JComboBox<>(FieldDelimiter.values());
		jCsvPanel.add(jFieldDelimiterLabel);
		jCsvPanel.add(jFieldDelimiter);

		JLabel jLineDelimiterLabel = new JLabel(DialoguesExport.get().linesTerminated());
		jLineDelimiter = new JComboBox<>(LineDelimiter.values());
		jCsvPanel.add(jLineDelimiterLabel);
		jCsvPanel.add(jLineDelimiter);

		//FIXME - - > ExportDialog: DecimalSeparator also used by HTML export...
		JLabel jDecimalSeparatorLabel = new JLabel(DialoguesExport.get().decimalSeparator());
		jDecimalSeparator = new JComboBox<>(DecimalSeparator.values());
		jCsvPanel.add(jDecimalSeparatorLabel);
		jCsvPanel.add(jDecimalSeparator);
	//Sql
		JOptionPanel jSqlPanel = new JOptionPanel();
		jOptionPanel.add(jSqlPanel, ExportFormat.SQL.name());

		JLabel jTableNameLabel = new JLabel(DialoguesExport.get().tableName());
		jTableName = new JDefaultField(Program.PROGRAM_NAME.toLowerCase() + "_" + toolName.toLowerCase());
		jTableName.setDocument(DocumentFactory.getWordPlainDocument());
		jSqlPanel.add(jTableNameLabel);
		jSqlPanel.add(jTableName);

		jDropTable = new JCheckBox(DialoguesExport.get().dropTable());
		jSqlPanel.add(jDropTable);

		jCreateTable = new JCheckBox(DialoguesExport.get().createTable());
		jSqlPanel.add(jCreateTable);

		jExtendedInserts = new JCheckBox(DialoguesExport.get().extendedInserts());
		jSqlPanel.add(jExtendedInserts);
	//Html
		JOptionPanel jHtmlPanel = new JOptionPanel();
		jOptionPanel.add(jHtmlPanel, ExportFormat.HTML.name());

		jHtmlStyle = new JCheckBox(DialoguesExport.get().htmlStyled());
		jHtmlPanel.add(jHtmlStyle);

		jHtmlIGB = new JCheckBox(DialoguesExport.get().htmlIGB());
		jHtmlPanel.add(jHtmlIGB);

		JLabel jHtmlHeaderRepeatLabel = new JLabel(DialoguesExport.get().htmlHeaderRepeat());
		jHtmlHeaderRepeat = new JSlider(JSlider.HORIZONTAL, 0, 50, 0);
		jHtmlHeaderRepeat.setMajorTickSpacing(10);
		jHtmlHeaderRepeat.setMinorTickSpacing(5);
		jHtmlHeaderRepeat.setSnapToTicks(true);
		jHtmlHeaderRepeat.setPaintTicks(true);
		jHtmlHeaderRepeat.setPaintLabels(true);
		jHtmlPanel.add(jHtmlHeaderRepeatLabel);
		jHtmlPanel.add(jHtmlHeaderRepeat);

	//Filters
		JPanel jFilterPanel = new JPanel();
		jFilterPanel.setBorder(BorderFactory.createTitledBorder(DialoguesExport.get().filters()));
		GroupLayout filterLayout = new GroupLayout(jFilterPanel);
		jFilterPanel.setLayout(filterLayout);
		filterLayout.setAutoCreateGaps(true);
		filterLayout.setAutoCreateContainerGaps(true);

		jNoFilter = new JRadioButton(DialoguesExport.get().noFilter());
		jNoFilter.setActionCommand(ExportAction.FILTER_CHANGED.name());
		jNoFilter.addActionListener(listener);
		jNoFilter.setSelected(true);

		jCurrentFilter = new JRadioButton(DialoguesExport.get().currentFilter());
		jCurrentFilter.setActionCommand(ExportAction.FILTER_CHANGED.name());
		jCurrentFilter.addActionListener(listener);

		jSavedFilter = new JRadioButton(DialoguesExport.get().savedFilter());
		jSavedFilter.setActionCommand(ExportAction.FILTER_CHANGED.name());
		jSavedFilter.addActionListener(listener);

		ButtonGroup jFilterButtonGroup = new ButtonGroup();
		jFilterButtonGroup.add(jNoFilter);
		jFilterButtonGroup.add(jSavedFilter);
		jFilterButtonGroup.add(jCurrentFilter);

		jFilters = new JComboBox<>();

		filterLayout.setHorizontalGroup(
			filterLayout.createParallelGroup()
					.addComponent(jNoFilter)
					.addComponent(jCurrentFilter)
					.addComponent(jSavedFilter)
					.addGroup(filterLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addGroup(filterLayout.createSequentialGroup()
							.addGap(20)
							.addComponent(jFilters, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
						)
					)
		);
		filterLayout.setVerticalGroup(
			filterLayout.createSequentialGroup()
					.addComponent(jNoFilter, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jCurrentFilter, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jSavedFilter, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jFilters, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
		);
	//Columns
		JPanel jColumnPanel = new JPanel();
		jColumnPanel.setBorder(BorderFactory.createTitledBorder(DialoguesExport.get().columns()));
		GroupLayout columnLayout = new GroupLayout(jColumnPanel);
		jColumnPanel.setLayout(columnLayout);
		columnLayout.setAutoCreateGaps(true);
		columnLayout.setAutoCreateContainerGaps(true);

		jViewCurrent = new JRadioButton(DialoguesExport.get().viewCurrent());
		jViewCurrent.setActionCommand(ExportAction.VIEW_CHANGED.name());
		jViewCurrent.addActionListener(listener);
		jViewCurrent.setSelected(true);

		jViewSaved = new JRadioButton(DialoguesExport.get().viewSaved());
		jViewSaved.setActionCommand(ExportAction.VIEW_CHANGED.name());
		jViewSaved.addActionListener(listener);

		jViews = new JComboBox<>();

		jViewSelect = new JRadioButton(DialoguesExport.get().viewSelect());
		jViewSelect.setActionCommand(ExportAction.VIEW_CHANGED.name());
		jViewSelect.addActionListener(listener);

		columnIndex.addAll(enumColumns);
		for (EnumTableColumn<E> column : enumColumns) {
			columns.put(column.name(), column);
		}

		jViewSelectAll = new JButton(DialoguesExport.get().viewSelectAll());
		jViewSelectAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jColumnSelection.toggleSelectAll();
			}
		});
		jViewSelectAll.setEnabled(false);

		jColumnSelection = new JMultiSelectionList<>(columnIndex);
		jColumnSelection.clearSelection();
		jColumnSelection.setEnabled(false);

		JScrollPane jColumnSelectionPanel = new JScrollPane(jColumnSelection);

		ButtonGroup jViewButtonGroup = new ButtonGroup();
		jViewButtonGroup.add(jViewCurrent);
		jViewButtonGroup.add(jViewSaved);
		jViewButtonGroup.add(jViewSelect);

		columnLayout.setHorizontalGroup(
			columnLayout.createParallelGroup()
					.addComponent(jViewCurrent)
					.addComponent(jViewSaved)
					.addComponent(jViewSelect)
					.addGroup(columnLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addGroup(columnLayout.createSequentialGroup()
							.addGap(20)
							.addGroup(columnLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
								.addComponent(jViews, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
								.addComponent(jViewSelectAll, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
								.addComponent(jColumnSelectionPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
								
							)
						)
					)
		);
		columnLayout.setVerticalGroup(
			columnLayout.createSequentialGroup()
					.addComponent(jViewCurrent, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jViewSaved, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jViews, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jViewSelect, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jColumnSelectionPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
					.addComponent(jViewSelectAll, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
		);
	//Buttons
		JSeparator jButtonSeparator = new JSeparator();

		jOK = new JButton(DialoguesExport.get().ok());
		jOK.setActionCommand(ExportAction.OK.name());
		jOK.addActionListener(listener);

		JButton jDefault = new JButton(DialoguesExport.get().defaultSettings());
		jDefault.setActionCommand(ExportAction.DEFAULT.name());
		jDefault.addActionListener(listener);

		JButton jCancel = new JButton(DialoguesExport.get().cancel());
		jCancel.setActionCommand(ExportAction.CANCEL.name());
		jCancel.addActionListener(listener);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addGroup(layout.createSequentialGroup()
					.addContainerGap()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						//Format
						.addComponent(jFormatPanel, 250, 250, 250)
						.addComponent(jOptionPanel, 250, 250, 250)
						.addComponent(jFilterPanel, 250, 250, 250)
					)
					.addGap(10)
					.addComponent(jColumnPanel, 250, 250, 250)
					.addContainerGap()
				)
				.addComponent(jButtonSeparator)
				.addGroup(layout.createSequentialGroup()
					.addContainerGap()
					.addComponent(jOK, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					.addComponent(jDefault, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					.addComponent(jCancel, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					.addContainerGap()
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup()
					.addGroup(layout.createSequentialGroup()
						.addComponent(jFormatPanel)
						.addComponent(jOptionPanel)
						.addComponent(jFilterPanel)
					)
					.addComponent(jColumnPanel)
				)
				.addComponent(jButtonSeparator, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addGroup(layout.createParallelGroup()
					.addComponent(jOK, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jDefault, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jCancel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addContainerGap()
		);
	}

	private void updateColumns() {
		final List<EnumTableColumn<E>> enumColumns = exportFilterControl.getColumns();
		columns.clear();
		columnIndex.clear();
		columnIndex.addAll(enumColumns);
		for (EnumTableColumn<E> column : enumColumns) {
			columns.put(column.name(), column);
		}
		jColumnSelection.setModel(new AbstractListModel<EnumTableColumn<E>>() {
			@Override
			public int getSize() {
				return columnIndex.size();
			}

			@Override
			public EnumTableColumn<E> getElementAt(int index) {
				return columnIndex.get(index);
			}
		});
	}

	private List<String> getExportColumns() {
		List<String> selectedColumns = new ArrayList<>();
		for (EnumTableColumn<E> column : jColumnSelection.getSelectedValuesList()) {
			selectedColumns.add(column.name());
		}
		return selectedColumns;
	}

	private boolean browse() {
		File file = new File(Settings.get().getExportSettings(toolName).getFilename());
		File path = new File(Settings.get().getExportSettings(toolName).getPath());
		if (path.exists()) {
			jFileChooser.setCurrentDirectory(path);
			jFileChooser.setSelectedFile(file);
		} else {
			jFileChooser.setCurrentDirectory(new File(Settings.get().getExportSettings(toolName).getDefaultPath()));
			jFileChooser.setSelectedFile(new File(Settings.get().getExportSettings(toolName).getDefaultFilename()));
		}
		int bFound = jFileChooser.showDialog(getDialog(), DialoguesExport.get().ok());
		if (bFound  == JFileChooser.APPROVE_OPTION) {
			file = jFileChooser.getSelectedFile();
			Settings.get().getExportSettings(toolName).setFilename(file.getAbsolutePath());
			return true;
		} else {
			return false;
		}
	}

	private String format(final Object object, final DecimalSeparator decimalSeparator, final boolean html) {
		if (object == null) {
			return "";
		} else if (object instanceof HierarchyColumn) {
			HierarchyColumn column = (HierarchyColumn) object;
			return column.getExport();
		} else if (object instanceof Number) {
			Number number = (Number) object;
			if (decimalSeparator == DecimalSeparator.DOT) {
				return HTML_EN_NUMBER_FORMAT.format(number);
			} else {
				return HTML_EU_NUMBER_FORMAT.format(number);
			}
		} else if (object instanceof Tags && html) {
			Tags tags = (Tags) object;
			return tags.getHtml();
		} else if (object instanceof Date) {
			return Formater.columnDate(object);
		} else {
			return object.toString();
		}
	}

	private void saveSettings() {
		Settings.lock("Export Settings (Save)"); //Lock for Export Settings (Save)
		//CSV
		Settings.get().getExportSettings(toolName).setFieldDelimiter((FieldDelimiter) jFieldDelimiter.getSelectedItem());
		Settings.get().getExportSettings(toolName).setLineDelimiter((LineDelimiter) jLineDelimiter.getSelectedItem());
		Settings.get().getExportSettings(toolName).setCsvDecimalSeparator((DecimalSeparator) jDecimalSeparator.getSelectedItem());
		//SQL
		Settings.get().getExportSettings(toolName).setTableName(jTableName.getText());
		Settings.get().getExportSettings(toolName).setDropTable(jDropTable.isSelected());
		Settings.get().getExportSettings(toolName).setCreateTable(jCreateTable.isSelected());
		Settings.get().getExportSettings(toolName).setExtendedInserts(jExtendedInserts.isSelected());
		//HTML
		Settings.get().getExportSettings(toolName).setHtmlStyled(jHtmlStyle.isSelected());
		Settings.get().getExportSettings(toolName).setHtmlIGB(jHtmlIGB.isSelected());
		Settings.get().getExportSettings(toolName).setHtmlRepeatHeader(jHtmlHeaderRepeat.getValue());
		//Shared
		if (jColumnSelection.getSelectedIndices().length == columns.size()) { //All is selected - nothing worth saving...
			Settings.get().getExportSettings(toolName).putTableExportColumns(null);
		} else {
			Settings.get().getExportSettings(toolName).putTableExportColumns(getExportColumns());
		}
		//Make sure there is a selected item and that it is not the filler text for empty list.
		if (jFilters.getSelectedItem() != null
				&& !DialoguesExport.get().noSavedFilter().equals(jFilters.getSelectedItem())) {
			Settings.get().getExportSettings(toolName).setFilterName((String) jFilters.getSelectedItem());
		} else  {
			Settings.get().getExportSettings(toolName).setFilterName(null);
		}
		//Make sure there is a selected item and that is is not filler text for empty list.
		if (jViews.getSelectedItem() != null
				&& !DialoguesExport.get().viewNoSaved().equals(jViews.getSelectedItem())) {
			Settings.get().getExportSettings(toolName).setViewName((String) jViews.getSelectedItem());
		} else  {
			Settings.get().getExportSettings(toolName).setViewName(null);
		}
		Settings.unlock("Export Settings (Save)"); //Unlock for Export Settings (Save)
		exportFilterControl.saveSettings("Export Settings (Save)");
	}

	private void loadSettings() {
		//CSV
		jFieldDelimiter.setSelectedItem(Settings.get().getExportSettings(toolName).getFieldDelimiter());
		jLineDelimiter.setSelectedItem(Settings.get().getExportSettings(toolName).getLineDelimiter());
		jDecimalSeparator.setSelectedItem(Settings.get().getExportSettings(toolName).getCsvDecimalSeparator());
		//SQL
		jTableName.setText(Settings.get().getExportSettings(toolName).getTableName());
		jDropTable.setSelected(Settings.get().getExportSettings(toolName).isDropTable());
		jCreateTable.setSelected(Settings.get().getExportSettings(toolName).isCreateTable());
		jExtendedInserts.setSelected(Settings.get().getExportSettings(toolName).isExtendedInserts());
		//HTML
		jHtmlStyle.setSelected(Settings.get().getExportSettings(toolName).isHtmlStyled());
		jHtmlIGB.setSelected(Settings.get().getExportSettings(toolName).isHtmlIGB());
		jHtmlHeaderRepeat.setValue(Settings.get().getExportSettings(toolName).getHtmlRepeatHeader());
		//Filename
		ExportFormat exportFormat = Settings.get().getExportSettings(toolName).getExportFormat();
		jFileChooser.setExtension(exportFormat.getExtension());
		//Columns (Shared)
		jColumnSelection.clearSelection();
		List<String> list = Settings.get().getExportSettings(toolName).getTableExportColumns();
		if (list == null) {
			jColumnSelection.clearSelection();
		} else {
			List<Integer> selections = new ArrayList<>();
			for (String column : list) {
				try {
					EnumTableColumn<E> enumColumn = exportFilterControl.valueOf(column);
					if (enumColumn != null) {
						int index = columnIndex.indexOf(enumColumn);
						selections.add(index);
					}
				} catch (IllegalArgumentException ex) {
					//ignore missing columns...
				}	
			}
			int[] indices = new int[selections.size()];
			for (int i = 0; i < selections.size(); i++) {
				indices[i] = selections.get(i);
			}
			jColumnSelection.setSelectedIndices(indices);
		}

		String filterName = Settings.get().getExportSettings(toolName).getFilterName();
		List<String> filterNames = new ArrayList<>(exportFilterControl.getAllFilters().keySet());
		if (!filterNames.isEmpty()) {
			Collections.sort(filterNames, new CaseInsensitiveComparator());
			jFilters.setModel(new ListComboBoxModel<>(filterNames));
			if(hasItem(jFilters, filterName)) {
				jFilters.setSelectedItem(filterName);
			}
		} else {
			jFilters.setModel(new ListComboBoxModel<>());
		}

		String viewName = Settings.get().getExportSettings(toolName).getViewName();
		List<String> viewNames = new ArrayList<>(Settings.get().getTableViews(toolName).keySet());
		if (!viewNames.isEmpty()) {
			Collections.sort(viewNames, new CaseInsensitiveComparator());
			jViews.setModel(new ListComboBoxModel<>(viewNames));
			if (hasItem(jViews, viewName)) {
				jViews.setSelectedItem(viewName);
			}
		} else {
			jViews.setModel(new ListComboBoxModel<>());
		}
		updateDisplayElements();
	}

	private void resetSettings() {
		Settings.lock("Export Settings (Reset)"); //Lock for Export Settings (Reset)
		//CSV
		Settings.get().getExportSettings(toolName).setFieldDelimiter(FieldDelimiter.COMMA);
		Settings.get().getExportSettings(toolName).setLineDelimiter(LineDelimiter.DOS);
		Settings.get().getExportSettings(toolName).setCsvDecimalSeparator(DecimalSeparator.DOT);
		//SQL
		Settings.get().getExportSettings(toolName).setTableName("");
		Settings.get().getExportSettings(toolName).setDropTable(true);
		Settings.get().getExportSettings(toolName).setCreateTable(true);
		Settings.get().getExportSettings(toolName).setExtendedInserts(true);
		//HTML
		Settings.get().getExportSettings(toolName).setHtmlStyled(true);
		Settings.get().getExportSettings(toolName).setHtmlRepeatHeader(0);
		//Shared
		Settings.get().getExportSettings(toolName).setFilename(Settings.get().getExportSettings(toolName).getDefaultFilename());
		Settings.get().getExportSettings(toolName).putTableExportColumns(null);
		Settings.get().getExportSettings(toolName).setExportFormat(ExportFormat.CSV);
		Settings.get().getExportSettings(toolName).setColumnSelection(ColumnSelection.SHOWN);
		Settings.get().getExportSettings(toolName).setFilterSelection(FilterSelection.NONE);
		Settings.get().getExportSettings(toolName).setViewName(null);
		Settings.get().getExportSettings(toolName).setFilterName(null);
		Settings.unlock("Export Settings (Reset)"); //Unlock for Export Settings (Reset)
		loadSettings();
	}

	/***
	 *Updates the UI elements that are displayed based on the current data. This will hide or show panels. Enable
	 *combo boxes. Etc.
	 */
	public void updateDisplayElements() {
		ExportFormat exportFormat = Settings.get().getExportSettings(toolName).getExportFormat();
		cardLayout.show(jOptionPanel, exportFormat.name());
		if (exportFormat == ExportFormat.HTML) {
			jHtml.setSelected(true);
			jOptionPanel.setBorder(BorderFactory.createTitledBorder(DialoguesExport.get().html()));
		} else if (exportFormat == ExportFormat.SQL) {
			jSql.setSelected(true);
			jOptionPanel.setBorder(BorderFactory.createTitledBorder(DialoguesExport.get().sql()));
		} else { //CSV and Default
			jCsv.setSelected(true);
			jOptionPanel.setBorder(BorderFactory.createTitledBorder(DialoguesExport.get().csv()));
		}

		FilterSelection filterSelection = Settings.get().getExportSettings(toolName).getFilterSelection();
		String filterName = Settings.get().getExportSettings(toolName).getFilterName();
		if (filterSelection == FilterSelection.NONE) {
			jNoFilter.setSelected(true);
			jFilters.setEnabled(false);
		} else if (filterSelection == FilterSelection.CURRENT && !exportFilterControl.getCurrentFilters().isEmpty()) {
			jCurrentFilter.setSelected(true);
			jFilters.setEnabled(false);
		} else if (filterSelection == FilterSelection.SAVED && hasItem(jFilters, filterName)) {
			jSavedFilter.setSelected(true);
			jFilters.setEnabled(true);
		} else {
			//If we got here then the view selection didn't match the data and we had to default, so reset the value
			//in the settings.
			jNoFilter.setSelected(true);
			jFilters.setEnabled(false);
			Settings.get().getExportSettings(toolName).setFilterSelection(FilterSelection.NONE);
		}

		//Filters current
		if (exportFilterControl.getCurrentFilters().isEmpty()) {
			jCurrentFilter.setEnabled(false);
		} else {
			jCurrentFilter.setEnabled(true);
		}

		//Filters saved
		if (exportFilterControl.getAllFilters().isEmpty()) {
			jSavedFilter.setEnabled(false);
			jFilters.getModel().setSelectedItem(DialoguesExport.get().noSavedFilter());
		} else {
			jSavedFilter.setEnabled(true);
		}

		ColumnSelection columnSelection = Settings.get().getExportSettings(toolName).getColumnSelection();
		String viewName = Settings.get().getExportSettings(toolName).getViewName();
		if (columnSelection == ColumnSelection.SHOWN) {
			jViewCurrent.setSelected(true);
			jViews.setEnabled(false);
			jColumnSelection.setEnabled(false);
			jViewSelectAll.setEnabled(false);
		} else if (columnSelection == ColumnSelection.SAVED && hasItem(jViews, viewName)) {
			jViewSaved.setSelected(true);
			jViews.setEnabled(true);
			jColumnSelection.setEnabled(false);
			jViewSelectAll.setEnabled(false);
		} else if (columnSelection == ColumnSelection.SELECTED) {
			jViewSelect.setSelected(true);
			jViews.setEnabled(false);
			jColumnSelection.setEnabled(true);
			jViewSelectAll.setEnabled(true);
		} else {
			//If we got here then the column selection didn't match the data and we had to default, so reset the value
			//in the settings.
			jViewCurrent.setSelected(true);
			jViews.setEnabled(false);
			Settings.get().getExportSettings(toolName).setColumnSelection(ColumnSelection.SHOWN);
		}

		//Views
		Map<String, View> tableViews = Settings.get().getTableViews(toolName);
		if (tableViews.isEmpty()) {
			jViewSaved.setEnabled(false);
			jViews.getModel().setSelectedItem(DialoguesExport.get().viewNoSaved());
		} else {
			jViewSaved.setEnabled(true);
		}
	}

	@Override
	public void setVisible(final boolean b) {
		if (b) {
			//Columns
			updateColumns();
			//Settings
			loadSettings();
		}
		super.setVisible(b);
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jOK;
	}

	@Override
	protected JButton getDefaultButton() {
		return jOK;
	}

	@Override
	protected void windowShown() { }

	@Override
	protected void save() {
		List<E> items = new ArrayList<>();

	//Columns + Header
		List<EnumTableColumn<E>> header = new ArrayList<>();
		if (jViewCurrent.isSelected()) {
			//Use the tool current shown columns + order
			header = exportFilterControl.getShownColumns();
		} else if (jViewSaved.isSelected()) {
			String viewKey = (String) jViews.getSelectedItem();
			View view = Settings.get().getTableViews(toolName).get(viewKey);
			for (SimpleColumn simpleColumn : view.getColumns()) {
				if (simpleColumn.isShown()) {
					EnumTableColumn<E> column = columns.get(simpleColumn.getEnumName());
					header.add(column);
				}
			}
		} else {
			//Use custom columns
			header.addAll(jColumnSelection.getSelectedValuesList());
		}
	//Bad selection
		if (header.isEmpty()) {
			JOptionPane.showMessageDialog(getDialog(), DialoguesExport.get().selectOne(), DialoguesExport.get().export(), JOptionPane.PLAIN_MESSAGE);
			return;
		}
	//Bad options
		if (jCsv.isSelected() && Settings.get().getExportSettings(toolName).getCsvDecimalSeparator() == DecimalSeparator.COMMA && Settings.get().getExportSettings(toolName).getFieldDelimiter() == FieldDelimiter.COMMA) {
			int nReturn = JOptionPane.showConfirmDialog(
					getDialog(),
					DialoguesExport.get().confirmStupidDecision(),
					DialoguesExport.get().export(),
					JOptionPane.YES_NO_OPTION,
					JOptionPane.PLAIN_MESSAGE);
			if (nReturn == JOptionPane.NO_OPTION) {
				return;
			}
		}
	//Save location
		boolean ok = browse();
		if (!ok) {
			return;
		}
	//Filters
		if (jNoFilter.isSelected()) {
			for (EventList<E> eventList : eventLists) {
				try {
					eventList.getReadWriteLock().readLock().lock();
					items.addAll(eventList);
				} finally {
					eventList.getReadWriteLock().readLock().unlock();
				}
			}
		} else if (jCurrentFilter.isSelected()) {
			List<Filter> filter = exportFilterControl.getCurrentFilters();
			for (EventList<E> eventList : eventLists) {
				try {
					eventList.getReadWriteLock().readLock().lock();
					FilterList<E> filterList = new FilterList<>(eventList, new FilterLogicalMatcher<>(filterControl, filter));
					if (!filterList.isEmpty() && filterList.get(0) instanceof TreeAsset) {
						FilterList<E> treeFilterList = new FilterList<>(eventList, new TreeMatcher<>(filterList));
						items.addAll(treeFilterList);
					} else {
						items.addAll(filterList);
					}
				} finally {
					eventList.getReadWriteLock().readLock().unlock();
				}
			}
		} else if (jSavedFilter.isSelected()) {
			String filterName = (String) jFilters.getSelectedItem();
			List<Filter> filter = exportFilterControl.getAllFilters().get(filterName);
			for (EventList<E> eventList : eventLists) {
				try {
					eventList.getReadWriteLock().readLock().lock();
					FilterList<E> filterList = new FilterList<>(eventList, new FilterLogicalMatcher<>(filterControl, filter));
					if (!filterList.isEmpty() && filterList.get(0) instanceof TreeAsset) {
						FilterList<E> treeFilterList = new FilterList<>(eventList, new TreeMatcher<>(filterList));
						items.addAll(treeFilterList);
					} else {
						items.addAll(filterList);
					}
				} finally {
					eventList.getReadWriteLock().readLock().unlock();
				}
			}
		}
	//Save settings
		saveSettings();
	//Save file
		boolean saved;
		if (jCsv.isSelected()) {
	//CSV
			//Create data
			List<String> headerStrings = new ArrayList<>(header.size());
			List<String> headerKeys = new ArrayList<>(header.size());
			for (EnumTableColumn<E> column : header) {
				headerStrings.add(column.getColumnName());
				headerKeys.add(column.name());
			}
			List<Map<String, String>> rows = new ArrayList<>();
			for (E e : items) {
				Map<String, String> row = new HashMap<>();
				for (EnumTableColumn<E> column : header) {
					row.put(column.name(), format(exportFilterControl.getColumnValue(e, column.name()), Settings.get().getExportSettings(toolName).getCsvDecimalSeparator(), false));
				}
				rows.add(row);
			}
			//Save data
			saved = CsvWriter.save(Settings.get().getExportSettings(toolName).getFilename(),
					rows,
					headerStrings.toArray(new String[headerStrings.size()]),
					headerKeys.toArray(new String[headerKeys.size()]),
					new CsvPreference.Builder('\"', Settings.get().getExportSettings(toolName).getFieldDelimiter().getValue(), Settings.get().getExportSettings(toolName).getLineDelimiter().getValue()).build());
		} else if (jHtml.isSelected()) {
	//HTML
			//Create data
			List<Map<EnumTableColumn<?>, String>> rows = new ArrayList<>();
			for (E e : items) {
				Map<EnumTableColumn<?>, String> row = new HashMap<>();
				for (EnumTableColumn<E> column : header) {
					row.put(column, format(exportFilterControl.getColumnValue(e, column.name()), Settings.get().getExportSettings(toolName).getCsvDecimalSeparator(), Settings.get().getExportSettings(toolName).isHtmlStyled()));
				}
				rows.add(row);
			}
			//Save data
			saved = HtmlWriter.save(Settings.get().getExportSettings(toolName).getFilename(),
					rows,
					new ArrayList<>(header),
					jHtmlIGB.isSelected() ? new ArrayList<>(items) : null,
					Settings.get().getExportSettings(toolName).isHtmlStyled(),
					Settings.get().getExportSettings(toolName).getHtmlRepeatHeader(),
					toolName.equals(TreeTab.NAME));
		} else if (jSql.isSelected()) {
	//SQL
			//Create data
			List<Map<EnumTableColumn<?>, Object>> rows = new ArrayList<>();
			for (E e : items) {
				Map<EnumTableColumn<?>, Object> row = new HashMap<>();
				for (EnumTableColumn<E> column : header) {
					row.put(column, exportFilterControl.getColumnValue(e, column.name()));
				}
				rows.add(row);
			}
			//Save data
			saved = SqlWriter.save(Settings.get().getExportSettings(toolName).getFilename(),
					rows,
					new ArrayList<>(header),
					Settings.get().getExportSettings(toolName).getTableName(),
					Settings.get().getExportSettings(toolName).isDropTable(),
					Settings.get().getExportSettings(toolName).isCreateTable(),
					Settings.get().getExportSettings(toolName).isExtendedInserts());
		} else {
			saved = false;
		}
		if (!saved) {
			JOptionPane.showMessageDialog(getDialog(),
					DialoguesExport.get().failedToSave(),
					DialoguesExport.get().export(),
					JOptionPane.PLAIN_MESSAGE);
		}

		setVisible(false);
	}

	/***
	 * Helper function to return if the comboBox has the item. Null string or item will return false.
	 * @param comboBox The combo box to look for the item in.
	 * @param item The item to look for int he combo box.
	 * @return True if the item is found (case sensitive). False if it is not found or either parameter is null.
	 */
	private boolean hasItem(JComboBox<String> comboBox, String item) {
		if (comboBox == null || item == null) {
			return false;
		}

		for (int i = 0; i < comboBox.getModel().getSize(); i++) {
			String comboItem = comboBox.getModel().getElementAt(i);
			if (item.equals(comboItem)) {
				return true;
			}
		}

		return false;
	}

	private class ListenerClass implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (ExportAction.OK.name().equals(e.getActionCommand())) {
				save();
			} else if (ExportAction.DEFAULT.name().equals(e.getActionCommand())) {
				resetSettings();
			} else if (ExportAction.CANCEL.name().equals(e.getActionCommand())) {
				setVisible(false);
				saveSettings();
			} else if (ExportAction.FORMAT_CHANGED.name().equals(e.getActionCommand())) {
				ExportFormat exportFormat = ExportFormat.CSV;
				if (jCsv.isSelected()) {
					exportFormat = ExportFormat.CSV;
					jOptionPanel.setBorder(BorderFactory.createTitledBorder(DialoguesExport.get().csv()));
				} else if (jHtml.isSelected()) {
					exportFormat = ExportFormat.HTML;
					jOptionPanel.setBorder(BorderFactory.createTitledBorder(DialoguesExport.get().html()));
				} else if (jSql.isSelected()) {
					exportFormat = ExportFormat.SQL;
					jOptionPanel.setBorder(BorderFactory.createTitledBorder(DialoguesExport.get().sql()));
				}
				Settings.get().getExportSettings(toolName).setExportFormat(exportFormat);
				cardLayout.show(jOptionPanel, exportFormat.name());
				jFileChooser.setExtension(exportFormat.getExtension());
			} else if (ExportAction.FILTER_CHANGED.name().equals(e.getActionCommand())) {
				jFilters.setEnabled(jSavedFilter.isSelected());
				FilterSelection filterSelection = FilterSelection.NONE;
				if (jNoFilter.isSelected()) {
					filterSelection = FilterSelection.NONE;
				} else if (jCurrentFilter.isSelected()) {
					filterSelection = FilterSelection.CURRENT;
				} else if (jSavedFilter.isSelected()) {
					filterSelection = FilterSelection.SAVED;
				}
				Settings.get().getExportSettings(toolName).setFilterSelection(filterSelection);
			} else if (ExportAction.VIEW_CHANGED.name().equals(e.getActionCommand())) {
				jViews.setEnabled(jViewSaved.isSelected());
				jColumnSelection.setEnabled(jViewSelect.isSelected());
				jViewSelectAll.setEnabled(jViewSelect.isSelected());
				ColumnSelection columnSelection = ColumnSelection.SHOWN;
				if (jViewCurrent.isSelected()) {
					columnSelection = ColumnSelection.SHOWN;
				} else if (jViewSaved.isSelected()) {
					columnSelection = ColumnSelection.SAVED;
				} else if (jViewSelect.isSelected()) {
					columnSelection = ColumnSelection.SELECTED;
				}
				Settings.get().getExportSettings(toolName).setColumnSelection(columnSelection);
			}
		}
	}

	public static class JOptionPanel extends JPanel {
		protected final GroupLayout layout;
		private final List<JComponent> components = new ArrayList<>();

		private JOptionPanel() {
			layout = new GroupLayout(this);
			this.setLayout(layout);
			layout.setAutoCreateGaps(false);
			layout.setAutoCreateContainerGaps(true);
		}

		public void add(JComponent comp) {
			components.add(comp);
			createLayout();
		}

		protected void createLayout() {
			this.removeAll();
			GroupLayout.ParallelGroup horizontalGroup = layout.createParallelGroup();
			GroupLayout.SequentialGroup verticalGroup = layout.createSequentialGroup();

			for (JComponent jComponent : components) {
				horizontalGroup.addComponent(jComponent);
				if (jComponent instanceof JLabel
						|| jComponent instanceof JButton
						|| jComponent instanceof JCheckBox
						|| jComponent instanceof JTextField
						|| jComponent instanceof JComboBox) {
					verticalGroup.addComponent(jComponent, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight());
				} else {
					verticalGroup.addComponent(jComponent);
				}
			}
			layout.setHorizontalGroup(
				layout.createSequentialGroup()
					.addGroup(horizontalGroup)
				);
			layout.setVerticalGroup(
				layout.createSequentialGroup()
					.addGroup(verticalGroup)
				);
		}
	}

	private class TreeMatcher<E> implements Matcher<E> {

		private final EventList<E> eventList;
		private final Set<TreeAsset> parentTree = new HashSet<>();

		public TreeMatcher(EventList<E> eventList) {
			this.eventList = eventList;
			Set<TreeAsset> items = new TreeSet<>(new AssetTreeComparator());
			for (E e : eventList) {
				if (e instanceof TreeAsset) {
					TreeAsset tree = (TreeAsset) e;
					items.add(tree);
					parentTree.addAll(tree.getTree());
				}
			}
			for (TreeAsset treeAsset : parentTree) {
				treeAsset.resetValues();
				if (treeAsset.isItem()) {
					items.add(treeAsset);
				}
			}
			for (TreeAsset treeAsset : items) {
				treeAsset.updateParents();
			}
		}

		@Override
		public boolean matches(E item) { //XXX - Expensive
			if (item instanceof TreeAsset) {
				TreeAsset treeAsset = (TreeAsset) item;
				if (treeAsset.isParent()) {
					return parentTree.contains(treeAsset);
				}
			}
			return eventList.contains(item);
		}
		
	}
}

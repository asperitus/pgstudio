/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.panels.popups;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;
import com.openscg.pgstudio.client.PgStudio;
import com.openscg.pgstudio.client.PgStudioService;
import com.openscg.pgstudio.client.PgStudioServiceAsync;
import com.openscg.pgstudio.client.handlers.UtilityCommandAsyncCallback;
import com.openscg.pgstudio.client.models.DatabaseObjectInfo;
import com.openscg.pgstudio.client.models.TableInfo;
import com.openscg.pgstudio.client.providers.ModelListProvider;
import com.openscg.pgstudio.client.providers.TableListDataProvider;

public class TruncateTablePopUp implements StudioModelPopUp {

	private final static String WARNING_MSG = "This will delete all data stored in the table and this can not "
			+ "be undone. Are you sure you want to truncate this " + "table?";

	private final PgStudioServiceAsync studioService = GWT
			.create(PgStudioService.class);

	final DialogBox dialogBox = new DialogBox();

	private SingleSelectionModel<TableInfo> selectionModel = null;
	private TableListDataProvider dataProvider = null;

	private DatabaseObjectInfo schema = null;

	@Override
	public DialogBox getDialogBox() throws PopUpException {
		if (selectionModel == null)
			throw new PopUpException("Selection Model is not set");

		if (dataProvider == null)
			throw new PopUpException("Data Provider is not set");

		if (schema == null)
			throw new PopUpException("Schema is not set");

		dialogBox.setWidget(getPanel());

		dialogBox.setGlassEnabled(true);
		dialogBox.center();

		return dialogBox;
	}

	@Override
	public void setSelectionModel(SingleSelectionModel model) {
		this.selectionModel = model;
	}

	@Override
	public void setDataProvider(ModelListProvider provider) {
		this.dataProvider = (TableListDataProvider) provider;

	}

	public void setSchema(DatabaseObjectInfo schema) {
		this.schema = schema;
	}

	private VerticalPanel getPanel() {
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("StudioPopup");

		VerticalPanel info = new VerticalPanel();
		info.setSpacing(10);

		Label lbl = new Label();
		lbl.setStyleName("StudioPopup-Msg");
		lbl.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		lbl.setText(WARNING_MSG);

		info.add(lbl);

		panel.add(info);

		Widget buttonBar = getButtonPanel();
		panel.add(buttonBar);
		panel.setCellHorizontalAlignment(buttonBar,
				HasHorizontalAlignment.ALIGN_CENTER);

		return panel;
	}

	private Widget getButtonPanel() {
		HorizontalPanel bar = new HorizontalPanel();
		bar.setHeight("50px");
		bar.setSpacing(10);

		Button yesButton = new Button("Yes");
		Button noButton = new Button("No");

		bar.add(yesButton);
		bar.add(noButton);

		bar.setCellHorizontalAlignment(yesButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		bar.setCellHorizontalAlignment(noButton,
				HasHorizontalAlignment.ALIGN_CENTER);

		yesButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (selectionModel.getSelectedObject() != null
						&& !"".equals(selectionModel.getSelectedObject()
								.getName())) {

					UtilityCommandAsyncCallback ac = new UtilityCommandAsyncCallback(
							dialogBox, dataProvider);
					ac.setAutoRefresh(true);
					ac.setShowResultOutput(true);

					studioService.truncate(PgStudio.getToken(), selectionModel
							.getSelectedObject().getId(), selectionModel
							.getSelectedObject().getItemType(), ac);
				}

			}
		});

		noButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				refresh();
				dialogBox.hide(true);
			}
		});

		return bar.asWidget();
	}

	private void refresh() {
		dataProvider.setSchema(schema);
	}
}

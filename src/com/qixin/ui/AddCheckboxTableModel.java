package com.qixin.ui;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class AddCheckboxTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 1L;

	public AddCheckboxTableModel(Vector<Vector<Object>> data, Vector<Object> columns) {
//	public AddCheckboxTableModel(Object[][] data, Object[] columns) {
		super(data, columns);
	}

	public boolean isCellEditable(int row, int column) { // 设置Table单元格是否可编辑
		if (column == 0) {
			return true;
		}
		return false;
	}

	
}

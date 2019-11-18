package com.qixin.ui;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

/**
 * 数据模型
 * 
 * @author Jevon
 * @date 2019年11月16日 下午4:10:08
 * 
 */
public class AddCheckboxTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 1L;

	public AddCheckboxTableModel(Vector<Vector<Object>> data, Vector<Object> columns) {
		super(data, columns);
	}

	public boolean isCellEditable(int row, int column) { // 设置Table单元格是否可编辑
		if (column == 0) {
			return true;
		}
		return false;
	}

}

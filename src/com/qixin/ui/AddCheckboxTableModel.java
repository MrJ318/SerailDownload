package com.qixin.ui;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

/**
 * ����ģ��
 * 
 * @author Jevon
 * @date 2019��11��16�� ����4:10:08
 * 
 */
public class AddCheckboxTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 1L;

	public AddCheckboxTableModel(Vector<Vector<Object>> data, Vector<Object> columns) {
		super(data, columns);
	}

	public boolean isCellEditable(int row, int column) { // ����Table��Ԫ���Ƿ�ɱ༭
		if (column == 0) {
			return true;
		}
		return false;
	}

}

package com.qixin.ui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.TooManyListenersException;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import com.qixin.listener.RecevieDataListener;
import com.qixin.service.ExcelManager;
import com.qixin.service.ReceiveData;
import com.qixin.service.SendThread;
import com.qixin.service.SerialPortManager;

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

public class MainWindows implements RecevieDataListener {

	private JFrame frame;
	private JTable table;
	private JLabel label_Info;
	private JProgressBar progressBar;
	private Vector<Vector<Object>> tableData = null;
	private Vector<String> comList;
	private File file;
	private SerialPort serialPort;
	private SendThread sendThread;
	private boolean flag = false;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindows window = new MainWindows();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public MainWindows() {
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setTitle("�ֳ����ļ����ع���");
		frame.setBounds(200, 100, 1355, 750);
		frame.setResizable(false);
		frame.getContentPane().setLayout(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JLabel label = new JLabel("����:");
		label.setBounds(27, 38, 72, 18);
		frame.getContentPane().add(label);

		JLabel label_1 = new JLabel("������:");
		label_1.setBounds(264, 38, 72, 18);
		frame.getContentPane().add(label_1);

		JComboBox<String> comboBox = new JComboBox<String>();
		comboBox.setBounds(80, 30, 135, 35);
		frame.getContentPane().add(comboBox);
		loadCom(comboBox);

		JComboBox<Integer> comboBox_1 = new JComboBox<Integer>();
		comboBox_1.setBounds(328, 30, 135, 35);
		frame.getContentPane().add(comboBox_1);
		loadRate(comboBox_1);

		JButton button = new JButton("ˢ��");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadCom(comboBox);
			}
		});
		button.setBounds(520, 25, 135, 45);
		frame.getContentPane().add(button);

		JButton button_1 = new JButton("�򿪴���");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String text = operPort((String) comboBox.getSelectedItem(), (int) comboBox_1.getSelectedItem());
				if (text != null && !text.equals("")) {
					button_1.setText(text);
				}
			}
		});
		button_1.setBounds(669, 25, 135, 45);
		frame.getContentPane().add(button_1);
//---------------------------------------------------------
		Vector<Object> columnNames = new Vector<Object>();
		columnNames.add("����");
		columnNames.add("�ܼ�¼��");
		columnNames.add("�����¼��");
		columnNames.add("ʱ��");
		columnNames.add("С��");
		columnNames.add("¥��");
		columnNames.add("��Ԫ");
		columnNames.add("���ߺ�");

		AddCheckboxTableModel tableModel = new AddCheckboxTableModel(tableData, columnNames);
		table = new JTable();
		table.setRowHeight(30);
		table.setModel(tableModel);

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(14, 92, 1299, 547);
		frame.getContentPane().add(scrollPane);

		JButton button_3 = new JButton("ѡ���ļ�");
		button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser jfc = new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				jfc.showDialog(new JLabel(), "ѡ��");
				file = jfc.getSelectedFile();
				if (file != null) {
					try {
						tableData = ExcelManager.readExcel(file);
						tableModel.setDataVector(tableData, columnNames);
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(frame, "��ȡ�ļ�ʧ�ܣ�����");
					}
				}
			}
		});
		button_3.setBounds(988, 25, 135, 45);
		frame.getContentPane().add(button_3);

		JButton button_5 = new JButton("д��");
		button_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				writeData();
			}
		});
		button_5.setBounds(1178, 25, 135, 45);
		frame.getContentPane().add(button_5);

		JLabel label_2 = new JLabel("����:");
		label_2.setBounds(253, 671, 72, 18);
		frame.getContentPane().add(label_2);

		label_Info = new JLabel();
		label_Info.setBounds(14, 671, 200, 18);
		frame.getContentPane().add(label_Info);

		progressBar = new JProgressBar();
		progressBar.setBounds(306, 668, 404, 21);
		frame.getContentPane().add(progressBar);

	}

	/**
	 * ���ش����б�
	 */
	private void loadCom(JComboBox<String> box) {
		comList = SerialPortManager.findPort();
		box.setModel(new DefaultComboBoxModel<String>(comList));
	}

	/**
	 * ���ز������б�
	 * 
	 * @param box
	 */
	private void loadRate(JComboBox<Integer> box) {
		Vector<Integer> rateList = new Vector<Integer>();
		rateList.add(9600);
		rateList.add(14400);
		rateList.add(19200);
		rateList.add(28800);
		rateList.add(38400);
		rateList.add(57600);
		rateList.add(115200);
		box.setModel(new DefaultComboBoxModel<Integer>(rateList));
	}

	/**
	 * �򿪻�رմ���
	 * 
	 * @param com  ���ں�
	 * @param rate ������
	 * @return
	 */
	private String operPort(String com, int rate) {
		if (flag) {
			SerialPortManager.closePort(serialPort);
			flag = false;
			return "�򿪴���";
		} else {
			try {
				serialPort = SerialPortManager.openPort(com, rate);
				flag = true;
				return "�رմ���";
			} catch (NoSuchPortException e1) {
				JOptionPane.showMessageDialog(frame, "��ʧ�ܣ����ں�����");
			} catch (PortInUseException e1) {
				JOptionPane.showMessageDialog(frame, "�ö˿��Ѵ򿪣�");
			} catch (UnsupportedCommOperationException e1) {
				JOptionPane.showMessageDialog(frame, "��ʧ�ܣ�" + e1.getMessage() + "��");
			}
			return "";
		}

	}

	private void writeData() {
//		if (tableData == null || tableData.size() < 1) {
//			JOptionPane.showMessageDialog(frame, "����ѡ���ļ�");
//			return;
//		}
//
//		if (serialPort == null) {
//			JOptionPane.showMessageDialog(frame, "���ȴ򿪴���");
//			return;
//		}
//
//		try {
//			List<byte[]> sendList = ExcelManager.getDataBytes(file);
//			progressBar.setMaximum(sendList.size());
//			label_Info.setText("����д�룬���Ժ�...");
//			SerialPortManager.addListener(serialPort, new ReceiveData(MainWindows.this, serialPort));
//			sendThread = new SendThread(MainWindows.this, serialPort, sendList);
//			sendThread.start();
//		} catch (Exception e) {
//			JOptionPane.showMessageDialog(frame, "��ȡ�ļ�ʧ�ܣ�����");
//		}
		SerialPort port;
		try {
			port = SerialPortManager.openPort("COM3", 57600);
			List<byte[]> sendList = ExcelManager.getDataBytes(new File("F:\\tt.xlsx"));
			SerialPortManager.addListener(port, new ReceiveData(MainWindows.this, port));
			sendThread = new SendThread(MainWindows.this, port, sendList);
			sendThread.start();
		} catch (NoSuchPortException | PortInUseException | UnsupportedCommOperationException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onReceive(byte[] bytes) {

		if (bytes[0] == 0) {
			bytes = Arrays.copyOfRange(bytes, 1, bytes.length);
		}

		if (bytes.length == 6 && bytes[0] == (byte) 0x68 && bytes[1] == 0 && bytes[2] == 0 && bytes[3] == (byte) 0xAC
				&& bytes[4] == (byte) 0x14 && bytes[5] == (byte) 0x16) {
			sendThread.flag = true;
			progressBar.setValue(progressBar.getValue() + 1);
		}
	}

	@Override
	public void onSendErr(int code) {
		String str;
		switch (code) {
		case 0:
			str = "д��С����Ϣʧ��,�����½��в�����";
			break;

		case 1:
			str = "д��¥��Ϣʧ��,�����½��в�����";
			break;

		case 2:
			str = "д�뵥Ԫ��Ϣʧ��,�����½��в�����";
			break;

		case 3:
			str = "д��������Ϣʧ��,�����½��в�����";
			break;

		case 256:
			str = "������ɣ�ȫ���ļ�д��ɹ�";
			break;

		default:
			str = "д���" + (code - 3) + "���ļ�ͷ��Ϣʧ��,�����½��в�����";
			break;
		}
		label_Info.setText(str);
		JOptionPane.showMessageDialog(frame, str);
		progressBar.setValue(0);
	}
}

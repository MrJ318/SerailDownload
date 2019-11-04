package com.qixin.ui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
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
import javax.swing.table.TableColumn;

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
	private int succ, err;
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
		frame.setTitle("手持器文件下载工具");
		frame.setBounds(200, 100, 1355, 750);
		frame.setResizable(false);
		frame.getContentPane().setLayout(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JLabel label = new JLabel("串口:");
		label.setBounds(27, 38, 72, 18);
		frame.getContentPane().add(label);

		JLabel label_1 = new JLabel("波特率:");
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

		JButton button = new JButton("刷新");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadCom(comboBox);
			}
		});
		button.setBounds(520, 25, 135, 45);
		frame.getContentPane().add(button);

		JButton button_1 = new JButton("打开串口");
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
		columnNames.add("类型");
		columnNames.add("总记录数");
		columnNames.add("错误记录数");
		columnNames.add("时间");
		columnNames.add("小区");
		columnNames.add("楼号");
		columnNames.add("单元");
		columnNames.add("总线号");
		columnNames.add("code");

		AddCheckboxTableModel tableModel = new AddCheckboxTableModel(tableData, columnNames);
// ------------------------------------------

		table = new JTable();
		table.setRowHeight(30);
		table.setModel(tableModel);
		hideLstColumn();

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(14, 92, 1299, 547);
		frame.getContentPane().add(scrollPane);

		JButton button_3 = new JButton("选择文件");
		button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser jfc = new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				jfc.showDialog(new JLabel(), "选择");
				file = jfc.getSelectedFile();
				if (file != null) {
					try {
						tableData = ExcelManager.readExcel(file);
						tableModel.setDataVector(tableData, columnNames);
						hideLstColumn();
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(frame, "读取文件失败，请检查");
					}
				}
			}
		});
		button_3.setBounds(988, 25, 135, 45);
		frame.getContentPane().add(button_3);

//		JButton button_4 = new JButton("写入选中项");
//		button_4.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				writeData(true);
//			}
//		});
//		button_4.setBounds(638, 17, 113, 27);
//		frame.getContentPane().add(button_4);

		JButton button_5 = new JButton("写入");
		button_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				writeData();
			}
		});
		button_5.setBounds(1178, 25, 135, 45);
		frame.getContentPane().add(button_5);

		JLabel label_2 = new JLabel("进度:");
		label_2.setBounds(27, 671, 72, 18);
		frame.getContentPane().add(label_2);

		label_Info = new JLabel();
		label_Info.setBounds(233, 422, 535, 18);
		frame.getContentPane().add(label_Info);

		progressBar = new JProgressBar();
		progressBar.setBounds(80, 668, 404, 21);
		frame.getContentPane().add(progressBar);

	}

	/**
	 * 隐藏最后一列
	 */
	private void hideLstColumn() {
		TableColumn tc = table.getColumnModel().getColumn(8);
		tc.setMaxWidth(0);
		tc.setPreferredWidth(0);
		tc.setMinWidth(0);
		tc.setWidth(0);
		table.getTableHeader().getColumnModel().getColumn(8).setMaxWidth(0);
		table.getTableHeader().getColumnModel().getColumn(8).setMinWidth(0);
	}

	/**
	 * 加载串口列表
	 */
	private void loadCom(JComboBox<String> box) {
		comList = SerialPortManager.findPort();
		box.setModel(new DefaultComboBoxModel<String>(comList));
	}

	private String operPort(String com, int rate) {
		if (flag) {
			SerialPortManager.closePort(serialPort);
			flag = false;
			return "打开串口";
		} else {
			try {
				serialPort = SerialPortManager.openPort(com, rate);
				flag = true;
				return "关闭串口";
			} catch (NoSuchPortException e1) {
				JOptionPane.showMessageDialog(frame, "打开失败，串口号有误！");
			} catch (PortInUseException e1) {
				JOptionPane.showMessageDialog(frame, "该端口已打开！");
			} catch (UnsupportedCommOperationException e1) {
				JOptionPane.showMessageDialog(frame, "打开失败，" + e1.getMessage() + "！");
			}
			return "";
		}

	}

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

	@Override
	public void onReceive(byte[] bytes) {

//		StringBuffer buffer = new StringBuffer();
//		for (byte b : bytes) {
//			buffer.append(String.format("%02X", b) + " ");
//
//		}
//		System.out.println(buffer.toString());

		if (bytes.length >= 6) {

			if (bytes[0] == 0x68 && bytes[1] == 0 && bytes[2] == 1 && bytes[6] == 0x16) {
				progressBar.setValue(progressBar.getValue() + 1);
				sendThread.flag = true;
			}

//			if (bytes[3] == (byte) 0xA0) {
//				succ++;
//			} else {
//				if (bytes[4] == 0x00) {
//					err++;
//				} else {
//					sendThread.flag = false;
//					JOptionPane.showMessageDialog(frame, "设备内存已满!");
//					SerialPortManager.removeListener(serialPort);
//				}
//			}
//			if (sendThread != null) {
//				progressBar.setValue(progressBar.getValue() + 1);
//				synchronized (sendThread) {
//					sendThread.notify();
//				}
//			}
		}

//		label_Info.setText("成功:" + succ + "条，" + "失败:" + err + "条");

	}

	private void writeData() {
//		if (tableData == null || tableData.size() < 1) {
//			JOptionPane.showMessageDialog(frame, "请先选择文件");
//			return;
//		}
//
//		if (serialPort == null) {
//			JOptionPane.showMessageDialog(frame, "请先打开串口");
//			return;
//		}
//
//		List<byte[]> sendList = new ArrayList<byte[]>();
//
//		for (Vector<Object> row : tableData) {
//			sendList.add((byte[]) row.get(9));
//		}

		try {
			List<byte[]> sendList = ExcelManager.getDataBytes(file);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(frame, "读取文件失败，请检查");
		}

//		progressBar.setMaximum(sendList.size());
//		sendThread = new SendThread(serialPort, sendList);
//		sendThread.start();
		byte[] bytes = new byte[4096];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) (i % 128);
		}
		SerialPortManager.sendToPort(serialPort, bytes);
		SerialPortManager.addListener(serialPort, new ReceiveData(MainWindows.this, serialPort));

	}

	@Override
	public void onSendErr(int code) {
		switch (code) {
		case 0:
			label_Info.setText("写入小区信息失败！");
			break;

		case 1:
			label_Info.setText("写入楼信息失败！");
			break;

		case 2:
			label_Info.setText("写入单元信息失败！");
			break;

		case 3:
			label_Info.setText("写入总线信息失败！");
			break;

		case 256:
			label_Info.setText("操作完成，全部文件写入成功");
			break;

		default:
			label_Info.setText("写入第" + (code - 3) + "条文件头信息失败！");
			break;
		}
	}
}

package com.qixin.ui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.google.common.primitives.Bytes;
import com.qixin.listener.MainListener;
import com.qixin.model.TableHead;
import com.qixin.model.TableRecoder;
import com.qixin.service.ComReceiveEventListener;
import com.qixin.service.ComReceiveThread;
import com.qixin.service.ComWriteEventListener;
import com.qixin.service.ComWriteThread;
import com.qixin.utils.ByteUtil;
import com.qixin.utils.ExcelManager;
import com.qixin.utils.SerialPortManager;

import gnu.io.SerialPort;

public class MainWindows implements MainListener {

	private JFrame frame;
	private JTable table_Write;
	private JLabel label_Info;
	private JProgressBar progressBar;
	private Vector<Vector<Object>> tableData = null;
	private Vector<String> comList;
	private File file;
	private SerialPort serialPort;
	private ComWriteThread sendThread;
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
		label_1.setBounds(254, 38, 72, 18);
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
		button.setBounds(496, 25, 135, 45);
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
		button_1.setBounds(645, 25, 135, 45);
		frame.getContentPane().add(button_1);
//---------------------------------------------------------
		Vector<Object> columnNames1 = new Vector<Object>();
		columnNames1.add("小区");
		columnNames1.add("楼号");
		columnNames1.add("单元");
		columnNames1.add("总线号");
		columnNames1.add("门牌号");
		columnNames1.add("表码");
		columnNames1.add("厂家代码");

		AddCheckboxTableModel tableModel = new AddCheckboxTableModel(tableData, columnNames1);
		table_Write = new JTable();
		table_Write.setRowHeight(30);
		table_Write.setModel(tableModel);

		JScrollPane scrollPane = new JScrollPane(table_Write);
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
						tableModel.setDataVector(tableData, columnNames1);
					} catch (Exception e) {
						JOptionPane.showMessageDialog(frame, "读取文件失败，请检查\n" + e.getMessage());
					}
				}
			}
		});
		button_3.setBounds(834, 25, 135, 45);
		frame.getContentPane().add(button_3);

		JButton button_5 = new JButton("写入数据");
		button_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				writeData();
			}
		});
		button_5.setBounds(983, 25, 135, 45);
		frame.getContentPane().add(button_5);

		JLabel label_2 = new JLabel("进度:");
		label_2.setBounds(14, 671, 72, 18);
		frame.getContentPane().add(label_2);

		label_Info = new JLabel("串口已关闭");
		label_Info.setBounds(496, 671, 200, 18);
		frame.getContentPane().add(label_Info);

		progressBar = new JProgressBar();
		progressBar.setBounds(80, 671, 404, 21);
		frame.getContentPane().add(progressBar);

		JButton btnNewButton = new JButton("导出数据");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exportData();
			}
		});
		btnNewButton.setBounds(1178, 25, 135, 45);
		frame.getContentPane().add(btnNewButton);

	}

	/**
	 * 加载串口列表
	 */
	private void loadCom(JComboBox<String> box) {
		comList = SerialPortManager.findPort();
		box.setModel(new DefaultComboBoxModel<String>(comList));
	}

	/**
	 * 加载波特率列表
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
	 * 打开或关闭串口
	 * 
	 * @param com  串口号
	 * @param rate 波特率
	 * @return
	 */
	private String operPort(String com, int rate) {
		if (flag) {
			SerialPortManager.closePort(serialPort);
			flag = false;
			label_Info.setText("串口已关闭");
			return "打开串口";
		} else {
			try {
				serialPort = SerialPortManager.openPort(com, rate);
				flag = true;
				label_Info.setText("串口已打开");
				return "关闭串口";
			} catch (Exception e) {
				JOptionPane.showMessageDialog(frame, "打开失败，" + e.getMessage() + "！");
			}
			return "";
		}

	}

	/**
	 * 写入数据
	 */
	private void writeData() {
		if (tableData == null || tableData.size() < 1) {
			JOptionPane.showMessageDialog(frame, "请先选择文件");
			return;
		}

		if (serialPort == null) {
			JOptionPane.showMessageDialog(frame, "请先打开串口");
			return;
		}

		try {
			List<byte[]> sendList = ExcelManager.getDataBytes(file);
			progressBar.setMaximum(sendList.size());
			label_Info.setText("正在写入，请稍后...");
			SerialPortManager.addListener(serialPort, new ComWriteEventListener(MainWindows.this, serialPort));
			sendThread = new ComWriteThread(MainWindows.this, serialPort, sendList);
			sendThread.start();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(frame, "读取文件失败，请检查\n" + e.getMessage());
		}
//		try {
//			serialPort = SerialPortManager.openPort("COM3", 57600);
//			List<byte[]> sendList = ExcelManager.getDataBytes(new File("F:\\tt.xlsx"));
//			progressBar.setMaximum(sendList.size());
//			SerialPortManager.addListener(serialPort, new ReceiveData(MainWindows.this, serialPort));
//			sendThread = new SendThread(MainWindows.this, serialPort, sendList);
//			sendThread.start();
//		} catch (NoSuchPortException | PortInUseException | UnsupportedCommOperationException e) {
//			e.printStackTrace();
//		} catch (InvalidFormatException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (ReadExcelException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}

	/**
	 * 导出数据
	 */
	private void exportData() {
//		if (serialPort == null) {
//			JOptionPane.showMessageDialog(frame, "请先打开串口");
//			return;
//		}

		try {
			serialPort = SerialPortManager.openPort("COM3", 57600);
			label_Info.setText("正在导出，请稍后...");
			SerialPortManager.addListener(serialPort, new ComReceiveEventListener(MainWindows.this, serialPort));
			byte[] bytes = new byte[] { (byte) 0xFE, (byte) 0x68, (byte) 0x00, (byte) 0x02, (byte) 0x51, (byte) 0x10,
					(byte) 0x20, (byte) 0xEB, (byte) 0x16 };
			SerialPortManager.sendToPort(serialPort, bytes);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(frame, e.getMessage());
		}

	}

	private void exRecData(byte[] bytes) {
		if (bytes[7] == 96) {
			int len = bytes[5] * 256 + bytes[6];
			len = (len - 2) / 6;
			List<byte[]> list = new ArrayList<byte[]>();
			for (int i = 0; i < len; i++) {
				byte[] b = new byte[9];
				b[0] = (byte) 0xFE;
				b[1] = (byte) 0x68;
				b[2] = 0;
				b[3] = 2;
				b[4] = (byte) 0x51;
				int page = (260 + i * 2) * 16;
				b[5] = (byte) (page / 256);
				b[6] = (byte) (page % 256);
				b[7] = (byte) (((byte) 0xBB) + b[5] + b[6]);
				b[8] = (byte) 0x16;
				list.add(b);
			}
			ComReceiveThread sendThread2 = new ComReceiveThread(MainWindows.this, serialPort, list);
			sendThread2.start();
			return;
		}

		int count = bytes[7] & 0xff;
		System.out.println("记录数：" + count);
		byte[] recBytes = new byte[64 + 51 * count];

		try {
			List<Byte> tmpBytes = new ArrayList<Byte>(Bytes.asList(bytes));
			for (int i = 7; i >= 0; i--) {
				tmpBytes.remove(1028 + 1029 * i);
				tmpBytes.remove(3 + 1029 * i);
				tmpBytes.remove(2 + 1029 * i);
				tmpBytes.remove(1 + 1029 * i);
				tmpBytes.remove(1029 * i);
			}
			for (int i = 0; i < recBytes.length; i++) {
				recBytes[i] = tmpBytes.get(i);
			}

			System.out.println("---");
			for (byte b : recBytes) {

				System.out.print(String.format("%02X", b) + " ");
			}
			System.out.println("\n---");
//			System.out.println(recBytes.length);
//			for (int j = 0; j < recBytes.length; j++) {
			//
//			}
			byte[] head = new byte[47];
			System.arraycopy(recBytes, 0, head, 0, 47);

			TableHead head2 = ByteUtil.byteToHead(head);
			System.err.println(head2.toString());

			List<TableRecoder> list = new ArrayList<TableRecoder>();
			for (int i = 0; i < count; i++) {
				byte[] rec = new byte[51];
				System.arraycopy(recBytes, 64 + i * 51, rec, 0, 51);
				TableRecoder recoder = ByteUtil.byteToRecoder(rec);
				System.err.println(recoder);
				list.add(recoder);
			}

			new Thread() {
				Object lock = new Object();

				public void run() {
					synchronized (lock) {

						ExcelManager.writeExcelFile(head2, list);
					}
				};
			}.start();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void onSendCompelet(int code) {
		SerialPortManager.removeListener(serialPort);
		if (code == 0) {
			// label_Info.setText("操作完成，全部文件写入成功!");
			JOptionPane.showMessageDialog(frame, "操作完成，全部文件写入成功！");
		} else {
			// label_Info.setText("操作完成，全部文件写入成功!");
			JOptionPane.showMessageDialog(frame, "写入过程中出错，请重新进行操作！");
		}
		progressBar.setValue(0);

	}

	@Override
	public void onWriteCompelet(byte[] bytes) {
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
	public void onReceiveCompelet(byte[] bytes) {

		if (bytes.length != 8232) {
			label_Info.setText("导出失败，请重试");
			return;
		}
		exRecData(bytes);

	}
}

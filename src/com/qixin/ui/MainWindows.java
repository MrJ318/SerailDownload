package com.qixin.ui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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

import org.apache.log4j.Logger;

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

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

public class MainWindows implements MainListener {

	private Logger logger = Logger.getLogger(MainWindows.class);

	private JFrame frame;
	private JTable table_Write;
	private JLabel label_Info;
	private JProgressBar progressBar;
	private Vector<Vector<Object>> tableData = null;
	private Vector<String> comList;
	private File file;
	private SerialPort serialPort;
	private ComWriteThread writeThread;
	private ComReceiveThread receiveThread;
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
			} catch (NoSuchPortException e) {
				JOptionPane.showMessageDialog(frame, "串口打开失败，串口号有误！");
				logger.error(e.getMessage());
			} catch (PortInUseException e) {
				JOptionPane.showMessageDialog(frame, "串口打开失败，当前串口被占用！");
				logger.error(e.getMessage());
			} catch (UnsupportedCommOperationException e) {
				JOptionPane.showMessageDialog(frame, "串口打开失败，" + e.getMessage());
				logger.error(e.getMessage());
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
			writeThread = new ComWriteThread(MainWindows.this, serialPort, sendList);
			writeThread.start();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(frame, "读取文件失败，请检查\n" + e.getMessage());
			logger.error(e.getMessage());
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
		if (serialPort == null) {
			JOptionPane.showMessageDialog(frame, "请先打开串口");
			return;
		}

		JFileChooser jfc = new JFileChooser();
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		jfc.showDialog(new JLabel(), "请选择保存位置");
		file = jfc.getSelectedFile();

		try {
//			file = new File("D:\\table");
//			serialPort = SerialPortManager.openPort("COM3", 57600);
			SerialPortManager.addListener(serialPort, new ComReceiveEventListener(MainWindows.this, serialPort, 0));
			byte[] bytes = new byte[] { (byte) 0xFE, (byte) 0x68, (byte) 0x00, (byte) 0x02, (byte) 0x51, (byte) 0x10,
					(byte) 0x20, (byte) 0xEB, (byte) 0x16 };
			SerialPortManager.sendToPort(serialPort, bytes);
			label_Info.setText("正在导出，请稍后...");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(frame, e.getMessage());
			logger.error(e.getMessage());
		}

	}

	/**
	 * 将收到的数据解析并保存进Excel文件
	 * 
	 * @param bytes
	 */
	private void exRecData(byte[] bytes) {

		logger.debug("-----开始解析数据-----");
		int count = bytes[7] & 0xff;
		logger.debug("总记录数:" + count);
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

			StringBuffer buffer = new StringBuffer();
			for (byte b : recBytes) {
				buffer.append(String.format("%02X", b) + " ");
			}
			logger.debug("解析到的字节数组：" + buffer);

			byte[] headBytes = new byte[47];
			System.arraycopy(recBytes, 0, headBytes, 0, 47);

			TableHead head = ByteUtil.byteToHead(headBytes);
			logger.debug("解析到的文件头：" + head);

			List<TableRecoder> list = new ArrayList<TableRecoder>();
			for (int i = 0; i < count; i++) {
				byte[] rec = new byte[51];
				System.arraycopy(recBytes, 64 + i * 51, rec, 0, 51);
				TableRecoder recoder = ByteUtil.byteToRecoder(rec);
				logger.debug("解析到的记录" + i + "：" + recoder);
				list.add(recoder);
			}

			new Thread() {
				public void run() {
					synchronized (file) {
						try {
							ExcelManager.writeExcelFile(head, list, file);
						} catch (FileNotFoundException e) {
							JOptionPane.showMessageDialog(frame, e.getMessage());
							logger.error(e.getMessage());
						} catch (IOException e) {
							JOptionPane.showMessageDialog(frame, e.getMessage());
							logger.error(e.getMessage());
						}
					}
				};
			}.start();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		logger.debug("-----结束解析数据-----");
	}

	public void onSendCompelet(int code) {
		SerialPortManager.removeListener(serialPort);
		if (code == 0) {
			JOptionPane.showMessageDialog(frame, "操作完成，全部文件写入成功！");
		} else {
			JOptionPane.showMessageDialog(frame, "写入过程中出错，请重新进行操作！");
		}
		label_Info.setText("写入完成");
		progressBar.setValue(0);

	}

	@Override
	public void onSendReadCompelet(int code) {
		SerialPortManager.removeListener(serialPort);
		if (code == 0) {
			JOptionPane.showMessageDialog(frame, "导出成功！");
		} else {
			JOptionPane.showMessageDialog(frame, "导出完成，有" + code + "条数据导出失败！");
		}
		label_Info.setText("导出完成");
		progressBar.setValue(0);
	}

	@Override
	public void onWriteCompelet(byte[] bytes) {
		if (bytes[0] == 0) {
			bytes = Arrays.copyOfRange(bytes, 1, bytes.length);
		}
		if (bytes.length == 6 && bytes[0] == (byte) 0x68 && bytes[1] == 0 && bytes[2] == 0 && bytes[3] == (byte) 0xAC
				&& bytes[4] == (byte) 0x14 && bytes[5] == (byte) 0x16) {
			writeThread.flag = true;
			progressBar.setValue(progressBar.getValue() + 1);
		}
	}

	@Override
	public void onReceiveCompelet1(byte[] bytes) {
		if (bytes.length != 8232 || bytes[4] != 104 || bytes[7] != 96) {
			label_Info.setText("导出失败，请重试");
			JOptionPane.showMessageDialog(frame, "导出失败，请重试！");
			SerialPortManager.removeListener(serialPort);
			return;
		}
		List<byte[]> list = ByteUtil.getReadByte(bytes);
		progressBar.setMaximum(list.size());
		SerialPortManager.addListener(serialPort, new ComReceiveEventListener(MainWindows.this, serialPort, 0));
		receiveThread = new ComReceiveThread(MainWindows.this, serialPort, list);
		receiveThread.start();
	}

	@Override
	public void onReceiveCompelet2(byte[] bytes) {
		if (bytes.length == 8232) {
			receiveThread.flag = true;
			exRecData(bytes);
			progressBar.setValue(progressBar.getValue() + 1);
		}

	}

}

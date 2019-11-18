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
		frame.setTitle("�ֳ����ļ����ع���");
		frame.setBounds(200, 100, 1355, 750);
		frame.setResizable(false);
		frame.getContentPane().setLayout(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JLabel label = new JLabel("����:");
		label.setBounds(27, 38, 72, 18);
		frame.getContentPane().add(label);

		JLabel label_1 = new JLabel("������:");
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

		JButton button = new JButton("ˢ��");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadCom(comboBox);
			}
		});
		button.setBounds(496, 25, 135, 45);
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
		button_1.setBounds(645, 25, 135, 45);
		frame.getContentPane().add(button_1);
//---------------------------------------------------------
		Vector<Object> columnNames1 = new Vector<Object>();
		columnNames1.add("С��");
		columnNames1.add("¥��");
		columnNames1.add("��Ԫ");
		columnNames1.add("���ߺ�");
		columnNames1.add("���ƺ�");
		columnNames1.add("����");
		columnNames1.add("���Ҵ���");

		AddCheckboxTableModel tableModel = new AddCheckboxTableModel(tableData, columnNames1);
		table_Write = new JTable();
		table_Write.setRowHeight(30);
		table_Write.setModel(tableModel);

		JScrollPane scrollPane = new JScrollPane(table_Write);
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
						tableModel.setDataVector(tableData, columnNames1);
					} catch (Exception e) {
						JOptionPane.showMessageDialog(frame, "��ȡ�ļ�ʧ�ܣ�����\n" + e.getMessage());
					}
				}
			}
		});
		button_3.setBounds(834, 25, 135, 45);
		frame.getContentPane().add(button_3);

		JButton button_5 = new JButton("д������");
		button_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				writeData();
			}
		});
		button_5.setBounds(983, 25, 135, 45);
		frame.getContentPane().add(button_5);

		JLabel label_2 = new JLabel("����:");
		label_2.setBounds(14, 671, 72, 18);
		frame.getContentPane().add(label_2);

		label_Info = new JLabel("�����ѹر�");
		label_Info.setBounds(496, 671, 200, 18);
		frame.getContentPane().add(label_Info);

		progressBar = new JProgressBar();
		progressBar.setBounds(80, 671, 404, 21);
		frame.getContentPane().add(progressBar);

		JButton btnNewButton = new JButton("��������");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exportData();
			}
		});
		btnNewButton.setBounds(1178, 25, 135, 45);
		frame.getContentPane().add(btnNewButton);

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
			label_Info.setText("�����ѹر�");
			return "�򿪴���";
		} else {
			try {
				serialPort = SerialPortManager.openPort(com, rate);
				flag = true;
				label_Info.setText("�����Ѵ�");
				return "�رմ���";
			} catch (NoSuchPortException e) {
				JOptionPane.showMessageDialog(frame, "���ڴ�ʧ�ܣ����ں�����");
				logger.error(e.getMessage());
			} catch (PortInUseException e) {
				JOptionPane.showMessageDialog(frame, "���ڴ�ʧ�ܣ���ǰ���ڱ�ռ�ã�");
				logger.error(e.getMessage());
			} catch (UnsupportedCommOperationException e) {
				JOptionPane.showMessageDialog(frame, "���ڴ�ʧ�ܣ�" + e.getMessage());
				logger.error(e.getMessage());
			}
			return "";
		}

	}

	/**
	 * д������
	 */
	private void writeData() {
		if (tableData == null || tableData.size() < 1) {
			JOptionPane.showMessageDialog(frame, "����ѡ���ļ�");
			return;
		}

		if (serialPort == null) {
			JOptionPane.showMessageDialog(frame, "���ȴ򿪴���");
			return;
		}

		try {
			List<byte[]> sendList = ExcelManager.getDataBytes(file);
			progressBar.setMaximum(sendList.size());
			label_Info.setText("����д�룬���Ժ�...");
			SerialPortManager.addListener(serialPort, new ComWriteEventListener(MainWindows.this, serialPort));
			writeThread = new ComWriteThread(MainWindows.this, serialPort, sendList);
			writeThread.start();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(frame, "��ȡ�ļ�ʧ�ܣ�����\n" + e.getMessage());
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
	 * ��������
	 */
	private void exportData() {
		if (serialPort == null) {
			JOptionPane.showMessageDialog(frame, "���ȴ򿪴���");
			return;
		}

		JFileChooser jfc = new JFileChooser();
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		jfc.showDialog(new JLabel(), "��ѡ�񱣴�λ��");
		file = jfc.getSelectedFile();

		try {
//			file = new File("D:\\table");
//			serialPort = SerialPortManager.openPort("COM3", 57600);
			SerialPortManager.addListener(serialPort, new ComReceiveEventListener(MainWindows.this, serialPort, 0));
			byte[] bytes = new byte[] { (byte) 0xFE, (byte) 0x68, (byte) 0x00, (byte) 0x02, (byte) 0x51, (byte) 0x10,
					(byte) 0x20, (byte) 0xEB, (byte) 0x16 };
			SerialPortManager.sendToPort(serialPort, bytes);
			label_Info.setText("���ڵ��������Ժ�...");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(frame, e.getMessage());
			logger.error(e.getMessage());
		}

	}

	/**
	 * ���յ������ݽ����������Excel�ļ�
	 * 
	 * @param bytes
	 */
	private void exRecData(byte[] bytes) {

		logger.debug("-----��ʼ��������-----");
		int count = bytes[7] & 0xff;
		logger.debug("�ܼ�¼��:" + count);
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
			logger.debug("���������ֽ����飺" + buffer);

			byte[] headBytes = new byte[47];
			System.arraycopy(recBytes, 0, headBytes, 0, 47);

			TableHead head = ByteUtil.byteToHead(headBytes);
			logger.debug("���������ļ�ͷ��" + head);

			List<TableRecoder> list = new ArrayList<TableRecoder>();
			for (int i = 0; i < count; i++) {
				byte[] rec = new byte[51];
				System.arraycopy(recBytes, 64 + i * 51, rec, 0, 51);
				TableRecoder recoder = ByteUtil.byteToRecoder(rec);
				logger.debug("�������ļ�¼" + i + "��" + recoder);
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
		logger.debug("-----������������-----");
	}

	public void onSendCompelet(int code) {
		SerialPortManager.removeListener(serialPort);
		if (code == 0) {
			JOptionPane.showMessageDialog(frame, "������ɣ�ȫ���ļ�д��ɹ���");
		} else {
			JOptionPane.showMessageDialog(frame, "д������г��������½��в�����");
		}
		label_Info.setText("д�����");
		progressBar.setValue(0);

	}

	@Override
	public void onSendReadCompelet(int code) {
		SerialPortManager.removeListener(serialPort);
		if (code == 0) {
			JOptionPane.showMessageDialog(frame, "�����ɹ���");
		} else {
			JOptionPane.showMessageDialog(frame, "������ɣ���" + code + "�����ݵ���ʧ�ܣ�");
		}
		label_Info.setText("�������");
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
			label_Info.setText("����ʧ�ܣ�������");
			JOptionPane.showMessageDialog(frame, "����ʧ�ܣ������ԣ�");
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

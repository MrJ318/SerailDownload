package com.qixin.ui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
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

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import com.qixin.listener.RecevieDataListener;
import com.qixin.service.ExcelManager;
import com.qixin.service.SendThread;
import com.qixin.service.SerialPortManager;

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

public class MainWindows2 implements RecevieDataListener {

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
					MainWindows2 window = new MainWindows2();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	MyThread thread = new MyThread();

	public MainWindows2() {
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

		JComboBox<Integer> comboBox_1 = new JComboBox<Integer>();
		comboBox_1.setBounds(328, 30, 135, 35);
		frame.getContentPane().add(comboBox_1);

		JButton button = new JButton("刷新");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thread.start();

			}
		});
		button.setBounds(520, 25, 135, 45);
		frame.getContentPane().add(button);

		JButton button_1 = new JButton("打开串口");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		button_1.setBounds(669, 25, 135, 45);
		frame.getContentPane().add(button_1);

		JButton button_5 = new JButton("写入");
		button_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thread.flag = false;
			}
		});
		button_5.setBounds(1178, 25, 135, 45);
		frame.getContentPane().add(button_5);

		JLabel label_2 = new JLabel("进度:");
		label_2.setBounds(253, 671, 72, 18);
		frame.getContentPane().add(label_2);

		label_Info = new JLabel();
		label_Info.setBounds(14, 671, 200, 18);
		frame.getContentPane().add(label_Info);

		progressBar = new JProgressBar();
		progressBar.setBounds(306, 668, 404, 21);
		frame.getContentPane().add(progressBar);

	}

	@Override
	public void onReceive(byte[] bytes) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSendErr(int code) {
		// TODO Auto-generated method stub

	}

	class MyThread extends Thread {

		public boolean flag = true;
		int i = 0;

		@Override
		public void run() {
			while (flag) {
				System.out.println(i);
				i++;
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}

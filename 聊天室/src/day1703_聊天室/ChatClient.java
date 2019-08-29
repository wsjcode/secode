package day1703_������;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

public class ChatClient {
	Socket s;
	BufferedReader in;
	PrintWriter out;
	String name;
	boolean flag;
	LinkedList<String> list = new LinkedList<>();

	public void lauch() {
		try {
			s = new Socket("172.233.3.49", 8000);
			in = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
			out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"));
			System.out.println("���Լ�������֣�");

			name = new Scanner(System.in).nextLine();
			out.println(name);
			out.flush();
			// �����ڿ���̨�����������ݣ������߳�
			new Thread() {
				public void run() {
					Input();
				}
			}.start();
			// �����ڷ����������������ݣ������߳�
			new Thread() {
				public void run() {
					recieve();
				}
			}.start();
			new Thread() {
				public void run() {
					print();
				}
			}.start();
		} catch (Exception e) {
			System.out.println("�޷����������ҷ�����");
			e.printStackTrace();
		}
	}

	protected void print()  {
		while(true) {
			synchronized (list) {
				while(list.isEmpty()||flag) {
					try {
						list.wait();
					} catch (InterruptedException e) {
						
					}
				}
				String msg=list.removeFirst();
				System.out.println(msg);
			}
		
		}

	}

	protected void recieve() {

		try {
			String line;
			while ((line = in.readLine()) != null) {
				synchronized (list) {
					list.add(line);
					list.notifyAll();
				}

			}

		} catch (Exception e) {
			System.out.println("�����ѶϿ�");
		}
	}

	protected void Input() {
		System.out.println("���س�������������");
		
		// ����
		while (true) {
			 new Scanner(System.in).nextLine();
			 flag=true;//��������
			System.out.println("�����������ݣ�");
			String str = new Scanner(System.in).nextLine();
			out.println(name+":"+str);
			out.flush();
			flag=false;
			synchronized (list) {
				list.notifyAll();
			}
		}

	}

	public static void main(String[] args) {
		ChatClient c = new ChatClient();
		c.lauch();
	}
}

package day1703_������;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class ChatServer {
	private static ArrayList<TongxinThread> list = new ArrayList<>();

	public void launch() {
		new Thread() {
			@Override
			public void run() {
				try {
					ServerSocket ss = new ServerSocket(8000);
					System.out.println("");
					while (true) {
						Socket s = ss.accept();
						TongxinThread t = new TongxinThread(s);
						t.start();
						System.out.println("һ���ͻ���������");
					}
				} catch (Exception e) {
					System.out.println("�������޷������������쳣ֹͣ");
				}
			}

		}.start();
	}

	static class TongxinThread extends Thread {
		Socket s;
		BufferedReader in;
		PrintWriter out;
		private String name;

		public void send(String msg) {
			out.println(msg);
			out.flush();
		}

		public void sendAll(String msg) {
			synchronized (list) {
				for (TongxinThread t : list) {
					t.send(msg);
				}
			}

		}

		public TongxinThread(Socket s) {

			this.s = s;
		}

		@Override
		public void run() {
			try {
				
				
				in = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
				out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"));
				// �ӿͻ��˽���һ���ǳ�
				this.name = in.readLine();
				send("��ӭ��������ӻ���");
				// �ѵ�ǰͨ���߳�ʵ�����뼯��
				s.setSoTimeout(3000);//�������ݵĵȴ���ʱʱ��
				synchronized (list) {
					list.add(this);
				}
				// Ⱥ��һ��������Ϣ
				sendAll(name + "������������,����������" + list.size());
				String line;
				int count=0;
				while (true) {
					try {
						line=in.readLine();
					} catch (SocketTimeoutException e) {
                          send("***********[ϵͳ��Ϣ]�������������**********");
                          count++;
                          if(count==4) {
                        	  send("***********[ϵͳ��Ϣ]���Ѿ����߳�������**********");
                        	  s.close();
                        	  break;
                          }
                          continue;
					}
					
					if(line==null) {
						break;
					}
					count=0;
					sendAll(line);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			synchronized (list) {
				list.remove(this); 
			}

			sendAll(name + "�����ߣ�����������" + list.size());
		}

	}
	public static void main(String[] args) {
		ChatServer s = new ChatServer();
		s.launch();
		String str="abc";
		str.trim();
	}
}

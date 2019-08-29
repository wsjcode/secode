package day1703_聊天室;

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
						System.out.println("一个客户端已连接");
					}
				} catch (Exception e) {
					System.out.println("服务器无法启动后或服务异常停止");
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
				// 从客户端接受一个昵称
				this.name = in.readLine();
				send("欢迎进入解忧杂货店");
				// 把当前通信线程实例加入集合
				s.setSoTimeout(3000);//接收数据的等待超时时长
				synchronized (list) {
					list.add(this);
				}
				// 群发一个上线消息
				sendAll(name + "进入了聊天室,在线人数：" + list.size());
				String line;
				int count=0;
				while (true) {
					try {
						line=in.readLine();
					} catch (SocketTimeoutException e) {
                          send("***********[系统消息]请积极参与聊天**********");
                          count++;
                          if(count==4) {
                        	  send("***********[系统消息]您已经被踢出聊天室**********");
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

			sendAll(name + "已下线，在线人数：" + list.size());
		}

	}
	public static void main(String[] args) {
		ChatServer s = new ChatServer();
		s.launch();
		String str="abc";
		str.trim();
	}
}

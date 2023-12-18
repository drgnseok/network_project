import java.io.*;
import java.net.*;


public class ServerSocketThread extends Thread {
	Socket socket;
	ChatServer server;
	BufferedReader in;		// 입력 담당 클래스
	PrintWriter out;		// 출력 담당 클래스
	String name;
	String threadName;
	
	public ServerSocketThread(ChatServer server, Socket socket) {
		this.server = server;
		this.socket = socket;
		threadName = super.getName();	// Thread 이름을 얻어옴
		System.out.println(socket.getInetAddress() + "님이 입장하였습니다.");	// IP주소 얻어옴
		System.out.println("Thread Name : " + threadName);
	}
	// 클라이언트로 메시지 출력
	public void sendMessage(String str) {
		out.println(str);
	}
	// 쓰레드
		@Override
		public void run() {
			try {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				// true : autoFlush 설정
				out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
				
				sendMessage("대화자 이름을 넣으세요");
				name = in.readLine();
				server.broadCasting("[" + name + "]님이 입장하셨습니다.");
				
				while(true) {
					String str_in = in.readLine();
					out.println("[" + name + "] " + str_in);
				}
			} catch (IOException e) {
				System.out.println(threadName + " 퇴장했습니다.");
				server.removeClient(this);
				e.printStackTrace();
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	
	// 클라이언트로부터 파일을 받는 메소드
    public void receiveFile(String filePath) {
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            byte[] buffer = new byte[4096];
            
            int filesize = 2048; // 파일 크기를 얻어옵니다. 이 값은 실제로는 클라이언트로부터 받아야 합니다.
            int read = 0;
            int totalRead = 0;
            int remaining = filesize;
            
            while((read = socket.getInputStream().read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
                totalRead += read;
                remaining -= read;
                fos.write(buffer, 0, read);
            }
            
            fos.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    
 // 클라이언트에게 파일을 전송하는 메소드
    public void sendFile(String filePath) {
        try {
            File myFile = new File(filePath);
            byte[] mybytearray = new byte[(int) myFile.length()];
            
            FileInputStream fis = new FileInputStream(myFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            bis.read(mybytearray, 0, mybytearray.length);

            OutputStream os = socket.getOutputStream();
            os.write(mybytearray, 0, mybytearray.length);
            os.flush();
            
            bis.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
}
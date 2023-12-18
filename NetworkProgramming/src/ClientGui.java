import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;


public class ClientGui extends JFrame implements ActionListener, Runnable{
	// 클라이언트 화면용
	Container container = getContentPane();
	JTextArea textArea = new JTextArea();
	JScrollPane scrollPane = new JScrollPane(textArea);
	JTextField textField = new JTextField();
	
	// 파일 전송 및 수신 버튼을 추가
    JButton sendButton, receiveButton;
	
	// 통신용
	Socket socket;
	PrintWriter out;
	BufferedReader in;
	String str; 		// 채팅 문자열 저장
	ServerSocketThread serverThread; // ServerSocketThread 인스턴스를 저장하는 필드. 이 필드는 서버 스레드를 참조할 때 사용됩니다.

	
	public ClientGui(String ip, int port) {
		// frame 기본 설정
		setTitle("챗팅");
		setSize(550, 400);
		setLocation(400, 400);
		init();
		start();
		setVisible(true);
		// 통신 초기화
		initNet(ip, port);
		System.out.println("ip = " + ip);
	}	
	// 통신 초기화
	private void initNet(String ip, int port) {
		try {
			// 서버에 접속 시도
	        socket = new Socket(ip, port);
	        // 통신용 input, output 클래스 설정
	        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        // ture : auto flush 설정
	        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
	        // 쓰레드 구동
	        //serverThread = new ServerSocketThread(, socket); // ServerSocketThread 인스턴스 생성
	        //Thread thread = new Thread(serverThread);
	        //thread.start();
		} catch (UnknownHostException e) {
			System.out.println("IP 주소가 다릅니다.");
			//e.printStackTrace();
		} catch (IOException e) {
			System.out.println("접속 실패");
			//e.printStackTrace();
		}
//		// 쓰레드 구동
		Thread thread = new Thread(this); // run 함수 -> this
		thread.start();
	}
	private void init() {
        container.setLayout(new BorderLayout());
        container.add("Center", scrollPane);
        container.add("South", textField);

        JPanel buttonPanel = new JPanel();
        sendButton = new JButton("Send File");
        receiveButton = new JButton("Receive File");
        buttonPanel.add(sendButton);
        buttonPanel.add(receiveButton);

        container.add("North", buttonPanel);  // 버튼 패널을 프레임에 추가

        // 파일 전송 버튼 이벤트 리스너
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int ret = fileChooser.showOpenDialog(null);
                if (ret == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    // TODO: 파일 전송 코드를 여기에 구현해야 합니다.
                    serverThread.sendFile(file.getPath()); // 서버에게 파일 전송 요청을 보냅니다.
                }
            }
        });

        // 파일 수신 버튼 이벤트 리스너
        receiveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int ret = fileChooser.showSaveDialog(null);
                if (ret == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    // TODO: 파일 수신 코드를 여기에 구현해야 합니다.
                    serverThread.receiveFile(file.getPath()); // 서버에게 파일 수신 요청을 보냅니다.
                }
            }
        });
    }
	private void start() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		textField.addActionListener(this);
	}
	// 응답 대기
    // -> 서버로부터 응답으로 전달된 문자열을 읽어서, textArea에 출력하기
    @Override
    public void run() {
        while(true) {
            try {
                str = in.readLine();
                textArea.append(str + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        // textField의 문자열을 읽어와서 서버로 전송함
        str = textField.getText();
        out.println(str);
        // textField 초기화
        textField.setText("");
    }
}
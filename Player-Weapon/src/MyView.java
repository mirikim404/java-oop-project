import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JProgressBar;
import javax.swing.ImageIcon;
import java.awt.Color;
import javax.swing.JTextArea;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JScrollPane;

public class MyView extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JButton p1AttackButton;
	private JLabel imgLabel1;
	private JPanel panel1;
	private JProgressBar progressBar2;
	private JPanel panel2;
	private JLabel imgLabel2;
	private JButton p2AttackButton;
	private JScrollPane scrollPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MyView frame = new MyView();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MyView() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 779, 603);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		panel1 = new JPanel();
		panel1.setBounds(12, 39, 259, 334);
		panel1.setBackground(new Color(128, 128, 128));
		contentPane.add(panel1);
		panel1.setLayout(null);
		
		imgLabel1 = new JLabel("");
		imgLabel1.setHorizontalAlignment(SwingConstants.CENTER);
		imgLabel1.setIcon(new ImageIcon(MyView.class.getResource("/images/블랙위도우.png")));
		imgLabel1.setBounds(12, 10, 235, 271);
		panel1.add(imgLabel1);
		
		p1AttackButton = new JButton("공격하기");
		p1AttackButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        // p1이 p2를 공격
		        p1.attack(p2);
		        
		        // p2 HP가 0 이하면 0으로 고정
		        if (p2.getHp() < 0) p2.setHp(0);
		        
		        // progressBar2 업데이트
		        progressBar2.setValue(p2.getHp());
		        
		        // p2가 죽으면 버튼 비활성화
		        if (p2.getHp() <= 0) {
		            p1AttackButton.setEnabled(false);
		            p2AttackButton.setEnabled(false);
		        }
		    }
		});
		p1AttackButton.setFont(new Font("나눔고딕", Font.PLAIN, 14));
		p1AttackButton.setBounds(12, 291, 235, 33);
		panel1.add(p1AttackButton);
		
		progressBar1 = new JProgressBar();
		progressBar1.setBounds(12, 383, 259, 14);
		progressBar1.setValue(100);
		progressBar1.setForeground(new Color(255, 0, 0));
		contentPane.add(progressBar1);
		
		panel2 = new JPanel();
		panel2.setBounds(494, 39, 259, 334);
		panel2.setLayout(null);
		panel2.setBackground(Color.GRAY);
		contentPane.add(panel2);
		
		imgLabel2 = new JLabel("");
		imgLabel2.setIcon(new ImageIcon(MyView.class.getResource("/images/헐크.png")));
		imgLabel2.setHorizontalAlignment(SwingConstants.CENTER);
		imgLabel2.setBounds(12, 10, 235, 271);
		panel2.add(imgLabel2);
		
		p2AttackButton = new JButton("공격하기");
		p2AttackButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        // p2가 p1을 공격
		        p2.attack(p1);
		        
		        if (p1.getHp() < 0) p1.setHp(0);
		        
		        // progressBar1 업데이트
		        progressBar1.setValue(p1.getHp());
		        
		        if (p1.getHp() <= 0) {
		            p1AttackButton.setEnabled(false);
		            p2AttackButton.setEnabled(false);
		        }
		    }
		});
		p2AttackButton.setFont(new Font("나눔고딕", Font.PLAIN, 14));
		p2AttackButton.setBounds(12, 291, 235, 33);
		panel2.add(p2AttackButton);
		
		progressBar2 = new JProgressBar();
		progressBar2.setBounds(494, 383, 259, 14);
		progressBar2.setValue(100);
		progressBar2.setForeground(Color.RED);
		contentPane.add(progressBar2);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(283, 39, 199, 334);
		contentPane.add(scrollPane);

	}
	
	Player p1, p2;
	private JProgressBar progressBar1;
	
	public MyView(Player p1, Player p2) {
		this();
		System.out.println("!!!");
		this.p1 = p1;
		this.p2 = p2;
		//이미지 세팅하기 
		imgLabel1.setIcon(new ImageIcon(MyView.class.getResource("/images/" + this.p1.getFilename())));
		imgLabel2.setIcon(new ImageIcon(MyView.class.getResource("/images/" + this.p2.getFilename())));
		//value 세팅하기
		this.progressBar1.setMaximum(this.p1.getHp());
		this.progressBar2.setMaximum(this.p2.getHp());
		this.progressBar1.setValue(this.p1.getHp());
		this.progressBar2.setValue(this.p2.getHp());
		
	}
	
	
}

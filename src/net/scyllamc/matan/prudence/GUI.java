package net.scyllamc.matan.prudence;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.commons.validator.UrlValidator;

import net.scyllamc.matan.prudence.parser.ParseTask;
import net.scyllamc.matan.prudence.prediction.PerusalTask;

import javax.swing.JLabel;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;

@SuppressWarnings("deprecation")
public class GUI extends JFrame {

	public static String newLine = System.getProperty("line.separator");

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	public static JTextArea inputParse;
	public static JProgressBar barParse;
	public static JLabel labelCount;
	public static JTextPane inputProb;
	public static JLabel labelTime;

	public static void main(String[] args) {

		GUI frame = new GUI();
		frame.setVisible(true);

	}

	public GUI() {

		setTitle("Prudence " + Main.version);
		setDefaultCloseOperation(3);
		setBounds(100, 100, 454, 749);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(6, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JButton btnParse = new JButton();
		btnParse.setBounds(0, 311, 164, 29);
		btnParse.setText("Try parse");

		JLabel lblNewLabel = new JLabel("Made by Matan Rak, 2017");
		lblNewLabel.setBounds(6, 705, 425, 16);
		contentPane.add(lblNewLabel);
		this.contentPane.add(btnParse);

		JLabel lblSetTheDirectory = new JLabel("Set the directory");
		lblSetTheDirectory.setBounds(6, 19, 233, 16);
		contentPane.add(lblSetTheDirectory);

		JLabel lblEnterDataTo = new JLabel("Enter data to parse");
		lblEnterDataTo.setBounds(10, 76, 176, 16);
		contentPane.add(lblEnterDataTo);

		inputParse = new JTextArea();
		inputParse.setBounds(11, 104, 299, 195);
		contentPane.add(inputParse);

		JScrollPane scroll = new JScrollPane(inputParse);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setBounds(11, 104, 299, 195);
		contentPane.add(scroll);

		barParse = new JProgressBar(0, 100);
		barParse.setBounds(164, 320, 146, 20);
		contentPane.add(barParse);

		labelCount = new JLabel("Global word count: " + Main.wordCount);
		labelCount.setBounds(6, 378, 392, 16);
		contentPane.add(labelCount);

		labelTime = new JLabel("Time: ");
		labelTime.setBounds(320, 324, 126, 16);
		contentPane.add(labelTime);

		inputProb = new JTextPane();
		inputProb.setBounds(6, 485, 310, 191);
		inputProb.addKeyListener(new KeyAdapter() {

			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {

					String[] words = inputProb.getText().split("\\s+");
					String s = words[words.length - 1].replaceAll("[^a-zA-Z]", "");

					Word word = Word.getWord(s);
					
					new PerusalTask(word, inputProb.getText().split("\\s+"), null);
					/**
					Word prob = word.getProbableAfterWord(inputProb.getText());

					if (prob != null) {
						inputProb.setText(inputProb.getText() + " " + prob.toString());
					}
					**/
					
				}

			}
		});
		contentPane.add(inputProb);

		JScrollPane scrollBar = new JScrollPane(inputProb);
		scrollBar.setBounds(6, 485, 304, 191);
		contentPane.add(scrollBar);

		JLabel lblEnterAWord = new JLabel("Enter a word and press space to auto suggest");
		lblEnterAWord.setBounds(6, 457, 304, 16);
		contentPane.add(lblEnterAWord);


		btnParse.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {

				if (inputParse.getText().length() > 0) {

					if (new UrlValidator(new String[] { "http", "https" }).isValid(inputParse.getText())) {
					
					} else {
						
						//Parser.Parse(inputParse.getText());
						new ParseTask("Given text", inputParse.getText());
					}

				}
			}
		});

	}
	
	
	public static void updateTime(String s) {
		labelTime.setText("Time: " + s);
	}
	
	public static void updateBar(double per) {
		barParse.setValue(((int) Math.round(per)));
	}
	

}

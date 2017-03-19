package net.scyllamc.matan.prudence;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.swing.JLabel;
import javax.swing.JTextField;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;

public class main extends JFrame {

	public static String version = "0.2";
	public static String newLine = System.getProperty("line.separator");

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private static JTextField inputDir;
	private static JTextArea inputParse;
	private static JProgressBar barParse;
	private static JLabel labelCount;
	private static JTextPane inputProb;
	private static File config;
	private static Integer wordCount = -1;
	private static String defaultDir = "/Users/matanrak/AI_DATA";
	private static JLabel labelTime;
	

	public static void main(String[] args) {

		main frame = new main();
		frame.setVisible(true);

	}

	public main() {

		setTitle("Auto suggest AI Version " + version);
		setDefaultCloseOperation(3);
		setBounds(100, 100, 454, 749);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(6, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JButton btnParse = new JButton();
		btnParse.setBounds(0, 311, 164, 29);
		btnParse.setText("Try parse");

		btnParse.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				Parser.Parse(inputParse.getText());
			}
		});

		JLabel lblNewLabel = new JLabel("Made by Matan Rak, 2017");
		lblNewLabel.setBounds(6, 705, 425, 16);
		contentPane.add(lblNewLabel);
		this.contentPane.add(btnParse);

		JLabel lblSetTheDirectory = new JLabel("Set the directory");
		lblSetTheDirectory.setBounds(6, 19, 233, 16);
		contentPane.add(lblSetTheDirectory);

		inputDir = new JTextField();
		inputDir.setText(main.defaultDir);
		inputDir.setBounds(0, 38, 276, 26);
		contentPane.add(inputDir);
		inputDir.setColumns(10);

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

		labelCount = new JLabel("Global word count: 0");
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
					System.out.println("Trying to get a probable word");

					String[] words = inputProb.getText().split("\\s+");
					String s = words[words.length - 1].replaceAll("[^a-zA-Z]", "");

					Word word = Word.getWord(s);

					Word prob = word.getProbableAfterWord(inputProb.getText());

					if (prob != null) {
						inputProb.setText(inputProb.getText() + " " + prob.toString());
					}

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

		config = new File(getDir() + File.separator + "_CONFIG_.json");
		wordCount = getTotalWordCount();

	}

	public static void updateTime(String s) {
		labelTime.setText("Time: " + s);
	}

	public static Integer getTotalWordCount() {

		if (main.wordCount == -1) {

			if (config.exists()) {

				BufferedReader reader;
				JsonObject obj = null;

				try {
					reader = new BufferedReader(new FileReader(config));
					obj = new Gson().fromJson(reader, JsonObject.class);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				if (obj.has("globalCount")) {
					main.wordCount = Integer.parseInt(obj.get("globalCount").toString());
				}

			} else {

				addWordCount(0);
				main.wordCount = 0;

			}

		}

		labelCount.setText("Global word count: " + main.wordCount + " Cache: "  + Word.cache.size());

		
		return main.wordCount;
	}

	public static Integer addWordCount(Integer i) {

		main.wordCount += i;
		JsonObject obj = new JsonObject();
		obj.addProperty("globalCount", main.wordCount);

		if (config.exists()) {

			BufferedReader reader;

			try {
				reader = new BufferedReader(new FileReader(config));
				obj = new Gson().fromJson(reader, JsonObject.class);

				if (obj.has("globalCount")) {
					obj.remove("globalCount");
				}

				obj.addProperty("globalCount", main.wordCount);

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		try {
			FileWriter writer = new FileWriter(config);
			writer.write(obj.toString());
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		getTotalWordCount();

		return main.wordCount;
	}

	public static String getDir() {

		if (inputDir == null || inputDir.getText() == null) {
			return main.defaultDir;
		}
		return inputDir.getText();
	}

	public static void updateBar(double per) {
		barParse.setValue(((int) Math.round(per)));
	}

	public static int itemCount(String s, Word w) {

		String[] words = s.split("\\s+");
		int count = 0;

		for (int i = 0; i < words.length; i++) {

			if (words[i].equalsIgnoreCase(w.toString())) {
				count++;
			}
		}
		
		return count;
	}
}

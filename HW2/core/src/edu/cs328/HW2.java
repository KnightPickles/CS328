package edu.cs328;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.*;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;


public class HW2 extends ApplicationAdapter {
	ShapeRenderer shapeRenderer;
	Batch batch;
	OrthographicCamera camera;

	GlyphLayout layout = new GlyphLayout();
	BitmapFont font;

	String word;
	int letters;
	int guesses;
	int remaining;
	boolean[] correctGuesses;
	ArrayList<Character> wrongGuesses;
	boolean allFound = true;
	char[] validChars = "abcdefghijklmnopqrstuvwxyz".toCharArray();

	@Override
	public void create () {
		camera = new OrthographicCamera(840, 480);
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setColor(0, 0, 0, 1);
		font = new BitmapFont();
		font.setColor(0, 0, 0, 1);

		wrongGuesses = new ArrayList<Character>();
		newGame();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		shapeRenderer.begin(ShapeType.Line);

		// Word box
		shapeRenderer.rect(130, 370, 130, 50);

		// Hangmans stand x + 100 y - 200
		shapeRenderer.line(520, 420, 520, 430);
		shapeRenderer.line(520, 430, 650, 430);
		shapeRenderer.line(650, 430, 650, 50);
		shapeRenderer.line(500, 50, 700, 50);

		// Man
		if (guesses > 0) shapeRenderer.circle(520, 380, 40); // head
		if (guesses > 2) shapeRenderer.line(520, 340, 520, 200); // body
		if (guesses > 4) shapeRenderer.line(520, 340, 420, 240); // left arm
		if (guesses > 6) shapeRenderer.line(520, 340, 620, 240); // right arm
		if (guesses > 8) shapeRenderer.line(520, 200, 420, 100); // left leg
		if (guesses >= 10) shapeRenderer.line(520, 200, 620, 100); // right leg

		batch.begin();

		// Draw discovered letters and a line for each letter
		int spacing = 220 - 10 * letters;
		for (int i = 0; i < letters; i++) {
			shapeRenderer.line(spacing + i * 25, 50, spacing + i * 25 + 20, 50);
			if (correctGuesses[i])
				drawFont(Character.toString(word.charAt(i)), spacing + i * 25 + 10, 75, true);
		}

		// Draw incorrect chars
		int boxOffset = 25 - 10 * letters;
		for(int i = 0; i < wrongGuesses.size(); i++) {
			drawFont(Character.toString(wrongGuesses.get(i)).toLowerCase(), 140 + i * 10, 420, true);
		}

		// Are all the letters guessed?
		allFound = true;
		for(int i = 0; i < correctGuesses.length; i++)
			if(!correctGuesses[i]) allFound = false;

		drawFont("Guesses Left: " + Integer.toString(10 - guesses), 130, 360, false);

		if(wrongGuesses.size() >= 10) {
			drawFont("You've Lost!", 200, 300, true);
			drawFont("Press escape to restart.", 200, 280, true);
		} else if(allFound) {
			drawFont("You've Won!", 200, 300, true);
			drawFont("Press escape to restart.", 200, 280, true);
		}

		batch.end();
		shapeRenderer.end();

		if (guesses < 10)
			getInput();

		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			newGame();
			allFound = true;
		}
	}

	public void newGame() {
		try {
			newWord();
		} catch (IOException e) {
			e.printStackTrace();
		}
		guesses = 0;
		remaining = 10;
		wrongGuesses.clear();
		correctGuesses = new boolean[letters];
		Arrays.fill(correctGuesses, Boolean.FALSE);
	}

	public void newWord() throws IOException {
		File f = new File("words.txt");
		word = null;
		Random rand = new Random();
		int n = 0;
		for(Scanner sc = new Scanner(f); sc.hasNext(); ) {
			++n;
			String line = sc.nextLine();
			if(rand.nextInt(n) == 0)
				word = line;
		}
		letters = word.length();
		System.out.println(word);
	}

	public void drawFont(String text, int x, int y, boolean center) {
		layout.setText(font, text);
		float w = layout.width;
		float h = layout.height;
		if (center) {
			font.draw(batch, text, x - w / 2, y - h / 2);
		} else font.draw(batch, text, x, y);
	}

	public void getInput() {
		for (char c : validChars) {
			if (Gdx.input.isKeyJustPressed(Input.Keys.valueOf(Character.toString(c).toUpperCase()))) {
				if (word.indexOf(c) >= 0) { // Correct guess
					for(int index = word.indexOf(c); index >= 0; index = word.indexOf(c, index + 1))
						correctGuesses[index] = true;
				} else {
					if (wrongGuesses.contains(c)) continue;
					wrongGuesses.add(c);
					guesses++;
				}
			}
		}
	}
}
package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	BitmapFont font;
	Pixmap pixMap;
	Texture texture;
	Sprite sprite;
	
	@Override
	public void create () {
		pixMap = new Pixmap(64, 64, Pixmap.Format.RGBA4444);
		pixMap.setColor(Color.RED);
		pixMap.fill();
		pixMap.setColor(Color.NAVY);
		pixMap.drawLine(0,  0, pixMap.getWidth() - 1, pixMap.getHeight() - 1);
		pixMap.drawLine(0, pixMap.getHeight(), pixMap.getWidth() - 1, 0);
		pixMap.setColor(Color.YELLOW);
		pixMap.drawCircle(pixMap.getWidth() / 2, pixMap.getHeight() / 2, pixMap.getWidth() / 2 - 1);
		
		texture = new Texture(pixMap);
		pixMap.dispose();
		
		sprite = new Sprite(texture);
		
		batch = new SpriteBatch();
		FileHandle fontFile = Gdx.files.internal("font/moonhouse-15.fnt");
		font = new BitmapFont(fontFile, false);
		font.setColor(Color.RED);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		font.draw(batch, "Hello World Test", 50, 50);
		sprite.setPosition(100,  100);
		sprite.draw(batch);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		font.dispose();
	}
}

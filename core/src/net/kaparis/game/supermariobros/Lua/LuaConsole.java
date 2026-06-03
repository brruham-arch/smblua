package net.kaparis.game.supermariobros.Lua;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class LuaConsole {
    private Stage stage;
    private Table consoleTable;
    private TextField inputField;
    private Label statusLabel;
    private TextButton toggleBtn;
    private boolean visible = false;
    private LuaEngine engine;
    private Skin skin;

    public LuaConsole(SpriteBatch batch, LuaEngine engine) {
        this.engine = engine;
        stage = new Stage(new FitViewport(800, 480), batch);

        // buat skin minimal
        skin = new Skin();
        BitmapFont font = new BitmapFont();
        skin.add("default-font", font);

        // style label
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);

        // style textfield
        TextField.TextFieldStyle tfStyle = new TextField.TextFieldStyle();
        tfStyle.font = font;
        tfStyle.fontColor = Color.WHITE;
        tfStyle.cursor = skin.newDrawable("white", Color.WHITE);
        tfStyle.selection = skin.newDrawable("white", new Color(0.3f,0.3f,1f,0.5f));
        tfStyle.background = skin.newDrawable("white", new Color(0,0,0,0.7f));
        skin.add("default", tfStyle);

        // style button
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = font;
        btnStyle.fontColor = Color.YELLOW;
        btnStyle.overFontColor = Color.WHITE;
        skin.add("default", btnStyle);

        // tombol toggle ">" di pojok kanan bawah
        toggleBtn = new TextButton("[LUA]", skin);
        toggleBtn.setPosition(720, 5);
        toggleBtn.setSize(70, 25);
        toggleBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleConsole();
            }
        });
        stage.addActor(toggleBtn);

        // console panel
        consoleTable = new Table();
        consoleTable.setPosition(10, 10);
        consoleTable.setSize(700, 40);
        consoleTable.setVisible(false);

        statusLabel = new Label("", skin);
        statusLabel.setColor(Color.CYAN);

        inputField = new TextField("", skin);
        inputField.setMessageText("ketik lua disini...");
        inputField.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == com.badlogic.gdx.Input.Keys.ENTER) {
                    executeCommand(inputField.getText());
                    inputField.setText("");
                    return true;
                }
                return false;
            }
        });

        consoleTable.add(inputField).width(540).height(30).padRight(5);

        TextButton execBtn = new TextButton("GO", skin);
        execBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                executeCommand(inputField.getText());
                inputField.setText("");
            }
        });
        consoleTable.add(execBtn).width(50).height(30).padRight(5);
        consoleTable.add(statusLabel).width(90).height(30);

        stage.addActor(consoleTable);

        Gdx.input.setInputProcessor(stage);
    }

    private void toggleConsole() {
        visible = !visible;
        consoleTable.setVisible(visible);
        if (visible) {
            stage.setKeyboardFocus(inputField);
            Gdx.input.setOnscreenKeyboardVisible(true);
        } else {
            Gdx.input.setOnscreenKeyboardVisible(false);
        }
    }

    private void executeCommand(String cmd) {
        if (cmd == null || cmd.trim().isEmpty()) return;
        cmd = cmd.trim();
        setStatus("OK: " + cmd);

        // shortcut commands
        if (cmd.equals("mushroom")) {
            engine.execLua("game.spawnMushroom(mario.getX(), mario.getY()+1)");
        } else if (cmd.equals("big")) {
            engine.execLua("mario.grow()");
        } else if (cmd.equals("kill")) {
            engine.execLua("mario.kill()");
        } else if (cmd.equals("nogravity")) {
            engine.execLua("game.setGravity(0,0)");
        } else if (cmd.equals("normalgravity")) {
            engine.execLua("game.setGravity(0,-10)");
        } else if (cmd.startsWith("score ")) {
            engine.execLua("game.setScore(" + cmd.substring(6) + ")");
        } else {
            // eksekusi langsung sebagai Lua
            engine.execLua(cmd);
        }
    }

    public void setStatus(String msg) {
        statusLabel.setText(msg);
    }

    public void draw() {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void resize(int w, int h) {
        stage.getViewport().update(w, h, true);
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    public Stage getStage() { return stage; }
}

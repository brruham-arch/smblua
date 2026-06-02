package net.kaparis.game.supermariobros.Lua;

import com.badlogic.gdx.Gdx;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.jse.*;

public class LuaEngine {
    private static final String TAG = "LuaEngine";
    private static final String SCRIPT_PATH = "/sdcard/mario.lua";

    private Globals globals;
    private GameAPI api;
    private float reloadTimer = 0;
    private static final float RELOAD_INTERVAL = 1.0f;

    public LuaEngine(GameAPI api) {
        this.api = api;
        globals = JsePlatform.standardGlobals();
        registerAPI();
        loadScript();
    }

    private void registerAPI() {
        // mario table
        LuaTable mario = new LuaTable();
        mario.set("getX",         api.mario_getX);
        mario.set("getY",         api.mario_getY);
        mario.set("getVelX",      api.mario_getVelX);
        mario.set("getVelY",      api.mario_getVelY);
        mario.set("isBig",        api.mario_isBig);
        mario.set("isDead",       api.mario_isDead);
        mario.set("isOnGround",   api.mario_isOnGround);
        mario.set("setVelocity",  api.mario_setVelocity);
        mario.set("grow",         api.mario_grow);
        mario.set("kill",         api.mario_kill);
        globals.set("mario", mario);

        // game table
        LuaTable game = new LuaTable();
        game.set("spawnMushroom", api.game_spawnMushroom);
        game.set("spawnGoomba",   api.game_spawnGoomba);
        game.set("setGravity",    api.game_setGravity);
        game.set("getScore",      api.game_getScore);
        game.set("setScore",      api.game_setScore);
        game.set("getTime",       api.game_getTime);
        game.set("setTime",       api.game_setTime);
        globals.set("game", game);

        // input table
        LuaTable input = new LuaTable();
        input.set("isLeft",       api.input_isLeft);
        input.set("isRight",      api.input_isRight);
        input.set("isJump",       api.input_isJump);
        input.set("isFire",       api.input_isFire);
        input.set("setLeft",      api.input_setLeft);
        input.set("setRight",     api.input_setRight);
        input.set("setJump",      api.input_setJump);
        globals.set("input", input);

        // camera table
        LuaTable camera = new LuaTable();
        camera.set("getX",        api.camera_getX);
        camera.set("setX",        api.camera_setX);
        globals.set("camera", camera);

        // hud table
        LuaTable hud = new LuaTable();
        hud.set("showMessage",    api.hud_showMessage);
        globals.set("hud", hud);
    }

    private void loadScript() {
        try {
            java.io.File f = new java.io.File(SCRIPT_PATH);
            if (!f.exists()) return;
            LuaValue chunk = globals.loadfile(SCRIPT_PATH);
            chunk.call();
            Gdx.app.log(TAG, "Script loaded OK");
        } catch (Exception e) {
            Gdx.app.error(TAG, "Script error: " + e.getMessage());
        }
    }

    public void update(float dt) {
        reloadTimer += dt;
        if (reloadTimer >= RELOAD_INTERVAL) {
            reloadTimer = 0;
            loadScript();
        }
        callHook("onUpdate", LuaValue.valueOf(dt));
    }

    public void onMarioDead() {
        callHook("onMarioDead");
    }

    public void onItemCollected(String type) {
        callHook("onItemCollected", LuaValue.valueOf(type));
    }

    public void onEnemyKilled(String type, float x, float y) {
        callHook("onEnemyKilled",
            LuaValue.valueOf(type),
            LuaValue.valueOf(x),
            LuaValue.valueOf(y));
    }

    private void callHook(String name, LuaValue... args) {
        try {
            LuaValue fn = globals.get(name);
            if (fn.isnil() || !fn.isfunction()) return;
            fn.invoke(LuaValue.varargsOf(args));
        } catch (Exception e) {
            Gdx.app.error(TAG, name + " error: " + e.getMessage());
        }
    }
}

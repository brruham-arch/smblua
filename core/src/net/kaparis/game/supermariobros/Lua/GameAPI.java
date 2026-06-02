package net.kaparis.game.supermariobros.Lua;

import com.badlogic.gdx.math.Vector2;
import net.kaparis.game.supermariobros.Items.ItemDef;
import net.kaparis.game.supermariobros.Items.Mushroom;
import net.kaparis.game.supermariobros.Screens.PlayScreen;
import net.kaparis.game.supermariobros.Sprites.Mario;
import net.kaparis.game.supermariobros.Scenes.Hud;
import net.kaparis.game.supermariobros.Tools.Controller;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.*;

public class GameAPI {
    private PlayScreen screen;
    private Mario mario;
    private Hud hud;
    private Controller controller;

    // input overrides
    public boolean overrideLeft, overrideRight, overrideJump;

    public GameAPI(PlayScreen screen, Mario mario, Hud hud, Controller controller) {
        this.screen = screen;
        this.mario = mario;
        this.hud = hud;
        this.controller = controller;
        initFunctions();
    }

    private void initFunctions() { /* dipanggil di constructor, fungsi sudah di-assign di bawah */ }

    // ── MARIO ──
    public final LuaValue mario_getX = new ZeroArgFunction() {
        public LuaValue call() {
            return LuaValue.valueOf(mario.b2Body.getPosition().x);
        }
    };
    public final LuaValue mario_getY = new ZeroArgFunction() {
        public LuaValue call() {
            return LuaValue.valueOf(mario.b2Body.getPosition().y);
        }
    };
    public final LuaValue mario_getVelX = new ZeroArgFunction() {
        public LuaValue call() {
            return LuaValue.valueOf(mario.b2Body.getLinearVelocity().x);
        }
    };
    public final LuaValue mario_getVelY = new ZeroArgFunction() {
        public LuaValue call() {
            return LuaValue.valueOf(mario.b2Body.getLinearVelocity().y);
        }
    };
    public final LuaValue mario_isBig = new ZeroArgFunction() {
        public LuaValue call() {
            return LuaValue.valueOf(mario.isBig());
        }
    };
    public final LuaValue mario_isDead = new ZeroArgFunction() {
        public LuaValue call() {
            return LuaValue.valueOf(mario.currentState == Mario.State.DEAD);
        }
    };
    public final LuaValue mario_isOnGround = new ZeroArgFunction() {
        public LuaValue call() {
            return LuaValue.valueOf(mario.b2Body.getLinearVelocity().y == 0);
        }
    };
    public final LuaValue mario_setVelocity = new TwoArgFunction() {
        public LuaValue call(LuaValue vx, LuaValue vy) {
            mario.b2Body.setLinearVelocity((float)vx.todouble(), (float)vy.todouble());
            return LuaValue.NIL;
        }
    };
    public final LuaValue mario_grow = new ZeroArgFunction() {
        public LuaValue call() {
            mario.grow();
            return LuaValue.NIL;
        }
    };
    public final LuaValue mario_kill = new ZeroArgFunction() {
        public LuaValue call() {
            mario.currentState = net.kaparis.game.supermariobros.Sprites.Mario.State.DEAD;
            mario.b2Body.setLinearVelocity(new Vector2(0, 0));
            mario.b2Body.applyLinearImpulse(new Vector2(0, 4f), mario.b2Body.getWorldCenter(), true);
            return LuaValue.NIL;
        }
    };

    // ── GAME ──
    public final LuaValue game_spawnMushroom = new TwoArgFunction() {
        public LuaValue call(LuaValue x, LuaValue y) {
            screen.spawnItem(new ItemDef(
                new Vector2((float)x.todouble(), (float)y.todouble()),
                Mushroom.class));
            return LuaValue.NIL;
        }
    };
    public final LuaValue game_spawnGoomba = new TwoArgFunction() {
        public LuaValue call(LuaValue x, LuaValue y) {
            // TODO: implement spawn goomba
            return LuaValue.NIL;
        }
    };
    public final LuaValue game_setGravity = new TwoArgFunction() {
        public LuaValue call(LuaValue x, LuaValue y) {
            screen.getWorld().setGravity(new Vector2((float)x.todouble(), (float)y.todouble()));
            return LuaValue.NIL;
        }
    };
    public final LuaValue game_getScore = new ZeroArgFunction() {
        public LuaValue call() {
            return LuaValue.valueOf(Hud.score);
        }
    };
    public final LuaValue game_setScore = new OneArgFunction() {
        public LuaValue call(LuaValue v) {
            Hud.score = v.toint();
            return LuaValue.NIL;
        }
    };
    public final LuaValue game_getTime = new ZeroArgFunction() {
        public LuaValue call() {
            return LuaValue.valueOf(Hud.worldTimer);
        }
    };
    public final LuaValue game_setTime = new OneArgFunction() {
        public LuaValue call(LuaValue v) {
            Hud.worldTimer = v.toint();
            return LuaValue.NIL;
        }
    };

    // ── INPUT ──
    public final LuaValue input_isLeft = new ZeroArgFunction() {
        public LuaValue call() { return LuaValue.valueOf(controller.isLeftPressed()); }
    };
    public final LuaValue input_isRight = new ZeroArgFunction() {
        public LuaValue call() { return LuaValue.valueOf(controller.isRightPressed()); }
    };
    public final LuaValue input_isJump = new ZeroArgFunction() {
        public LuaValue call() { return LuaValue.valueOf(controller.isUpPressed()); }
    };
    public final LuaValue input_isFire = new ZeroArgFunction() {
        public LuaValue call() { return LuaValue.valueOf(controller.bPressed()); }
    };
    public final LuaValue input_setLeft = new OneArgFunction() {
        public LuaValue call(LuaValue v) { overrideLeft = v.toboolean(); return LuaValue.NIL; }
    };
    public final LuaValue input_setRight = new OneArgFunction() {
        public LuaValue call(LuaValue v) { overrideRight = v.toboolean(); return LuaValue.NIL; }
    };
    public final LuaValue input_setJump = new OneArgFunction() {
        public LuaValue call(LuaValue v) { overrideJump = v.toboolean(); return LuaValue.NIL; }
    };

    // ── CAMERA ──
    public final LuaValue camera_getX = new ZeroArgFunction() {
        public LuaValue call() { return LuaValue.valueOf(screen.getCamX()); }
    };
    public final LuaValue camera_setX = new OneArgFunction() {
        public LuaValue call(LuaValue v) { screen.setCamX((float)v.todouble()); return LuaValue.NIL; }
    };

    // ── HUD ──
    public final LuaValue hud_showMessage = new OneArgFunction() {
        public LuaValue call(LuaValue v) {
            hud.showMessage(v.tojstring());
            return LuaValue.NIL;
        }
    };
}

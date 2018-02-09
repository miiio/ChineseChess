package com.a510.chinesechess.Bean;

/**
 * Created by Lao on 2018/2/9.
 */

public class PlayerBean {
    /**
     * id : 1
     * name : player1
     * status : free
     * room_id : 0
     * ready : false
     * game_color : 0
     */

    private int id;
    private String name;
    private String status;
    private int room_id;
    private boolean ready;
    private int game_color;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public int getGame_color() {
        return game_color;
    }

    public void setGame_color(int game_color) {
        this.game_color = game_color;
    }
}

package com.a510.chinesechess.Bean;

import java.util.List;

/**
 * 游戏房间实体类
 * Created by Lao on 2018/2/8.
 */

public class RoomsBean {

    /**
     * type : all_rooms
     * data : [{"id":1,"pw":"","name":"name1","status":"waitting","player":[{"id":3,"name":"player3","status":"room","room_id":1,"ready":false,"game_color":0}],"duration":30,"game_data":""},{"id":2,"pw":"","name":"name2","status":"waitting","player":[{"id":4,"name":"player4","status":"room","room_id":2,"ready":false,"game_color":0}],"duration":30,"game_data":""}]
     */

    private String type;
    private List<DataBean> data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 1
         * pw :
         * name : name1
         * status : waitting
         * player : [{"id":3,"name":"player3","status":"room","room_id":1,"ready":false,"game_color":0}]
         * duration : 30
         * game_data :
         */

        private int id;
        private String pw;
        private String name;
        private String status;
        private int duration;
        private String game_data;
        private List<PlayerBean> player;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPw() {
            return pw;
        }

        public void setPw(String pw) {
            this.pw = pw;
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

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public String getGame_data() {
            return game_data;
        }

        public void setGame_data(String game_data) {
            this.game_data = game_data;
        }

        public List<PlayerBean> getPlayer() {
            return player;
        }

        public void setPlayer(List<PlayerBean> player) {
            this.player = player;
        }
    }
}

package com.tournaments.grindbattles.model;

import java.util.ArrayList;
import java.util.List;

public class SlotListModel {
    public List<Data> data=new ArrayList<>();
    public static class Data {
        public int id;
        public String user_id;
        public String user_name;
        public int slot;
        public String position;
        public boolean selected=false;

    }
}

package com.tournaments.grindbattles.model;

import java.util.ArrayList;
import java.util.List;

public class SlotListOrganizedModel {

    public List<Data> data=new ArrayList<>();
    public static class Data {
        public int slot;
        public List<Position> position=new ArrayList<>();
    }
    public  static  class Position{
        public String position;
        public boolean selected=false;
        public String user_id;
        public int id;
    }
}

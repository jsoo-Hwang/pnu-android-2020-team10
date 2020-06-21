package mainactivity;



public class alarmsItem {
    String alarms_names;
    String dayOfweek;
    String time;
    int hours;
    int minutes;
    boolean alarms_OnOff;
    int countDown;
    String nextDayOfWeek;
    String amPm;
    int dataBase_id;

    public int getDataBase_id() { return dataBase_id; }

    public void setDataBase_id(int dataBase_id) { this.dataBase_id = dataBase_id; }

    public String getNextDayOfWeek() { return nextDayOfWeek; }

    public void setNextDayOfWeek(String nextDayOfWeek) { this.nextDayOfWeek = nextDayOfWeek;
    }
    public String getAmPm() { return amPm; }

    public void setAmPm(String amPm) { this.amPm = amPm;}

    public int getHours() { return hours; }

    public void setHours(int hours) { this.hours = hours; }

    public int getMinutes() { return minutes; }

    public void setMinutes(int minutes) { this.minutes = minutes; }

    public boolean getAlarms_OnOff() { return alarms_OnOff; }

    public void setAlarms_OnOff(boolean alarms_OnOff) { this.alarms_OnOff = alarms_OnOff; }

    public String getAlarms_names() { return alarms_names; }

    public void setAlarms_names(String alarms_names) { this.alarms_names = alarms_names; }

    public String getDayOfweek() { return dayOfweek; }

    public void setDayOfweek(String dayOfweek) { this.dayOfweek = dayOfweek; }

    public String getTime() { return time; }

    public void setTime(String time) { this.time = time; }

    public int getCountDown() { return countDown; }

    public void setCountDown(int countDown) { this.countDown = countDown; }




}

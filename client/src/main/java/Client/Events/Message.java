package Client.Events;

public final class Message implements Comparable<Message> {
    private final String text;
    private final String username;
    private final long timeStamp;

    public Message(String text, String username, long timeStamp){
        this.text = text.replace("\n", "");
        this.username = username;
        this.timeStamp = timeStamp;
    }

    @Override
    public int compareTo(Message o) {
        if (timeStamp < o.getTimeStamp()){
            return -1;
        }else if (timeStamp > o.getTimeStamp()){
            return 1;
        }else{
            return username.compareTo(o.getUsername());
        }
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getUsername() {
        return username;
    }

    public String getText() {
        return text;
    }

    public String getFullText(){
        return username + "says: \""+ text + "\"";
    }
}

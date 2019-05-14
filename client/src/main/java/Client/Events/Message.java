package Client.Events;

/**
 * this class handles a single message that can be sent through the network.
 * or sent to a user.
 * @author Jim Spagnola
 */
public final class Message implements Comparable<Message> {
    private final String text;
    private final String username;
    private final long timeStamp;

    /**
     * its a constructor
     * @param text the text of the message
     * @param username the user that sent the message
     * @param timeStamp the causal consistent message that was sent for ordering.
     */
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

    private long getTimeStamp() {
        return timeStamp;
    }

    private String getUsername() {
        return username;
    }

    public String getText() {
        return text;
    }

    public String getFullText(){
        return username + " says: \""+ text + "\"";
    }
}

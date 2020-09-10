import java.util.Scanner;

class Message {
    public String cmd;// stop / init / msg / info
    public String from; // name
    public String to; // name / @  -- all
    public String text; // last teg!

    public Message(){}
    public Message(String msg){
        parseMessage(msg);
    }
    public Message(String cmd, String from, String to, String text){
        this.cmd = cmd;
        this.from = from;
        this.to = to;
        this.text = text;
    }
    public void parseMessage(String msg){
        Scanner sc = new Scanner(msg);
        while(sc.hasNext()){
            String word = sc.next();
            if(word.equals("@cmd")){
                cmd = sc.next();
            }else if(word.equals("@from")){
                from = sc.next();
            }else if(word.equals("@to")){
                to = sc.next();
            }else if(word.equals("@text")){
                text = sc.nextLine();
            }
        }
        sc.close();
    }
    
    @Override
    public String toString(){
        StringBuilder b = new StringBuilder();
        b.append(" @cmd ");
        b.append(cmd);
        b.append(" @from ");
        b.append(from);
        b.append(" @to ");
        b.append(to);
        b.append(" @text ");
        b.append(text);
        b.append("\n");
        return b.toString();
    }
}
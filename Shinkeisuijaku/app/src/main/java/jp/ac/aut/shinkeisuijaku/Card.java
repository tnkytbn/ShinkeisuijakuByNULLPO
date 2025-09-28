package jp.ac.aut.shinkeisuijaku;

public class Card {
    private String suit;//スート。イージーモードのカードの判定。
    private int num;//数字。数字が一致しているかの判定で使う。
    private int cardTableID;//
    private int  cardImageID;//
    private boolean alreadyOpen = false;//このオブジェクトがめくられているか状態判定。
    //コンストラクタ。
    Card(String suit,int num,int cardTableID,int cardImageID){
        this.suit=suit;
        this.num=num;
        this.cardTableID= cardTableID;
        this. cardImageID= cardImageID;
    }
    //アクセサ。
    public void setAlreadyOpen(boolean y){
        this.alreadyOpen = y;
    }
    public boolean getAlreadyOpen(){
        return this.alreadyOpen;
    }
    public int getCardImageID()
    {
        return this.cardImageID;
    }
    public int getCardTableID() {
        return this.cardTableID;
    }
    public  String getSuit() {return this.suit;}
    public int getNum() {
        return this.num;
    }
}

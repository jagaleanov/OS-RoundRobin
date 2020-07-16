/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package roundrobin;

/**
 *
 * @author jgale
 */
public class Node {

    private Node next;
    private int id;
    private String name;
    private int timeIn;         //tiempo llegada
    private int raf;            //ráfaga
    private int timeStart;      //tiempo Comienzo
    private int timeEnd;        //tiempo Final
    private int timeReturn;        //tiempo Retorno
    private int timeWait;           //Tiempo espera
    private String status;   //estados "Ready","OnProcess","Blocked","Done"
    private char actualChar;

    public Node(int id, int timeIn, int raf) {

        this.next = null;
        this.timeIn = timeIn;
        this.raf = raf;
        this.timeStart = 0;
        this.timeEnd = 0;
        this.timeReturn = 0;
        this.timeWait = 0;
        this.status = "Ready";
        this.id = id;
        this.actualChar = 'A';
        this.name = Integer.toString(id) + this.getChar();
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTimeIn() {
        return timeIn;
    }

    public void setTimeIn(int timeIn) {
        this.timeIn = timeIn;
    }

    public int getRaf() {
        return raf;
    }

    public void setRaf(int raf) {
        this.raf = raf;
    }

    public void processRaf() {
        this.raf--;
    }

    public int getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(int timeStart) {
        this.timeStart = timeStart;
    }

    public int getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(int timeEnd) {
        this.timeEnd = timeEnd;
    }

    public int getTimeReturn() {
        return timeReturn;
    }

    public void setTimeReturn(int timeRet) {
        this.timeReturn = timeRet;
    }

    public int getTimeWait() {
        return timeWait;
    }

    public void setTimeWait(int timeWait) {
        this.timeWait = timeWait;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public char getChar() {
        char temp = this.actualChar;
        this.actualChar++;
        return temp;
    }

    public void setChar(char actualChar) {
        this.actualChar = actualChar;
    }

    @Override
    public Node clone() {

        Node newNode = new Node(this.id, this.timeIn, this.raf);
        newNode.setName(this.name);
        newNode.setTimeStart(this.timeStart);
        newNode.setTimeEnd(this.timeEnd);
        newNode.setTimeReturn(this.timeReturn);
        newNode.setTimeWait(this.timeWait);
        newNode.setChar(this.actualChar);

        return newNode;
    }

    @Override
    public String toString() {
        return "Id:" + getName() + " "
                + "Next:" + this.getNext().getName() + " "
                + "TimeIn:" + this.getTimeIn() + " "
                + "Raf:" + this.getRaf() + " "
                + "TimeStart:" + this.getTimeStart() + " "
                + "TimeEnd:" + this.getTimeEnd() + " "
                + "TimeReturn:" + this.getTimeReturn() + " "
                + "TimeWait:" + this.getTimeWait() + " "
                + "Status:" + this.getStatus();

    }
}

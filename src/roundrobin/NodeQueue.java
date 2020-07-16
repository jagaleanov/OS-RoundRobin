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
public class NodeQueue {

    private Node head;
    private int id;

    public NodeQueue() {
        this.id = 1;
        this.head = null;
    }

    public void add(int timeIn, int raf) {

        Node newNode = new Node(this.id, timeIn, raf);

        if (this.head != null) {
            newNode.setNext(this.head);
            //System.out.println("Ingresando dato en lista NO vacia");
            Node tail = getTail();
            tail.setNext(newNode);
        } else {
            //System.out.println("Ingresando dato en lista vacia");
            newNode.setNext(newNode);
            this.head = newNode;
        }
        this.id++;
        this.print();
    }

    public void add(Node newNode) {

        //System.out.println("Añadiendo nodo con id " + newNode.getId());
        if (this.head != null) {
            //System.out.println("Ingresando dato en lista NO vacia");
            newNode.setNext(this.head);
            Node tail = getTail();
            tail.setNext(newNode);
        } else {
            //System.out.println("Ingresando dato en lista vacia");
            newNode.setNext(newNode);
            this.head = newNode;
        }
    }

    public void replaceHead(Node newHead) {

        if (this.head == null || this.head == this.head.getNext()) {
            newHead.setNext(newHead);
            this.head = newHead;
        } else {
            //System.out.println("Reemplazando cabeza en lista extensa");
            Node temp = this.head;
            Node tail = getTail();
            //System.out.println("La cabeza actual tiene el id "+this.head.getId());
            //System.out.println("La cola actual tiene el id "+tail.getId());
            //System.out.println("El segundo actual tiene el id "+this.head.getNext.getId());

            newHead.setNext(this.head.getNext());
            //System.out.println("A la nueva cabeza se asigno como siguiente el id "+newHead.getNext().getId());

            tail.setNext(newHead);
            //System.out.println("A la cola se asigno como siguiente el id "+tail.getNext().getId()+" proveniente del nodo de reemplazo");

            this.head = newHead;
            //System.out.println("La nueva cabeza es "+this.head.toString()+" y la lista es");
            print();
        }
    }

    public void rollList() {
        this.head = this.head.getNext();
    }

    public Node remove() {

        Node removed = this.head;
        if (this.head != null) {

            if (this.head.getNext() == this.head) {
                this.head = null;
            } else {
                Node tail = getTail();
                Node next = head.getNext();
                tail.setNext(next);

                this.head = next;
            }

            removed.setNext(null);
        }

        return removed;
    }

    public Node getHead() {
        return this.head;
    }

    public Node getTail() {
        Node tail = this.head;

        if (this.head != null) {
            //System.out.println("Buscando cola no vacia");
            while (tail.getNext() != this.head) {
                //System.out.println("Buscando cola no vacia, iterando");
                tail = tail.getNext();
            }
        }

        return tail;
    }

    public int getLength() {

        Node temp = this.head;

        if (temp != null) {
            Node q = this.head;
            int counter = 0;
            do {
                System.out.print(q.getName() + " -> ");
                counter++;
                q = q.getNext();
            } while (q != this.head);
            return counter;
        }
        return 0;

    }

    public void print() {

        if (this.head != null) {
            Node q = this.head;
            do {
                System.out.print(q.toString() + " -> ");
                q = q.getNext();
            } while (q != this.head);
        }

        System.out.println();
    }

    public void printId() {

        if (this.head != null) {
            Node q = this.head;
            do {
                System.out.print(q.getName() + " -> ");
                q = q.getNext();
            } while (q != this.head);
        }

        System.out.println();
    }
}

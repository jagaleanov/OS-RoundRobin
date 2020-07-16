/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package roundrobin;

import java.awt.Color;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

/**
 *
 * @author jgale
 */
public class View extends JFrame implements ActionListener, Runnable {

    private NodeQueue queue;
    private NodeQueue history;
    private NodeQueue block;
    private Node process;

    private int time;
    private int quantum;
    private int quantumCounter;

    private Thread hilo;

    private JLabel lblTime;
    private JLabel lblStatus;
    private JPanel panelStatus;

    private JTextField txtRaf;
    private JTextField txtQuantum;
    private JButton btnAddProcess;
    private JButton btnStartProcess;
    private JButton btnBlockProcess;
    private JButton btnUnBlockProcess;

    private String[] columnNamesQueue;
    private JTable tableQueue;
    private TableModel tableModelQueue;
    private JScrollPane scrollQueue;

    private String[] columnNamesHistory;
    private JTable tableHistory;
    private TableModel tableModelHistory;
    private JScrollPane scrollHistory;

    private String[] columnNamesBlock;
    private JTable tableBlock;
    private TableModel tableModelBlock;
    private JScrollPane scrollBlock;

    private String[] columnNamesGantt;
    private JTable tableGantt;
    private TableModel tableModelGantt;
    private JScrollPane scrollGantt;

    public View() {
        this.hilo = new Thread(this);//pasandole la vista q es runneable
        this.queue = new NodeQueue();
        this.history = new NodeQueue();
        this.block = new NodeQueue();

        this.time = 0;
        this.process = null;

        this.columnNamesQueue = new String[]{"Id", "Llegada", "Ráfaga"};
        this.columnNamesBlock = new String[]{"Id", "Llegada", "Ráfaga"};
        this.columnNamesHistory = new String[]{"Id", "Llegada", "Ráfaga", "Comienzo", "Final", "Retorno", "Espera"};
        this.columnNamesGantt = new String[]{"Id"};

        setBounds(0, 50, 1100, 300);
        setTitle("Gestión de procesos no expulsivos. Round Robin");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        //PANEL CONTROL
        JPanel panelTop = new JPanel();
        add(panelTop);

        panelTop.add(new JLabel("Ráfaga"));
        this.txtRaf = new JTextField("", 5);
        panelTop.add(this.txtRaf);//rafaga

        this.btnAddProcess = new JButton("Ingresar proceso");
        this.btnAddProcess.addActionListener(this);
        panelTop.add(this.btnAddProcess);//Boton añadir

        panelTop.add(new JLabel("Quantum"));
        this.txtQuantum = new JTextField("", 5);
        panelTop.add(this.txtQuantum);//Quantum

        this.btnStartProcess = new JButton("Iniciar simulación");
        this.btnStartProcess.addActionListener(this);
        panelTop.add(this.btnStartProcess);//Boton iniciar

        this.btnBlockProcess = new JButton("Bloquear proceso");
        this.btnBlockProcess.addActionListener(this);
        this.btnBlockProcess.setEnabled(false);
        panelTop.add(this.btnBlockProcess);//Boton bloquear

        this.btnUnBlockProcess = new JButton("Desbloquear proceso");
        this.btnUnBlockProcess.addActionListener(this);
        this.btnUnBlockProcess.setEnabled(false);
        panelTop.add(this.btnUnBlockProcess);//Boton desbloquear

        this.lblTime = new JLabel("Tiempo: 0");
        panelTop.add(this.lblTime);//lbl tiempo

        //PANEL TABLAS
        JPanel panelTables = new JPanel();
        panelTables.setLayout(new BoxLayout(panelTables, BoxLayout.X_AXIS));
        add(panelTables);

        //TABLA COLA
        JPanel panelQueue = new JPanel();
        panelQueue.setLayout(new BoxLayout(panelQueue, BoxLayout.Y_AXIS));
        panelQueue.add(new JLabel("Cola de listos"));
        this.tableModelQueue = new TableModel(columnNamesQueue);
        this.tableQueue = new JTable(this.tableModelQueue);
        this.scrollQueue = new JScrollPane(this.tableQueue);
        panelQueue.add(this.scrollQueue);
        panelTables.add(panelQueue);

        //TABLA HISTORIAL
        JPanel panelHistory = new JPanel();
        panelHistory.setLayout(new BoxLayout(panelHistory, BoxLayout.Y_AXIS));
        panelHistory.add(new JLabel("Historial"));
        this.tableModelHistory = new TableModel(columnNamesHistory);
        this.tableHistory = new JTable(this.tableModelHistory);
        this.scrollHistory = new JScrollPane(this.tableHistory);
        panelHistory.add(this.scrollHistory);
        panelTables.add(panelHistory);

        //TABLA BLOQUEOS
        JPanel panelBlock = new JPanel();
        panelBlock.setLayout(new BoxLayout(panelBlock, BoxLayout.Y_AXIS));
        panelBlock.setBackground(Color.red);
        JLabel lblB = new JLabel("Cola de bloqueos");
        lblB.setForeground(Color.white);
        panelBlock.add(lblB);
        this.tableModelBlock = new TableModel(columnNamesBlock);
        this.tableBlock = new JTable(this.tableModelBlock);
        this.scrollBlock = new JScrollPane(this.tableBlock);
        panelBlock.add(this.scrollBlock);
        panelTables.add(panelBlock);

        //BARRA DE ESTADO
        this.panelStatus = new JPanel();
        add(this.panelStatus);

        this.lblStatus = new JLabel("Estado: Detenido.");
        this.lblStatus.setFont(new Font("Arial", Font.BOLD, 13));
        this.panelStatus.add(this.lblStatus);//lbl estado
        this.panelStatus.setBackground(Color.yellow);

        //GANTT
        JPanel panelGantt = new JPanel();
        panelGantt.setLayout(new BoxLayout(panelGantt, BoxLayout.Y_AXIS));
        panelGantt.add(new JLabel("Diagrama de Gantt"));
        this.tableModelGantt = new TableModel(columnNamesGantt);
        this.tableGantt = new JTable(this.tableModelGantt);
        this.tableGantt.setDefaultRenderer(Object.class, new TableRender());
        this.scrollGantt = new JScrollPane(this.tableGantt);
        panelGantt.add(this.scrollGantt);
        add(panelGantt);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == this.btnStartProcess) {
            try {
                int q = Integer.parseInt(this.txtQuantum.getText());

                if (q < 1) {
                    JOptionPane.showMessageDialog(this, "El quantum es incorrecto.");
                } else {
                    this.quantum = q;
                    this.hilo.start();//iniciar hilo (run())
                    this.txtQuantum.setEditable(false);
                    this.btnStartProcess.setEnabled(false);
                }

            } catch (HeadlessException | NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El quantum es incorrecto.");
            }
        } else if (e.getSource() == this.btnAddProcess) {
            this.addProcess();
        } else if (e.getSource() == this.btnBlockProcess) {
            this.blockProcess();
        } else if (e.getSource() == this.btnUnBlockProcess) {
            this.unBlockProcess();
        }
    }

    public void addProcess() {

        try {
            int raf = Integer.parseInt(this.txtRaf.getText());
            System.out.println("La rafaga es  " + raf);
            this.queue.add(this.time, raf);

            this.tableModelQueue.setRowsQueue(this.queue.getHead());
            this.tableModelGantt.addRowGantt(this.queue.getTail(), this.time);

            this.txtRaf.setText("");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Los datos del proceso son incorrectos.");
        }

        this.txtRaf.requestFocus();
    }

    public boolean startProcess() {
        this.process = this.queue.getHead();//Desencolar proceso y asignarlo a la clase
        this.quantumCounter = 0;

        if (this.process != null) {

            this.queue.print();
            this.process.setStatus("OnProcess");

            this.process.setTimeStart(this.time);//setear tiempo comienzo

            this.process.processRaf();
            this.quantumCounter++;
            this.tableModelQueue.setRowsQueue(this.queue.getHead());
            return true;
        }
        return false;
    }

    public void continueProcess() {
        this.process.processRaf();
        System.out.println("criticProcess: " + this.process.toString());
        //this.queue.print();
        this.queue.replaceHead(this.process);
        this.queue.print();

        this.tableModelQueue.setRowsQueue(this.queue.getHead());
        this.quantumCounter++;
    }

    public void rollProcess() {
        this.queue.printId();

        this.process.setStatus("Ready");
        this.queue.replaceHead(this.process);
        this.queue.rollList();
        this.tableModelQueue.setRowsQueue(this.queue.getHead());
        String tempString = this.process.getName();
        this.process.setName(Integer.toString(this.process.getId()) + this.process.getChar());
        this.tableModelGantt.addRowGantt(this.queue.getTail(), this.time);
        this.process = this.process.clone();

        this.process.setStatus("Done");
        this.process.setName(tempString);
        this.process.setRaf(this.quantum);
        this.process.setTimeEnd(this.process.getRaf() + this.process.getTimeStart());//setear el tiempo final
        this.process.setTimeReturn(this.process.getTimeEnd() - this.process.getTimeIn());//setear el tiempo de retorno
        this.process.setTimeWait(this.process.getTimeReturn() - this.process.getRaf());//setear el tiempo de espera
        this.history.add(this.process);
        this.tableModelHistory.setRowsHistory(this.history.getHead());

        this.process = null;
    }

    public void finishProcess() {
        this.queue.printId();
        this.process.setStatus("Done");
        //this.process.setName(Integer.toString(this.process.getId()) + this.process.getChar());

        this.process.setName(this.process.getName() + " Final");
        this.process.setRaf(quantumCounter);
        this.process.setTimeEnd(this.process.getRaf() + this.process.getTimeStart());//setear el tiempo de espera
        this.process.setTimeReturn(this.process.getTimeEnd() - this.process.getTimeIn());//setear el tiempo de espera
        this.process.setTimeWait(this.process.getTimeReturn() - this.process.getRaf());//setear el tiempo de espera
        this.queue.remove();//sacar de la cola de listos
        this.tableModelQueue.setRowsQueue(this.queue.getHead());
        this.history.add(this.process);
        this.tableModelHistory.setRowsHistory(this.history.getHead());

        this.process = null;
    }

    public void blockProcess() {
        if (this.process != null) {
            this.queue.remove();
            this.process.setStatus("Blocked");
            this.block.add(this.process);
            this.tableModelBlock.setRowsBlock(this.block.getHead());
            this.process = null;
            this.btnBlockProcess.setEnabled(false);
            this.btnUnBlockProcess.setEnabled(true);
        } else {
            JOptionPane.showMessageDialog(this, "No se esta ejecutando un proceso para bloquear");
        }

    }

    public void unBlockProcess() {
        if (this.block.getHead() != null) {
            Node dequeued = this.block.remove();
            dequeued.setStatus("Ready");
            this.queue.add(dequeued);
            this.tableModelBlock.setRowsBlock(this.block.getHead());
            this.tableModelQueue.setRowsQueue(this.queue.getHead());

            if (this.block.getHead() == null) {
                this.btnUnBlockProcess.setEnabled(false);
            }

        } else {
            JOptionPane.showMessageDialog(this, "No Hay procesos para desbloquear");
        }
    }

    @Override
    public void run() {
        while (this.time <= 70) {
            try {
                this.lblTime.setText("Tiempo: " + this.time);

                //PROCESAR O LIBERAR PROCESO EN ESTADO CRÍTICO
                if (this.process != null) {//si hay un proceso en estado critico

                    if (this.process.getRaf() <= 1) {
                        this.btnBlockProcess.setEnabled(false);//habilitar boton de bloqueo
                    }

                    if (this.process.getRaf() <= 0) {//si ya termino su rafaga
                        System.out.println("Finalizando rafaga");
                        this.finishProcess();
                        this.btnBlockProcess.setEnabled(false);//Deshabilitando boton de bloqueo
                        this.lblStatus.setText("Estado: Esperando nuevo proceso.");
                    } else {//si aun necesita mas tiempo de proceso

                        if (this.quantumCounter == this.quantum) {
                            System.out.println("Rotando proceso");
                            this.rollProcess();
                            this.lblStatus.setText("Estado: Rotando proceso ");
                        } else {
                            System.out.println("Continuando rafaga");
                            this.continueProcess();
                            this.lblStatus.setText("Estado: Atendiendo proceso: " + this.process.getName() + ", "
                                    + "Ráfaga total: " + this.process.getRaf());
                        }

                    }
                }

                //AÑADIR PROCESO AL ESTADO CRÍTICO
                if (this.process == null && this.startProcess()) {//si no hay proceso en estado critico se intenta iniciar
                    System.out.println("Iniciando proceso");
                    if (this.process.getRaf() > 1) {
                        this.btnBlockProcess.setEnabled(true);//habilitar boton de bloqueo
                    }

                    this.lblStatus.setText("Estado: Atendiendo proceso: " + this.process.getName() + ", "
                            + "Ráfaga total: " + this.process.getRaf());
                }

                if (this.process == null) {
                    this.lblStatus.setText("Estado: Esperando nuevo proceso.");
                }

                //PAUSAR
                this.tableModelGantt.addColumnGantt(this.queue.getHead(), this.block.getHead(), this.process, this.time);
                sleep(1000);
                this.time++;//incrementar tiempo
            } catch (InterruptedException ex) {
                Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.lblStatus.setText("Estado: Detenido.");
    }

    public static void main(String[] args) {
        // TODO code application logic here
        View view = new View();
    }
}

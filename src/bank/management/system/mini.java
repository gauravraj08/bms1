package bank.management.system;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class mini extends JFrame implements ActionListener {
    String pin;
    JButton button;
    JTextArea label1;
    JLabel label3;
    JLabel label4;

    public mini(String pin){
        this.pin = pin;
        getContentPane().setBackground(new Color(145, 149, 246));
        setSize(400,600);
        setLocation(20,20);
        setLayout(null);

        label1 = new JTextArea();
        label1.setBounds(20,140,360,200);
        label1.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(label1);
        scrollPane.setBounds(20, 140, 360, 200);
        add(scrollPane);

        JLabel label2 = new JLabel("Mini Statement");
        label2.setFont(new Font("System", Font.BOLD,15));
        label2.setBounds(150,20,200,20);
        add(label2);

        label3 = new JLabel();
        label3.setBounds(20,80,300,20);
        add(label3);

        label4 = new JLabel();
        label4.setBounds(20,400,300,20);
        add(label4);

        try{
            Connn c = new Connn();
            ResultSet resultSet = c.statement.executeQuery("select * from login where pin = '"+pin+"'");
            while (resultSet.next()){
                label3.setText("Card Number:  "+ resultSet.getString("cardno").substring(0,4) + "XXXXXXXX"+ resultSet.getString("cardno").substring(12));
            }
        }catch (Exception e ){
            e.printStackTrace();
        }

        try{
            int balance =0;
            Connn c = new Connn();
            ResultSet resultSet = c.statement.executeQuery("select * from bank where pin = '"+pin+"'");

            StringBuilder transactions = new StringBuilder();
            while (resultSet.next()){
                String transaction = resultSet.getString("date") + "\t" +
                        resultSet.getString("type") + "\t" +
                        resultSet.getString("amount") + "\n";

                transactions.append(transaction);

                if (resultSet.getString("type").equals("Deposit")){
                    balance += Integer.parseInt(resultSet.getString("amount"));
                }else {
                    balance -= Integer.parseInt(resultSet.getString("amount"));
                }
            }
            label1.setText(transactions.toString()); // Set all transactions to text area
            label4.setText("Your Total Balance is Rs "+balance);
        }catch (Exception e){
            e.printStackTrace();
        }

        button = new JButton("Download PDF");
        button.setBounds(130,500,150,25);
        button.addActionListener(this);
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        add(button);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button) {
            createPDF(label1.getText(), label3.getText(), label4.getText());
        } else {
            setVisible(false);
        }
    }

    static void createPDF(String transactions, String cardNumber, String balance) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream("Mini_Statement.pdf"));
            document.open();
            document.add(new Paragraph("Mini Statement\n\n"));
            document.add(new Paragraph("Card Number: " + cardNumber + "\n\n"));
            document.add(new Paragraph("Transactions:\n" + transactions + "\n\n"));
            document.add(new Paragraph(balance));
            document.close();
            JOptionPane.showMessageDialog(null, "PDF generated successfully!");
        } catch (DocumentException | FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new mini("");
    }
}

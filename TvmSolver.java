/**
 * This calculates interest, loans, and amortization.
 * 
 * @author King 
 * @version 1.0 May 2017
 */

import javafx.application.Application;

import javafx.stage.Stage;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.control.*;
import javafx.scene.text.*;
import javafx.scene.Node;
import javafx.scene.paint.Color;

import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.HPos;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class TvmSolver extends Application
{
    private GridPane pane;

    private TextField numPmts;
    private TextField ratePerc;
    private TextField presVal;
    private TextField payAmt;
    private TextField futVal;
    private TextField payPerYr;
    private TextField compPerYr;

    public TvmSolver()
    {
        pane = new GridPane();

        numPmts = new TextField();
        ratePerc = new TextField();
        presVal = new TextField();
        payAmt = new TextField();
        futVal = new TextField();
        payPerYr = new TextField();
        compPerYr = new TextField();
    }

    @Override
    public void start(Stage primaryStage)
    {
        primaryStage.setTitle("Loan Calculator");
        pane.setAlignment(Pos.CENTER);
        pane.setPadding(new Insets(11.5, 12.5, 13.5, 14.5));
        pane.setHgap(5.5);
        pane.setVgap(5.5);

        Scene scene = new Scene(pane);

        // Top Menu Bar
        MenuBar menuBar = new MenuBar();
        Menu menuHelp = new Menu("Help");
        MenuItem menuItemAbout = new MenuItem("About");
        MenuItem menuItemHelp = new MenuItem("Help");

        /*menuItemAbout.setOnAction(new EventHandler<ActionEvent>()
        {
        @Override public void handle(ActionEvent e)
        {
        aboutMenuAction();
        }
        });*/

        menuItemAbout.setOnAction(e -> aboutMenuAction());
        menuItemHelp.setOnAction(e -> helpMenuAction());

        menuHelp.getItems().add(menuItemAbout);
        menuHelp.getItems().add(menuItemHelp);
        menuBar.getMenus().add(menuHelp);
        //menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
        final String os = System.getProperty("os.name");
        menuBar.useSystemMenuBarProperty().set(os != null && os.startsWith("Mac"));

        pane.getChildren().add(menuBar);
        primaryStage.setScene(scene);

        ///////////////////////////////////////////////////

        pane.add(new Label("Number of Payments:"), 0, 0); 
        pane.add(numPmts, 1, 0); 

        pane.add(new Label("Interest Rate %:"), 0, 1);
        pane.add(ratePerc, 1, 1); 

        pane.add(new Label("Present Value:"), 0, 2);
        pane.add(presVal, 1, 2);

        pane.add(new Label("Payment Amount:"), 0, 3);
        pane.add(payAmt, 1, 3);

        pane.add(new Label("Future Value:"), 0, 4);
        pane.add(futVal, 1, 4);

        pane.add(new Label("Payments Per Year:"), 0, 5);
        pane.add(payPerYr, 1, 5);

        pane.add(new Label("Compounds Per Year:"), 0, 6);
        pane.add(compPerYr, 1, 6);

        Label error = new Label("");
        error.setTextFill(Color.RED);
        pane.add(error, 0, 7);

        ObservableList<String> options = FXCollections.observableArrayList(
                "Number of Payments", "Interest Rate", "Present Value", 
                "Payment Amount", "Future Value");
        final ComboBox<String> comboBox = new ComboBox<String>(options);
        pane.add(comboBox, 0, 8);

        Button btCalc = new Button("Calculate"); 
        pane.add(btCalc, 1, 8); 

        GridPane.setHalignment(btCalc, HPos.CENTER);

        btCalc.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent e)
                {
                    int errors = 0;
                    int numEmpty = 0;

                    for(int r = 0; r < getRowCount(); r++)
                    {
                        for(int c = 0; c < getColCount(); c++)
                        {
                            // check TextFields for errors
                            if(getNode(r, c) instanceof TextField)
                            {
                                TextField field = (TextField) getNode(r, c);
                                String entry = field.getText();
                                Label label = (Label) getNode(r, c-1);

                                // TextField can only be empty if that is value user has chosen to calculate
                                if(entry.isEmpty() && !label.getText().substring(0, label.getText().length()-1).equals(comboBox.getValue()))
                                {
                                    numEmpty++;
                                }

                                // all entries must be numeric
                                else if(!entry.isEmpty() && !entry.matches("-?\\d+(\\.\\d+)?"))
                                {
                                    errors++;
                                }
                            }
                        }
                    }

                    if(errors > 0 || numEmpty > 0)
                    {
                        error.setText("There are errors");
                    }

                    else
                    {
                        error.setText("");
                        calculate(comboBox.getValue());
                    }
                }
            });

        primaryStage.show();
    }

    /* TVM calc methods */
    private void calculate(String val)
    {
        if(val.equals("Payment Amount"))
        {
            calcPaymentAmt();
        }

        else if(val.equals("Future Value"))
        {
            calcFutVal();
        }
        
        /*else if(val.equals("Number of Payments"))
        {
        	calcNumPmts();
        }*/
    }

    private void calcPaymentAmt()
    {   
        double comps = getComps();
        double rate = getRate();
        rate *= 0.01;
        rate /= comps;
        double pv = getPresVal();
        double fv = getFutVal();
        double numP = getNumPmts();

        double payment = (pv + ((pv + fv) / (Math.pow((1 + rate), numP) - 1))) * (-rate / 1);  // 1 if payment made at beg, 1 + rate if at end

        payAmt.setText(String.valueOf(payment));
    }
    
    public void calcFutVal()
    {
        double comps = getComps();
        double rate = getRate();
        rate *= 0.01;
        rate /= comps;
        double pv = getPresVal();
        double pmt = getPmt();
        double numP = getNumPmts();
        
        double fv = (pmt * 1 / rate) - Math.pow(1 + rate, numP) * (pv + (pmt * 1) / rate);
        
        futVal.setText(String.valueOf(fv));
    }
    
    /*public void calcNumPmts()
    {
    	double comps = getComps();
        double rate = getRate();
        rate *= 0.01;
        rate /= comps;
        double pv = getPresVal();
        double fv = getFutVal();
        
        double numPmts = Math.log(fv / pv) / Math.log(1 + rate);
        
        payAmt.setText(String.valueOf(numPmts));
    }*/
    
    public double getNumPmts()
    {
        return Double.parseDouble(numPmts.getText());
    }
    
    public double getRate()
    {
        return Double.parseDouble(ratePerc.getText());
    }
    
    public double getPresVal()
    {
        return Double.parseDouble(presVal.getText());
    }
    
    public double getFutVal()
    {
        return Double.parseDouble(futVal.getText());
    }
    
    public double getPmt()
    {
        return Double.parseDouble(payAmt.getText());
    }
    
    public double getComps()
    {
        return Double.parseDouble(compPerYr.getText());
    }

    /* Node helper methods */
    private Node getNode(int row, int col) 
    {
        for(Node node: pane.getChildren()) 
        {
            if(node != null && GridPane.getColumnIndex(node) != null && GridPane.getRowIndex(node) != null &&
                    GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) 
            {
                return node;
            }
        }
        return null;
    }

    private int getRowCount() 
    {
        int numRows = pane.getRowConstraints().size();
        for(int i = 0; i < pane.getChildren().size(); i++) 
        {
            Node child = pane.getChildren().get(i);
            if(child.isManaged()) 
            {
                Integer rowIndex = GridPane.getRowIndex(child);
                if(rowIndex != null)
                {
                    numRows = Math.max(numRows,rowIndex+1);
                }
            }
        }
        return numRows;
    }

    private int getColCount() 
    {
        int numCols = pane.getColumnConstraints().size();
        for(int i = 0; i < pane.getChildren().size(); i++) 
        {
            Node child = pane.getChildren().get(i);
            if(child.isManaged()) 
            {
                Integer colIndex = GridPane.getColumnIndex(child);
                if(colIndex != null)
                {
                    numCols = Math.max(numCols,colIndex+1);
                }
            }
        }
        return numCols;
    }

    private void aboutMenuAction()
    {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Stage aboutStage = new Stage();
        aboutStage.setTitle("About Loan Calculator");
        aboutStage.setScene(new Scene(grid, 250, 225));

        Text aboutText = new Text("King's Loan Calculator");
        aboutText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        Text coderText = new Text("Created by Cody King\nFeel free to redistribute application and\n"
                + "adapt code to fit your needs.");
        coderText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
        Text verText = new Text("Ver 1.0");
        verText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));

        grid.add(aboutText, 0, 0);
        grid.add(coderText, 0, 1);
        grid.add(verText, 0, 3);

        aboutStage.show();
    }
    
    private void helpMenuAction()
    {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Stage aboutStage = new Stage();
        aboutStage.setTitle("Help - Loan Calculator");
        aboutStage.setScene(new Scene(grid, 350, 275));

        Text aboutText = new Text("King's Loan Calculator");
        aboutText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        Text coderText = new Text("Only one field may be left blank (the value being calculated)\n" +
                "Negative values mean money has been deposited.");
        coderText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
        
        grid.add(aboutText, 0, 0);
        grid.add(coderText, 0, 1);

        aboutStage.show();
    }

    public static void main(String[] args)
    {
        Application.launch(args);
    }
}

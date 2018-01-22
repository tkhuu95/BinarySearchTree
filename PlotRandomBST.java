package BST;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;

import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * This class plots average case results of a randomly generated BST from N 
 * random key inserts for height, number of leaves, number of compares of a
 * successful search, and the number of compares of an unsuccessful search
 * in the form of a Tufte plot.
 * 
 * @author Thomas Khuu
 */
public class PlotRandomBST extends ApplicationFrame {
    
    /**
     * Constructs the dataset specified and prepares the plot.
     * 
     * @param title  the title for the plot.
     * @param s      the results to plot.
                     ("height", "leaves", "ss" (for successful search), 
                      "us"(for unsuccessful search))
     */
    public PlotRandomBST(String title, String s) {
        super(title);
        if (!s.equalsIgnoreCase("height") && 
            !s.equalsIgnoreCase("leaves") &&
            !s.equalsIgnoreCase("ss")     && 
            !s.equalsIgnoreCase("us")) {
            throw new IllegalArgumentException("Invalid string: " + s);
        }
        String yLabel;
        XYDataset dataset;
        if (s.equalsIgnoreCase("height")) {
            yLabel = "Height";
            dataset = createHeightDataset();
        } else if (s.equalsIgnoreCase("leaves")) {
            yLabel = "Leaves";
            dataset = createLeavesDataset();
        } else if (s.equalsIgnoreCase("ss")) {
            yLabel = "Compares for successful search";
            dataset = createSSCostDataset();
        } else {
            yLabel = "Compares for unsuccessful search";
            dataset = createUSCostDataset();
        }
        JFreeChart xyChart = ChartFactory.createScatterPlot(title, 
                                        "Number of Nodes", yLabel, dataset, 
                                        PlotOrientation.VERTICAL, 
                                        true, true, false);
        
        ChartPanel chartPanel = new ChartPanel(xyChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 500));
        
        XYPlot xyPlot = xyChart.getXYPlot();
        xyPlot.setBackgroundPaint(Color.WHITE);
        
        XYItemRenderer renderer = xyPlot.getRenderer();
        renderer.setSeriesShape(0, new Ellipse2D.Double(-3.0, -3.0, 7.0, 7.0));
        renderer.setSeriesShape(1, new Ellipse2D.Double(-2.0, -2.0, 2.5, 2.5));
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesPaint(1, Color.LIGHT_GRAY);
        renderer.setSeriesStroke(0, new BasicStroke(0.005f));
        
        XYLineAndShapeRenderer lineRenderer = (XYLineAndShapeRenderer) xyPlot.getRenderer();
        lineRenderer.setSeriesShapesVisible(2, false);
        lineRenderer.setSeriesLinesVisible(2, true);
        lineRenderer.setSeriesStroke(2, new BasicStroke(2.0f));
        lineRenderer.setSeriesPaint(2, Color.BLACK);
        
        setContentPane(chartPanel);
        
    }
    
    private XYDataset createHeightDataset() {
        XYSeriesCollection data = new XYSeriesCollection();
        
        XYSeries heights = new XYSeries("1 random BST height");
        XYSeries avg = new XYSeries("Average of 200 heights");
        ArrayList<Integer> heightsList = new ArrayList<>(200);
        
        for (int n = 100; n <= 10000; n += 100) {
            for (int j = 1; j <= 200; j++) {
                RandomBST rbst = new RandomBST(n);
                heightsList.add(rbst.height());
                heights.add(n, rbst.height());
            }
            avg.add(n, calcAvg(heightsList));
            heightsList.clear();
        }
        
        // Average case mathmatical model for height
        // alpha = 4.31107 ...
        // beta  = 1.953 ...
        // alpha*ln(N) - beta*ln(ln(N)) + O(1)
        XYSeries mathModel = new XYSeries("Math Model");
        double alpha = 4.31107;
        double beta  = 1.953;
        for (int n = 100; n <= 10000; n += 10)
            mathModel.add(n, alpha*Math.log(n) - 1.953*Math.log(Math.log(n)) - 5);
        
        data.addSeries(avg);
        data.addSeries(heights);
        data.addSeries(mathModel);
        return data;
    }
    
    private XYDataset createLeavesDataset() {
        XYSeriesCollection data = new XYSeriesCollection();
        
        XYSeries leaves = new XYSeries("Number of leaves for 1 random BST");
        XYSeries avg = new XYSeries("Average of 300 leaves");
        ArrayList<Integer> leavesList = new ArrayList<>(300);
        
        for (int n = 100; n <= 10000; n += 100) {
            for (int j = 1; j <= 300; j++) {
                RandomBST rbst = new RandomBST(n);
                int l = rbst.leaves();
                leavesList.add(l);
                leaves.add(n, l);
            }
            avg.add(n, calcAvg(leavesList));
            leavesList.clear();
        }
        
        // Average case mathmatical model for number of leaves
        // (N + 1) / 3
        XYSeries mathModel = new XYSeries("Math Model");
        for (int n = 100; n <= 10000; n += 10)
            mathModel.add(n, (n + 1)/3.0);
        
        data.addSeries(avg);
        data.addSeries(leaves);
        data.addSeries(mathModel);
        return data;
    }
    
    private XYDataset createSSCostDataset() {
        XYSeriesCollection data = new XYSeriesCollection();
        
        XYSeries costs = new XYSeries("Compares for 1 random BST");
        XYSeries avg = new XYSeries("Average of 500 compares");
        ArrayList<Double> costsList = new ArrayList<>(500);
        
        for (int n = 100; n <= 10000; n += 200) {
            for (int j = 1; j <= 500; j++) {
                RandomBST rbst = new RandomBST(n);
                double pl = rbst.successfulSearchCost();
                costsList.add(pl);
                costs.add(n, pl);
            }
            double total = 0.0;
            for (double i : costsList)
                total += i;
            avg.add(n, total / costsList.size());
            costsList.clear();
        }
        
        // Average case mathmatical model for number of compares for a successful
        //   search
        // 1.39*lg(N) - 1.85
        // or 2*H_n - 3 - 2*H_n/n
        XYSeries mathModel = new XYSeries("Math Model");
        for (int n = 100; n <= 10000; n += 10) {
            double log2 = Math.log(n) / Math.log(2);
            mathModel.add(n, 1.39*log2 - 1.85);
            //double harmonic = harmonic(n);
            //mathModel.add(n, 2*harmonic - 3 - 2*harmonic/n);
        }
        
        data.addSeries(avg);
        data.addSeries(costs);
        data.addSeries(mathModel);
        return data;
    }
    
    private XYDataset createUSCostDataset() {
        XYSeriesCollection data = new XYSeriesCollection();
        
        XYSeries costs = new XYSeries("Compares for 1 random BST");
        XYSeries avg = new XYSeries("Average of 500 compares");
        ArrayList<Double> costsList = new ArrayList<>(500);
        
        for (int n = 100; n <= 10000; n += 200) {
            for (int j = 1; j <= 500; j++) {
                RandomBST rbst = new RandomBST(n);
                double pl = rbst.unsuccessfulSearchCost();
                costsList.add(pl);
                costs.add(n, pl);
            }
            double total = 0.0;
            for (double i : costsList)
                total += i;
            avg.add(n, total / costsList.size());
            costsList.clear();
        }
        
        // Average case mathmatical model for number of compares for an
        //   unsuccessful search
        // 1.39*lg(N) - .846 + 2/(N + 1)
        // or 2*H_(N + 1) - 2
        XYSeries mathModel = new XYSeries("Math Model");
        for (int n = 100; n <= 10000; n += 10) {
            double log2 = Math.log(n)/Math.log(2);
            mathModel.add(n, 1.39*log2 - .846 + 2.0/(n+ 1));
            //double harmonic = harmonic(n+1);
            //mathModel.add(n, 2*harmonic - 2);
        }
        
        data.addSeries(avg);
        data.addSeries(costs);
        data.addSeries(mathModel);
        return data;
    }
    
    private double harmonic(int n) {
        double total = 0.0;
        for (int i = 1; i <= n; i++)
            total += 1.0/i;
        return total;
    }
    
    private double calcAvg(ArrayList<Integer> list) {
        int total = 0;
        for (int i : list)
            total += i;
        return 1.0 * total / list.size();
    }

    public static void main(String[] args) {
        PlotRandomBST p = new PlotRandomBST("Unsuccessful search", "us");
        p.pack();
        RefineryUtilities.centerFrameOnScreen(p);
        p.setVisible(true);
    }
    
}

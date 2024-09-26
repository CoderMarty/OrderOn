package com.orderon.commons;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterJob;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;

import com.orderon.services.Services.Printer;

public class PrintHtml{	
	
	public void Print(String html, String printerName, int copies){
    	PrinterJob pjob = PrinterJob.getPrinterJob();
		JFrame frame= new JFrame();
        Container bg = frame.getContentPane();
        JEditorPane pane = new JEditorPane();
        pane.setEditable(false);
        HTMLEditorKit editorKit = new HTMLEditorKit();
        pane.setEditorKit(editorKit);
        pane.setOpaque(true);
        pane.setText(html);
        bg.add(pane, BorderLayout.CENTER);
        final JScrollPane scrollPane = new JScrollPane(pane);
        frame.getContentPane().add(scrollPane);
        frame.setBounds( 0, 0, 450, 200);
        frame.setVisible(true);
        PageFormat preformat = pjob.defaultPage();
		preformat.setOrientation(PageFormat.PORTRAIT);        
		PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
		Paper paper = new Paper();
		double height = 1500.0;
		double width = 600.0;
		paper.setSize(width, height);
		paper.setImageableArea(0, 0, width, height/4);
		// Orientation
		preformat.setPaper(paper);
		PageFormat postFormat = pjob.validatePage(preformat);
		for (PrintService printService : printServices) {
			try {
				if (printService.getName().equals(printerName)) {
					pjob.setPrintService(printService);
					pjob.setPrintable(new Printer(pane), postFormat);
					for (int x = 0; x < copies; x++)
						pjob.print();
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Some problem has occured" + e.getMessage());
			}
		}
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }
}
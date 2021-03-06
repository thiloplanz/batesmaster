package com.batesmaster;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileOutputStream;


import com.lowagie.text.DocumentException;


import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfReader;

/**
 *batesStamper class
 *
 *this class is responsible for doing all the stamping.
 *
 *@author Gregory Pruden
 *@author Mark Manoukian
 *
 *copyright 2009 Mark Manoukian and Gregory Pruden
 */
public class batesStamper {
	
	/**
	 *batesStamper class constructor
	 */
	public batesStamper()
	{
	    Contruction();	
	}
	
	/**
	 *batesStamper class constructor with properties
	 */
	public batesStamper(String inPdf, String outPdf, int newSeed)
	{
		//go with easy options
		seed = newSeed;
		inputFileName = inPdf;
		outputFileName = outPdf;
		Contruction();
	}
	private void Contruction()
	{	
		//nothing to do so far, fine by me.
	}
	
	public int numpages = 1;
	public int lastnumber = -1;
	private PdfReader reader;
	private PdfStamper stamper;
	
	public String Format = "%05d";
	/** Modifies the numbering format 
	 * 
	 * @param newValue the string java format to use instead of the default of %05d
	 */
	public void setFormat(String newValue)
	{
		Format = newValue;
	}
	
	public int offsetx = 10;
	/** 
	 * Sets the offset from the left of the page
	 * 
	 * @param newValue the number of pixels from the left edge of the page
	 */
	public void setOffsetx( int newValue)
	{
		offsetx = newValue;
	}
	
	public int offsety = 10;
	/**
	 * Sets the offset from the bottom of the page
	 * 
	 * @param newValue the number of pixels up from the bottom edge of the page 
	 */
	public void setOffsety( int newValue)
	{
		offsety = newValue;
	}
	public int rotation = 0;
	/**
	 * Sets the rotation on the page
	 * 
	 * @param newValue the number of degrees of rotation the page, where 0 is normal. 
	 */
	public void setRotation( int newValue)
	{
		rotation = newValue;
	}	
	
	public int origin=0;
	/***
	 * Sets the origin of the bates stamp to a new corner/side of the pdf
	 * @param newValue the origin to set
	 */
	public void setOrigin( int newValue)
	{
		origin = newValue;
	}

	public int seed = 1;
	/**
	 * Sets a new seed value for the bates number which defaults to 1
	 * 
	 * @param newValue the new seed value which will begin the numbering on page 1
	 */
	public void setSeed( int newValue)
	{
		seed = newValue;
		lastnumber = -1;
	}
	
	public String inputFileName = "";
	/**
	 * Set the file name of the input pdf file.
	 * 
	 * @param newValue	file name.
	 */
	public void setInputFileName(String newValue)
	{
		inputFileName = newValue;
		if (outputFileName == "")
		{
			outputFileName = inputFileName+".out.pdf";
		}
	}
	
	public String outputFileName = "";
	/**
	 * Set the file name of the output pdf file.
	 * 
	 * @param newValue	file name.
	 */
	public void setOutputFileName( String newValue)
	{
		outputFileName = newValue;
	}
	
	/**
	 * ProcessDoc opens the document and moves thru the pages stamping.
	 * 
	 * @return returns true if successful false otherwise.
	 */
	public Boolean ProcessDoc()
	{
		try 
		{
			//open pdf document.
			reader = new PdfReader(inputFileName);
			numpages = reader.getNumberOfPages();
			
			//create a stamper object.
			stamper = new PdfStamper(reader, new FileOutputStream(outputFileName));
			
			//move thru the pages stamping
			for(int page=1; page<=numpages; page++)
			{
				if (lastnumber==-1)
					lastnumber = seed-1 + page;

				String bates = String.format(Format, lastnumber++);
				
				//call the bates writer for each page
				if (!writebates(reader, stamper, bates, page, rotation))
				{
					return false;
				}
				
				Thread.yield();
			}
			stamper.close();
			reader = null;
		}
		catch (FileNotFoundException exfnf)
		{
			
			Main.displayln("Batesmaster cannot access the input file( --inpdf) because it is being used by another process.");
			return false;
		}
		catch (IOException ex){
			ex.printStackTrace();
			Main.usage(ex.getMessage());
			return false;
		} catch (DocumentException e) {
			e.printStackTrace();
			Main.usage(e.getMessage());
			return false;

		}
		return true;

	}
	
	static public int[] DefaultTextAlignment= {PdfContentByte.ALIGN_LEFT, PdfContentByte.ALIGN_CENTER, PdfContentByte.ALIGN_RIGHT, PdfContentByte.ALIGN_RIGHT, PdfContentByte.ALIGN_RIGHT, PdfContentByte.ALIGN_CENTER,  PdfContentByte.ALIGN_LEFT,  PdfContentByte.ALIGN_LEFT,  PdfContentByte.ALIGN_CENTER};
    static public float[] DefaultRotation = {0.0f, 0.0f, 0.0f, 270.0f, 0.0f, 0.0f, 0.0f, 90.0f, 0.0f};
    static public Origin[] DefaultOffset = { new Origin(10,10), new Origin(0, 10), new Origin(-30, 10), new Origin(-30,0), new Origin(-30,-30), new Origin(0, -30), new Origin(10,-30), new Origin(10,0), new Origin(0,0)};

	
	/**
	 * writebates computes the location and adds the bates text to the page 
	 * 
	 * @param currentReader the open reader object
	 * @param stamper		the open stamper object
	 * @param bates			the bates number to use
	 * @param page			the page number to stamp
	 * @return				return true on success false otherwise
	 * 
	 * overloaded for backwards compatibility.
	 * 
	 */
	public boolean writebates(PdfReader currentReader, PdfStamper stamper, String bates, int page)
	{
		return writebates(currentReader, stamper, bates, page, 0);
	}
	/**
	 * writebates computes the location and adds the bates text to the page 
	 * 
	 * @param currentReader the open reader object
	 * @param stamper		the open stamper object
	 * @param bates			the bates number to use
	 * @param page			the page number to stamp
	 * @param rotate		the rotation of the number on the page
	 * @return				return true on success false otherwise
	 */
	public boolean writebates(PdfReader currentReader, PdfStamper stamper, String bates, int page, int rotate)
	{
		BaseFont bf;
		try {
			
			//set the font
			//TODO somebody add a command line option for this...

			bf = BaseFont.createFont(BaseFont.HELVETICA,
					BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);

			//TODO same
			int txtsize = 10;
			
			PdfContentByte overContent = stamper.getOverContent(page);
			
			if (bates != null) {
				overContent.beginText();
				overContent.setFontAndSize(bf, txtsize);

				Origin org = new Origin(stamper.getReader().getPageSizeWithRotation(page), origin);
				org = org.Add(batesStamper.DefaultOffset[origin]);
				org = org.Add(new Origin(offsetx, offsety));
				
				overContent.showTextAligned(batesStamper.DefaultTextAlignment[origin], bates, org.x, org.y, batesStamper.DefaultRotation[origin]+(float)rotate);
				overContent.endText();
			}
			return true;

		} catch (DocumentException e) {
			// debug remove
			e.printStackTrace();
			Main.usage(e.getMessage());
			return false;
		} catch (IOException e) {
			// debug remove
			e.printStackTrace();
			Main.usage(e.getMessage());
			return false;
		}
	}
	

}

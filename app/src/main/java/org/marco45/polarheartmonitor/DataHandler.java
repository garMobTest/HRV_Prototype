package org.marco45.polarheartmonitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;

import com.androidplot.xy.SimpleXYSeries;

/**
 * This handler is specalised for decoding my polar hart rate monitor and get the data from it
 * Data format is something like this
 * 
 * 
 *   Polar Bluetooth Wearlink packet example;
 *   Hdr Len Chk Seq Status HeartRate RRInterval_16-bits
 *    FE  08  F7  06   F1      48          03 64
 *   where; 
 *      Hdr always = 254 (0xFE), 
 *      Chk = 255 - Len
 *      Seq range 0 to 15
 *      Status = Upper nibble may be battery voltage
 *               bit 0 is Beat Detection flag.
 *               
 *   src:http://ww.telent.net/2012/5/3/listening_to_a_polar_bluetooth_hrm_in_linux
 *   
 *   
 *   
 *   Now also work for BTLE (H7) based on the bluetooth protocol. 
 *   
 * @author Marco
 *
 */
public class DataHandler extends Observable{
	private static DataHandler dd = new DataHandler();
	
	//DATA FOR SAVING
	boolean newValue = true;
	SimpleXYSeries series1;
	ConnectThread reader;
	H7ConnectThread H7;

	private ArrayList<Integer>mBeatToBeatArray = new ArrayList<>();;
	private int mHRV = 0;

	
	int pos=0;
	int valBPM =0;
	int min=0;
	int max=0;
	
	//for the average maths
	int data=0;
	int total=0;

	int id;
	
	private DataHandler(){
		
	}
	
	public static DataHandler getInstance(){
		return dd;
	}

	public void acqui(int i){
		if (i==254){
			pos=0;
		}
		else if (pos==5){
			cleanInput(i,null);
		}
		pos++;
	}
	
	public void cleanInput(int i, Integer[] beatVar){
		valBPM =i;
		if(valBPM !=0){
			data+= valBPM;//Average maths
			total++;//Average maths
		}
		if(valBPM <min||min==0)
			min= valBPM;
		else if(valBPM >max)
			max= valBPM;

		if (beatVar!= null) addToBeatToBeat(beatVar);
		setChanged();
		notifyObservers();
	}

    public String getLastBpmValue(){

        return valBPM + " BPM";
    }

    public int getLastBPMIntValue(){

        return valBPM;
    }
	
	public String getMin(){
		return "Min " + min + " BPM";
	}
	
	public String getMax(){

		return "Max " + max + " BPM";
	}
	
	public String getAvg(){
		if(total==0)
            return "Avg " + 0 + " BPM";
		return "Avg " + data/total + " BPM";
	}

	public void setNewValue(boolean newValue) {
		this.newValue = newValue;
	}

	public SimpleXYSeries getSeries1() {
		return series1;
	}

	public void setSeries1(SimpleXYSeries series1) {
		this.series1 = series1;
	}

	public ConnectThread getReader() {
		return reader;
	}

	public void setReader(ConnectThread reader) {
		this.reader = reader;
	}

	public int getID() {
		return id;
	}
	public void setID(int id) {
		this.id=id;
	}

	public void setH7(H7ConnectThread H7){
		this.H7=H7;
	}
	public H7ConnectThread getH7(){
		return H7;
	}

	public void addToBeatToBeat(Integer[] integers){
		mBeatToBeatArray.addAll(Arrays.asList(integers));
		if (mBeatToBeatArray.size() >= 30){
			//run gary's method
			mHRV = Algorithm.calculateHRV_Score(mBeatToBeatArray);
			mBeatToBeatArray = new ArrayList<>();
		}
	}
	public Integer getLastRR(){
		return mBeatToBeatArray.get(mBeatToBeatArray.size());
	}

	public int getmHRV() {
		return mHRV;
	}
}

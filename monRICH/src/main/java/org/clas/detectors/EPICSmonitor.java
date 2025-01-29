package org.clas.detectors;

import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.epics.ca.Channel;
import org.epics.ca.Context;
import org.epics.ca.Monitor;
import org.jlab.utils.groups.IndexedList;

public class EPICSmonitor {
	
    public String      appName = null;
    public String      detName = null;

    public Context     context = null;
    
    public Monitor<Double>     monitor = null;
    
    public JPanel                   engineView = new JPanel();    
    public JSplitPane               enginePane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    public JPanel                 engine1DView = new JPanel();
    public JPanel                 engine2DView = new JPanel();

    public JPanel                   buttonPane = null;
    public JPanel                   sliderPane = null;
    
    TreeMap<String,IndexedList<Channel<Double>>>  caMap = new TreeMap<String,IndexedList<Channel<Double>>>();
   
    String ca_FC    = "scaler_calc1";
    String ca_2C24A = "hallb_IPM2C24A_CUR";
    String ca_2H01  = "hallb_IPM2H01_CUR";
        
    public int is1,is2;
    public int sectorSelected, layerSelected, channelSelected, orderSelected;
    public Boolean online = true;
    
    
	public void clearMaps() {
		System.out.println("FCEpics: Clearing Maps");
	    this.caMap.clear();		
	}
	
	public int createContext() {
	    if (!online) return 0;
	    this.context = new Context();
	    return 1;
	}
	
	public int destroyContext() {
        if (!online) return 0;
	    this.context.close();
	    return 1;
	}
    
    public String getName() {
        return this.appName;
    }
    
    public int connectCa(int grp, String action, int sector, int layer, int channel) {
        if (!online) return 0;
        try {
        caMap.get(action).getItem(grp,sector,layer,channel).connectAsync().get(1,TimeUnit.MILLISECONDS);  //org.epics.ca
        }
        catch (InterruptedException e) {  
            return -1;
        }        
        catch (TimeoutException e) {  
            return -1;
        }        
        catch (ExecutionException e) {  
            return -1;
        }
        return 1;
        
    }
    
    public double getCaValue(int grp, String action, int sector, int layer, int channel) {
        if (!online) return 1000.0;
        try {
        CompletableFuture<Double> ffd = caMap.get(action).getItem(grp,sector,layer,channel).getAsync(); //org.epics.ca
        return ffd.get(); 
        }
        catch (InterruptedException e) {  
            return -1.0;
        }        
        catch (ExecutionException e) {  
            return -1.0;
        }   
    }
    
    public int putCaValue(int grp, String action, int sector, int layer, int channel, double value) {
        if(!online) return 0;
        caMap.get(action).getItem(grp,sector,layer,channel).putNoWait(value); //org.epics.ca  
        return 1;
    } 
    
    public void startMonitor(int grp, String action, int sector, int layer, int channel) {
        this.monitor = caMap.get(action).getItem(grp,sector,layer,channel).addValueMonitor(value->System.out.println(value));
    }
    
    public void stopMonitor(){
        this.monitor.close();
    }
    
    public int setCaActionNames(int grp, String action) {
        if (!online) return 0;
        IndexedList<Channel<Double>> map = new IndexedList<Channel<Double>>(4);
        map.add(context.createChannel(ca_FC,    Double.class),grp,0,0,1); //org.epics.ca
        map.add(context.createChannel(ca_2C24A, Double.class),grp,0,0,2); //org.epics.ca
        map.add(context.createChannel(ca_2H01,  Double.class),grp,0,0,3); //org.epics.ca
        caMap.put(action,map);
      	return 1;
    }




}

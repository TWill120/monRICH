package org.clas.detectors;

import org.clas.viewer.DetectorMonitor;
import org.jlab.detector.helicity.DecoderBoardUtil;
import org.jlab.groot.data.GraphErrors;
import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;
import org.jlab.groot.group.DataGroup;
import org.jlab.io.base.DataBank;
import org.jlab.io.base.DataEvent;

/**
 *
 * @author devita
 */

public class HELmonitor extends DetectorMonitor {

    static final DecoderBoardUtil decoderBoardUtil = DecoderBoardUtil.QUARTET;
    static final byte DELAY_WINDOWS = 8;

    public HELmonitor(String name) {
        super(name);
        this.setDetectorTabNames("signals","board");
        this.init(false);
    }

    @Override
    public void createHistos() {
        this.setNumberOfEvents(0);
        H1F summary = new H1F("summary","helicity",20,-1.5,1.5);
        summary.setTitleX("helicity");
        summary.setTitleY("Counts");
        summary.setFillColor(3);
        DataGroup sum = new DataGroup(1,1);
        sum.addDataSet(summary, 0);
        this.setDetectorSummary(sum);
        H1F rawHel = new H1F("rawHelicity","rawHelicity", 200,0.,4000.);
        rawHel.setTitleX("helicity");
        rawHel.setTitleY("Counts");
        rawHel.setFillColor(22);
        H1F rawSync = new H1F("rawSync","rawSync", 200,0.,4000.);
        rawSync.setTitleX("sync");
        rawSync.setTitleY("Counts");
        rawSync.setFillColor(33);
        H1F rawQuartet = new H1F("rawQuartet","rawQuartet", 200,0.,4000.);
        rawQuartet.setTitleX("quartet");
        rawQuartet.setTitleY("Counts");
        rawQuartet.setFillColor(44);
        H1F hel = new H1F("helicity","helicity", 20,-1.5,1.5);
        hel.setTitleX("helicity");
        hel.setTitleY("Counts");
        hel.setFillColor(22);
        H1F sync = new H1F("sync","sync", 20,-1.5,1.5);
        sync.setTitleX("sync");
        sync.setTitleY("Counts");
        sync.setFillColor(33);
        H1F quartet = new H1F("quartet","quartet", 20,-1.5,1.5);
        quartet.setTitleX("quartet");
        quartet.setTitleY("Counts");
        quartet.setFillColor(44);
        GraphErrors  helSequence = new GraphErrors("helSequence");
        helSequence.setTitle("Helicity Sequence");
        helSequence.setTitleX("Event Number");
        helSequence.setTitleY("Signals");
        helSequence.setMarkerColor(22);
        helSequence.setMarkerSize(5);
        GraphErrors  syncSequence = new GraphErrors("syncSequence");
        syncSequence.setTitle("Sync Sequence");
        syncSequence.setTitleX("Event Number");
        syncSequence.setTitleY("Signals");
        syncSequence.setMarkerColor(33);
        syncSequence.setMarkerSize(5);
        GraphErrors  quartetSequence = new GraphErrors("quartetSequence");
        quartetSequence.setTitle("Quartet Sequence");
        quartetSequence.setTitleX("Event Number");
        quartetSequence.setTitleY("Signals");
        quartetSequence.setMarkerColor(44);
        quartetSequence.setMarkerSize(5);

        DataGroup dg = new DataGroup(2,4);
        dg.addDataSet(rawHel,      0);
        dg.addDataSet(rawSync,     1);
        dg.addDataSet(rawQuartet,  2);
        dg.addDataSet(hel,         4);
        dg.addDataSet(sync,        5);
        dg.addDataSet(quartet,     6);
        dg.addDataSet(helSequence, 7);
        dg.addDataSet(syncSequence, 7);
        dg.addDataSet(quartetSequence, 7);

        this.getDataGroup().add(dg, 0,0,0);
        
        DataGroup dg2 = new DataGroup(3,2);
        H1F template = new H1F("h","",3,-1.5,1.5);
        template.setTitleY("Counts");
        String[] signals = {"Helicity","HelicityRaw","Pair","Pattern","L3"};
        int i=0;
        for (String s : signals) {
            H1F h = template.histClone(String.format("helbrd%s",s));
            if (i<2) h.setFillColor(22);
            else if (i==2) h.setFillColor(33);
            else if (i==3) h.setFillColor(44);
            else if (i==4) h.setFillColor(22);
            h.setTitleX(s);
            dg2.addDataSet(h, i==2?++i:i++);
        }

        H2F helcmp = new H2F("helcmp","",3,-1.5,1.5,3,-1.5,1.5);
        helcmp.setTitleX("L3");
        helcmp.setTitleY("Helicity");
        dg2.addDataSet(helcmp, 2);
        this.getDataGroup().add(dg2, 1,0,0);
    }
       
    @Override
    public void plotHistos() {
        this.getDetectorCanvas().getCanvas("signals").divide(3, 2);
        this.getDetectorCanvas().getCanvas("signals").setGridX(false);
        this.getDetectorCanvas().getCanvas("signals").setGridY(false);
        this.getDetectorCanvas().getCanvas("signals").cd(0);
        this.getDetectorCanvas().getCanvas("signals").getPad(0).getAxisY().setLog(true);
        this.getDetectorCanvas().getCanvas("signals").draw(this.getDataGroup().getItem(0,0,0).getH1F("rawHelicity"));
        this.getDetectorCanvas().getCanvas("signals").cd(1);
        this.getDetectorCanvas().getCanvas("signals").getPad(1).getAxisY().setLog(true);
        this.getDetectorCanvas().getCanvas("signals").draw(this.getDataGroup().getItem(0,0,0).getH1F("rawSync"));
        this.getDetectorCanvas().getCanvas("signals").cd(2);
        this.getDetectorCanvas().getCanvas("signals").getPad(2).getAxisY().setLog(true);
        this.getDetectorCanvas().getCanvas("signals").draw(this.getDataGroup().getItem(0,0,0).getH1F("rawQuartet"));
        this.getDetectorCanvas().getCanvas("signals").cd(3);
        this.getDetectorCanvas().getCanvas("signals").draw(this.getDataGroup().getItem(0,0,0).getH1F("helicity"));
        this.getDetectorCanvas().getCanvas("signals").cd(4);
        this.getDetectorCanvas().getCanvas("signals").draw(this.getDataGroup().getItem(0,0,0).getH1F("sync"));
        this.getDetectorCanvas().getCanvas("signals").cd(5);
        this.getDetectorCanvas().getCanvas("signals").draw(this.getDataGroup().getItem(0,0,0).getH1F("quartet"));
        this.getDetectorCanvas().getCanvas("signals").update();

        this.getDetectorCanvas().getCanvas("board").divide(3,2);
        this.getDetectorCanvas().getCanvas("board").cd(0);
        this.getDetectorCanvas().getCanvas("board").draw(this.getDataGroup().getItem(1,0,0).getH1F("helbrdHelicity"));
        this.getDetectorCanvas().getCanvas("board").cd(3);
        this.getDetectorCanvas().getCanvas("board").draw(this.getDataGroup().getItem(1,0,0).getH1F("helbrdHelicityRaw"));
        this.getDetectorCanvas().getCanvas("board").cd(4);
        this.getDetectorCanvas().getCanvas("board").draw(this.getDataGroup().getItem(1,0,0).getH1F("helbrdPair"));
        this.getDetectorCanvas().getCanvas("board").cd(5);
        this.getDetectorCanvas().getCanvas("board").draw(this.getDataGroup().getItem(1,0,0).getH1F("helbrdPattern"));
        this.getDetectorCanvas().getCanvas("board").cd(1);
        this.getDetectorCanvas().getCanvas("board").draw(this.getDataGroup().getItem(1,0,0).getH1F("helbrdL3"));

        this.getDetectorCanvas().getCanvas("board").cd(2);
        this.getDetectorCanvas().getCanvas("board").draw(this.getDataGroup().getItem(1,0,0).getH2F("helcmp"));
        this.getDetectorCanvas().getCanvas("board").getCanvasPads().get(2).setPalette("kAvocado");
        this.getDetectorCanvas().getCanvas("board").getCanvasPads().get(2).getAxisZ().setLog(true);
    }

    public void processEventBoard(DataEvent event) {
        final int row = 0;
        if (event.hasBank("HEL::online")) {
            DataBank b = event.getBank("HEL::online");
            if (b.rows() > 0) {
                int honline = -b.getByte("helicity", row);
                this.getDataGroup().getItem(1,0,0).getH1F("helbrdL3").fill(honline);
            }
        }
        if (event.hasBank("HEL::decoder")) {
            DataBank bboard = event.getBank("HEL::decoder");
            if (bboard.rows() > 0) {
                int hboard = !decoderBoardUtil.check(bboard) ? 0 :
                    -1+2*decoderBoardUtil.getWindowHelicity(bboard, DELAY_WINDOWS);
                this.getDataGroup().getItem(1,0,0).getH1F("helbrdHelicity").fill(hboard);
                this.getDataGroup().getItem(1,0,0).getH1F("helbrdHelicityRaw").fill(-1+2*(float)(bboard.getInt("helicityArray",row)&1));
                this.getDataGroup().getItem(1,0,0).getH1F("helbrdPair").fill(-1+2*(float)(bboard.getInt("pairArray",row)&1));
                this.getDataGroup().getItem(1,0,0).getH1F("helbrdPattern").fill(-1+2*(float)(bboard.getInt("patternArray",row)&1));
                if (event.hasBank("HEL::online")) {
                    DataBank bonline = event.getBank("HEL::online");
                    if (bonline.rows() > 0) {
                        int honline = -bonline.getByte("helicity", row);
                        this.getDataGroup().getItem(1,0,0).getH2F("helcmp").fill(honline,hboard);
                    }
                }
            }
        }
    }
    
    @Override
    public void processEvent(DataEvent event) {

        processEventBoard(event);

        if (event.hasBank("RUN::trigger") && event.hasBank("RUN::config") && event.hasBank("HEL::adc")) {
            DataBank bank = event.getBank("HEL::adc");
            int rows = bank.rows();
            int hel     = -1;
            for(int loop = 0; loop < rows; loop++){
                int component = bank.getShort("component", loop);
                int rawValue  = bank.getShort("ped", loop);
                int value = (int) rawValue/2000;
                switch (component) {
                    case 1:
                        this.getDataGroup().getItem(0,0,0).getH1F("rawHelicity").fill(rawValue);
                        this.getDataGroup().getItem(0,0,0).getH1F("helicity").fill(value);
                        hel=value;
                        break;
                    case 2:
                        this.getDataGroup().getItem(0,0,0).getH1F("rawSync").fill(rawValue);
                        this.getDataGroup().getItem(0,0,0).getH1F("sync").fill(value);
                        break;
                    case 3:
                        this.getDataGroup().getItem(0,0,0).getH1F("rawQuartet").fill(rawValue);
                        this.getDataGroup().getItem(0,0,0).getH1F("quartet").fill(value);
                        break;
                    default:
                        break;
                }
            }
            this.getDetectorSummary().getH1F("summary").fill(hel);
        }       
    }

    @Override
    public void analysisUpdate() {

    }
    
}

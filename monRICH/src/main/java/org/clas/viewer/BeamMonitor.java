package org.clas.viewer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.epics.ca.Channel;
import org.epics.ca.Context;

/**
 *
 * @author baltzell
 */
public class BeamMonitor extends Thread {

    static final String[] PVNAMES = {"IPM2C21A", "IPM2C24A"};
    static final float DEFAULT_BEAM_CURRENT = -1;

    private float minBeamCurrent = DEFAULT_BEAM_CURRENT;
    private final List<Channel> beamCurrents = new ArrayList<>();

    private volatile float beamCurrent = DEFAULT_BEAM_CURRENT;

    public BeamMonitor(float minimumBeamCurrent) {
        minBeamCurrent = minimumBeamCurrent;
    }

    /**
     * @return value of the current beam current in nA
     */
    public float getBeamCurrent() {
        return beamCurrent;
    }

    /**
     * @return whether beam currently looks good
     */
    public boolean getBeamStatus() {
        return beamCurrents.isEmpty() || beamCurrent > minBeamCurrent;
    }
    
    /**
     * Connect to all the PVs.
     */
    public void connect() {
        beamCurrents.clear();
        Context context = new Context();
        for (String s : PVNAMES) {
            Channel c = context.createChannel(s, Double.class);
            try {
                c.connectAsync().get(2, TimeUnit.SECONDS);
                beamCurrents.add(c);
            } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                System.err.println(ex);
                System.err.println("Error connection to PV:  " + s);
            }
        }
        if (beamCurrents.isEmpty()) {
            System.err.println("No PVs available.  Assume beam is always good.");
        }
    }
    
    /**
     * Poll all PVs and store beam current.
     */
    public void poll() {
        while (!beamCurrents.isEmpty()) {
            float minimum = Float.POSITIVE_INFINITY;
            for (Channel c : beamCurrents) {
                try {
                    minimum = (float) Math.min(minimum, (double)c.getAsync().get());
                } catch (InterruptedException | ExecutionException ex) {
                    System.err.println(ex);
                } 
            }
            beamCurrent = minimum!=Float.POSITIVE_INFINITY ? minimum : DEFAULT_BEAM_CURRENT;
        }
    }

    @Override
    public void run() {
        connect();
        while (!beamCurrents.isEmpty()) {
            poll();
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                System.err.println(ex);
            }
        }
    }

}

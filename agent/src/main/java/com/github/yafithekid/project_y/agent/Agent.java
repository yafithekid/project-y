package com.github.yafithekid.project_y.agent;


import com.github.yafithekid.project_y.agent.daemons.*;
import com.github.yafithekid.project_y.commons.config.Config;
import com.github.yafithekid.project_y.commons.config.ResourceMonitor;
import com.sun.management.GarbageCollectionNotificationInfo;

import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.*;

public class Agent {
    //singleton to prevent multiple thread of hardware daemon within tomcat or other webserver
    private static Thread hardwareThread;

    //the java instrumentation object
    private static Instrumentation globalInst;

    public static void premain(String agentArgs, Instrumentation inst) throws FileNotFoundException {
        Config config = Config.readFromFile(Config.DEFAULT_FILE_CONFIG_LOCATION);
        globalInst = inst;

        inst.addTransformer(new BasicClassFileTransformer(config));

        ResourceMonitor rm = config.getResourceMonitor();
        if (rm.isActive() && hardwareThread == null){
//            installGCMonitoring();
            List<HardwareDaemonWriter> writers = new ArrayList<HardwareDaemonWriter>();

            if (rm.isDebug()){
                System.out.println("use debug");
                writers.add(new HardwareDaemonWriterMockImpl());
            } else {
                System.out.println("not using debug");
            }
            if (rm.isSendToCollector()) try {
                writers.add(new HardwareDaemonWriterCollectorImpl(config));
            } catch (IOException e) {
                e.printStackTrace();
            }
            hardwareThread = new HardwareDaemon(config,writers);
            hardwareThread.start();
        }
    }

    static void installGCMonitoring(){
        List<GarbageCollectorMXBean> gcbeans = ManagementFactory.getGarbageCollectorMXBeans();

        for (GarbageCollectorMXBean gcbean : gcbeans) {
            System.out.println(gcbean);
            NotificationEmitter emitter = (NotificationEmitter) gcbean;
            //use an anonymously generated listener for this example
            // - proper code should really use a named class
            NotificationListener listener = new NotificationListener() {
                //keep a count of the total time spent in GCs
                long totalGcDuration = 0;

                //implement the notifier callback handler
                @Override
                public void handleNotification(Notification notification, Object handback) {
                    //we only handle GARBAGE_COLLECTION_NOTIFICATION notifications here
                    if (notification.getType().equals(GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION)) {
                        //get the information associated with this notification
                        GarbageCollectionNotificationInfo info = GarbageCollectionNotificationInfo.from((CompositeData) notification.getUserData());
                        //get all the info and pretty print it
                        long duration = info.getGcInfo().getDuration();
                        String gctype = info.getGcAction();
                        if ("end of minor GC".equals(gctype)) {
                            gctype = "Young Gen GC";
                        } else if ("end of major GC".equals(gctype)) {
                            gctype = "Old Gen GC";
                        }
                        System.out.println();
                        System.out.println(gctype + ": - " + info.getGcInfo().getId()+ " " + info.getGcName() + " (from " + info.getGcCause()+") "+duration + " microseconds; start-end times " + info.getGcInfo().getStartTime()+ "-" + info.getGcInfo().getEndTime());
                        //System.out.println("GcInfo CompositeType: " + info.getGcInfo().getCompositeType());
                        //System.out.println("GcInfo MemoryUsageAfterGc: " + info.getGcInfo().getMemoryUsageAfterGc());
                        //System.out.println("GcInfo MemoryUsageBeforeGc: " + info.getGcInfo().getMemoryUsageBeforeGc());

                        //Get the information about each memory space, and pretty print it
                        Map<String, MemoryUsage> membefore = info.getGcInfo().getMemoryUsageBeforeGc();
                        Map<String, MemoryUsage> mem = info.getGcInfo().getMemoryUsageAfterGc();
                        for (Map.Entry<String, MemoryUsage> entry : mem.entrySet()) {
                            String name = entry.getKey();
                            MemoryUsage memdetail = entry.getValue();
                            long memInit = memdetail.getInit();
                            long memCommitted = memdetail.getCommitted();
                            long memMax = memdetail.getMax();
                            long memUsed = memdetail.getUsed();
                            MemoryUsage before = membefore.get(name);
                            long beforepercent = ((before.getUsed()*1000L)/before.getCommitted());
                            long percent = ((memUsed*1000L)/before.getCommitted()); //>100% when it gets expanded

                            System.out.print(name + (memCommitted==memMax?"(fully expanded)":"(still expandable)") +"used: "+(beforepercent/10)+"."+(beforepercent%10)+"%->"+(percent/10)+"."+(percent%10)+"%("+((memUsed/1048576)+1)+"MB) / ");
                        }
                        System.out.println();
                        totalGcDuration += info.getGcInfo().getDuration();
                        long percent = totalGcDuration*1000L/info.getGcInfo().getEndTime();
                        System.out.println("GC cumulated overhead "+(percent/10)+"."+(percent%10)+"%");
                    }
                }
            };

            //Add the listener
            emitter.addNotificationListener(listener, null, null);
        }
    }

    public static long getObjectSize(Object object){
        if (globalInst == null){
            throw new IllegalStateException("Agent not initialized");
        }
        if (object == null){
            return 0;
        } else {
            long totalSize = globalInst.getObjectSize(object);
            if (object instanceof List){
                List list = (List) object;
                for(int i = 0; i < list.size();i++){
                    if (list.get(i)!=null){
                        totalSize += globalInst.getObjectSize(list.get(i));
                    }
                }
            }
            globalInst.getObjectSize(object);
            return totalSize;
        }
    }

//    public static long getObjectSize(int x){
//        return 4;
//    }
//
//    public static long getObjectSize(float x){
//        return 4;
//    }
//
//    public static long getObjectSize(double x){
//        return 8;
//    }
//
//    public static long getObjectSize(char x){
//        return 1;
//    }
//
//    public static long getObjectSize(boolean x){
//        return 1;
//    }
//
//    public static long getObjectSize(byte b){
//        return 1;
//    }
//
//    public static long getObjectSize(long x){
//        return 8;
//    }
//
//    public static long getObjectSize(Void v){
//        return 0;
//    }


}

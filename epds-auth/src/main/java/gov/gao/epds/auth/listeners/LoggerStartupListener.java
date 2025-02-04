//package gov.gao.epds.auth.listeners;
//
//import ch.qos.logback.classic.Level;
//import ch.qos.logback.classic.Logger;
//import ch.qos.logback.classic.LoggerContext;
//import ch.qos.logback.classic.spi.LoggerContextListener;
//import ch.qos.logback.core.Context;
//import ch.qos.logback.core.spi.ContextAwareBase;
//import ch.qos.logback.core.spi.LifeCycle;
//
//public class LoggerStartupListener extends ContextAwareBase implements LoggerContextListener, LifeCycle {
//
//    private static final String DEFAULT_LOG_FILE = "epds";
//
//    private boolean started = false;
//
//    @Override
//    public void start() {
//        if (started) return;
//
//        String userHome = System.getProperty("jboss.home.dir");
//
//        Context context = getContext();
//
//        context.putProperty("DEV_HOME", userHome);
//
//        started = true;
//    }
//
//    @Override
//    public void stop() {
//    }
//
//    @Override
//    public boolean isStarted() {
//        return started;
//    }
//
//    @Override
//    public boolean isResetResistant() {
//        return true;
//    }
//
//    @Override
//    public void onStart(LoggerContext context) {
//    }
//
//    @Override
//    public void onReset(LoggerContext context) {
//    }
//
//    @Override
//    public void onStop(LoggerContext context) {
//    }
//
//    @Override
//    public void onLevelChange(Logger logger, Level level) {
//    }
//}
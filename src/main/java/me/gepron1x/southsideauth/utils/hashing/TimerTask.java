package me.gepron1x.southsideauth.utils.hashing;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TimerTask {
    private ScheduledTask task;
    private TimeUnit timeUnit;
    private long left;
    private static Map<Character, Integer> timeUnits = new HashMap<>();
    {
        timeUnits.put('s', 1);
        timeUnits.put('m', timeUnits.get('s') * 60);
        timeUnits.put('h', timeUnits.get('m') * 60);
    }
    public TimerTask(Plugin plugin, Runnable runnable, long delay, TimeUnit timeUnit) {
        this.left = delay;
        this.timeUnit = timeUnit;
        this.task = ProxyServer.getInstance().getScheduler().schedule(plugin, () -> {
            this.left--;
            if(left <= 0) {
                this.task.cancel();
                runnable.run();
            }
       }, 1, 1, timeUnit);
    }
    public long howManyLeft() {
        return left;
    }
    public TimeUnit getTimeUnit() {
        return timeUnit;
    }
    public void cancel() {
        task.cancel();
    }



}

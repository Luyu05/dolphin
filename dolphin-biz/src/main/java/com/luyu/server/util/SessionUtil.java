package com.luyu.server.util;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by luyu on 2019/7/9.
 */
public class SessionUtil {

    private static final ConcurrentHashMap<String, ChannelGroup> groupIdChannelGroupMap = new ConcurrentHashMap<String, ChannelGroup>();

    private static final Interner<String> interner = Interners.newWeakInterner();

    public static void bindChannelGroup(String groupId, Channel channel) {
        synchronized (interner.intern(groupId)) {
            if (groupIdChannelGroupMap.get(groupId).isEmpty()) {
                ChannelGroup channelGroup = new DefaultChannelGroup(channel.eventLoop());
                channelGroup.add(channel);
                groupIdChannelGroupMap.put(groupId, channelGroup);
            } else {
                groupIdChannelGroupMap.get(groupId).add(channel);
            }
        }
    }

    public static ChannelGroup getChannelGroup(String groupId) {
        return groupIdChannelGroupMap.get(groupId);
    }

}

package com.luyu.server.util;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by luyu on 2019/7/9.
 */
public class SessionUtil {

    private static final Map<String, ChannelGroup> groupIdChannelGroupMap = new ConcurrentHashMap<String, ChannelGroup>();

    public static void bindChannelGroup(String groupId, Channel channel) {
        if (groupIdChannelGroupMap.get(groupId).isEmpty()) {
            ChannelGroup channelGroup = new DefaultChannelGroup(channel.eventLoop());
            channelGroup.add(channel);
            groupIdChannelGroupMap.put(groupId, channelGroup);
        } else {
            groupIdChannelGroupMap.get(groupId).add(channel);
        }
    }

    public static ChannelGroup getChannelGroup(String groupId) {
        return groupIdChannelGroupMap.get(groupId);
    }

}

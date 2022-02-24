package com.creativityshark.pyrotastic.common.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

public class RocketColorHandler {
    public static final int DEFAULT_COLOR = 12134697;

    public static int getColor(ItemStack stack) {
        NbtCompound nbtCompound = stack.getSubNbt("Fireworks");
        if (nbtCompound == null) {
            return DEFAULT_COLOR;
        } else {
            NbtList nbtList = nbtCompound.getList("Explosions", 10);
            if (nbtList == null || nbtList.isEmpty()) {
                return DEFAULT_COLOR;
            } else {
                NbtCompound nbtList1 = nbtList.getCompound(0);
                if (nbtList1 == null || nbtList1.isEmpty()) {
                    return DEFAULT_COLOR;
                } else {
                    int[] colors = nbtList1.getIntArray("Colors");
                    if (colors.length <= 0) {
                        return DEFAULT_COLOR;
                    }
                    return getMixColor(colors);
                }
            }
        }
    }
    //yoinked from net.minecraft.client.color.item
    private static int getMixColor(int[] is) {
        if (is != null && is.length != 0) {
            if (is.length == 1) {
                return is[0];
            } else {
                int i = 0;
                int j = 0;
                int k = 0;

                for (int l : is) {
                    i += (l & 16711680) >> 16;
                    j += (l & '\uff00') >> 8;
                    k += (l & 255);
                }

                i /= is.length;
                j /= is.length;
                k /= is.length;
                return i << 16 | j << 8 | k;
            }
        } else {
            return DEFAULT_COLOR;
        }
    }
}
//    color equation taken from Minecraft Wiki
//    https://minecraft.fandom.com/wiki/Dye#Dyeing_armor
//    private static int getMixColor(int[] colors) {
//        double totalRed = 0, totalBlue = 0, totalGreen = 0, totalMax = 0;
//        for (int color1 : colors) {
//            Color color = new Color(color1);
//            totalRed += color.getRed();
//            totalBlue += color.getBlue();
//            totalGreen += color.getGreen();
//            totalMax += Math.max(Math.max(color.getRed(), color.getBlue()), Math.max(color.getBlue(), color.getGreen()));
//        }
//        double averageRed = totalRed / colors.length;
//        double averageBlue = totalBlue / colors.length;
//        double averageGreen = totalGreen / colors.length;
//        double averageMax = totalMax / colors.length;
//
//        double maxOfAverage = Math.max(Math.max(averageRed, averageBlue), Math.max(averageBlue, averageGreen));
//        double gainFactor = averageMax / maxOfAverage;
//        return (int) ((averageRed * gainFactor) * 65536 + (averageGreen * gainFactor) * 256 + (averageBlue * gainFactor));
//    }
//}


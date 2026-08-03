package com.zirfps.config;

public final class ZirConfig {
    public static boolean smartMode = true;
    public static boolean enableEntityCulling = true;
    public static boolean enableChunkOcclusion = true;
    public static boolean enableAdaptiveRenderDistance = true;
    public static boolean enableDynamicFps = true;
    public static int targetFps = 60;
    public static int backgroundFpsLimit = 1;
    public static int maxRenderDistance = 16;
    public static int entityRenderDistance = 64;
    public static boolean entityShadows = true;
    public static int particles = 0;

    private ZirConfig() {}
}

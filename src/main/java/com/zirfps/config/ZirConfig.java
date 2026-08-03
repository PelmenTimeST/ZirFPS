package com.zirfps.config;

public final class ZirConfig {
    public static boolean enableEntityCulling = true;
    public static boolean enableChunkOcclusion = true;
    public static boolean enableAdaptiveRenderDistance = true;
    public static int targetFps = 60;

    public static boolean enableDynamicFps = true;
    public static int backgroundFpsLimit = 1;

    private ZirConfig() {}
}

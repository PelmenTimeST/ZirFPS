package com.zirfps.config;

public final class ZirConfig {
    public static boolean enableEntityCulling = true;
    public static double maxEntityRenderDistance = 64.0;
    public static double occlusionCheckDistance = 32.0;

    public static boolean enableDynamicFps = true;
    public static int backgroundFpsLimit = 1;

    private ZirConfig() {}
}

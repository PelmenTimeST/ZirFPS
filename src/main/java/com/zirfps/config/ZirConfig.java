package com.zirfps.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ZirConfig {
    public static final ModConfigSpec SPEC;

    private static final ModConfigSpec.BooleanValue SMART_MODE;
    private static final ModConfigSpec.IntValue TARGET_FPS;
    private static final ModConfigSpec.BooleanValue ENTITY_CULLING;
    private static final ModConfigSpec.BooleanValue CHUNK_OCCLUSION;
    private static final ModConfigSpec.BooleanValue ADAPTIVE_RENDER_DISTANCE;
    private static final ModConfigSpec.BooleanValue DYNAMIC_FPS;
    private static final ModConfigSpec.IntValue BACKGROUND_FPS_LIMIT;
    private static final ModConfigSpec.IntValue MAX_RENDER_DISTANCE;
    private static final ModConfigSpec.IntValue ENTITY_RENDER_DISTANCE;
    private static final ModConfigSpec.BooleanValue ENTITY_SHADOWS;
    private static final ModConfigSpec.IntValue PARTICLES;

    public static volatile boolean smartMode = true;
    public static volatile int targetFps = 60;
    public static volatile boolean enableEntityCulling = true;
    public static volatile boolean enableChunkOcclusion = true;
    public static volatile boolean enableAdaptiveRenderDistance = true;
    public static volatile boolean enableDynamicFps = true;
    public static volatile int backgroundFpsLimit = 1;
    public static volatile int maxRenderDistance = 16;
    public static volatile int entityRenderDistance = 64;
    public static volatile boolean entityShadows = true;
    public static volatile int particles = 0;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("general");

        SMART_MODE = builder
            .comment("Automatically lower graphics settings to hold the target FPS")
            .define("smartMode", true);

        TARGET_FPS = builder
            .comment("FPS that Smart Mode and Adaptive Render Distance try to maintain")
            .defineInRange("targetFps", 60, 10, 260);

        ENTITY_CULLING = builder
            .comment("Skip rendering entities that are not actually visible")
            .define("enableEntityCulling", true);

        CHUNK_OCCLUSION = builder
            .comment("Hide entities located in chunks fully blocked from view")
            .define("enableChunkOcclusion", true);

        ADAPTIVE_RENDER_DISTANCE = builder
            .comment("Lower render distance automatically when FPS drops below target")
            .define("enableAdaptiveRenderDistance", true);

        DYNAMIC_FPS = builder
            .comment("Limit FPS while the game window is not focused")
            .define("enableDynamicFps", true);

        BACKGROUND_FPS_LIMIT = builder
            .comment("FPS limit applied while the window is unfocused")
            .defineInRange("backgroundFpsLimit", 1, 1, 60);

        MAX_RENDER_DISTANCE = builder
            .comment("Render distance can never exceed this value, in chunks")
            .defineInRange("maxRenderDistance", 16, 2, 32);

        ENTITY_RENDER_DISTANCE = builder
            .comment("Max distance entities render at, in blocks. 0 falls back to render distance")
            .defineInRange("entityRenderDistance", 64, 0, 512);

        ENTITY_SHADOWS = builder
            .comment("Entity shadows, mirrors the vanilla video option")
            .define("entityShadows", true);

        PARTICLES = builder
            .comment("Particle amount: 0 = All, 1 = Decreased, 2 = Minimal")
            .defineInRange("particles", 0, 0, 2);

        builder.pop();
        SPEC = builder.build();
    }

    private ZirConfig() {}

    public static void bake() {
        smartMode = SMART_MODE.get();
        targetFps = TARGET_FPS.get();
        enableEntityCulling = ENTITY_CULLING.get();
        enableChunkOcclusion = CHUNK_OCCLUSION.get();
        enableAdaptiveRenderDistance = ADAPTIVE_RENDER_DISTANCE.get();
        enableDynamicFps = DYNAMIC_FPS.get();
        backgroundFpsLimit = BACKGROUND_FPS_LIMIT.get();
        maxRenderDistance = MAX_RENDER_DISTANCE.get();
        entityRenderDistance = ENTITY_RENDER_DISTANCE.get();
        entityShadows = ENTITY_SHADOWS.get();
        particles = PARTICLES.get();
    }

    public static void setSmartMode(boolean v) { smartMode = v; SMART_MODE.set(v); }
    public static void setTargetFps(int v) { targetFps = v; TARGET_FPS.set(v); }
    public static void setEntityCulling(boolean v) { enableEntityCulling = v; ENTITY_CULLING.set(v); }
    public static void setChunkOcclusion(boolean v) { enableChunkOcclusion = v; CHUNK_OCCLUSION.set(v); }
    public static void setAdaptiveRenderDistance(boolean v) { enableAdaptiveRenderDistance = v; ADAPTIVE_RENDER_DISTANCE.set(v); }
    public static void setDynamicFps(boolean v) { enableDynamicFps = v; DYNAMIC_FPS.set(v); }
    public static void setBackgroundFpsLimit(int v) { backgroundFpsLimit = v; BACKGROUND_FPS_LIMIT.set(v); }
    public static void setMaxRenderDistance(int v) { maxRenderDistance = v; MAX_RENDER_DISTANCE.set(v); }
    public static void setEntityRenderDistance(int v) { entityRenderDistance = v; ENTITY_RENDER_DISTANCE.set(v); }
    public static void setEntityShadows(boolean v) { entityShadows = v; ENTITY_SHADOWS.set(v); }
    public static void setParticles(int v) { particles = v; PARTICLES.set(v); }

    public static void save() {
        SPEC.save();
    }
}

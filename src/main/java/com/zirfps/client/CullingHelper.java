package com.zirfps.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.*;
import net.minecraft.world.IBlockReader;

public final class CullingHelper {
    private CullingHelper() {}

    private static final Minecraft MC = Minecraft.getInstance();

    public static boolean isVisible(Entity entity) {
        if (MC.player == null || MC.world == null) return true;

        ActiveRenderInfo info = MC.gameRenderer.getActiveRenderInfo();
        Vec3d cam = info.getProjectedView();
        AxisAlignedBB box = entity.getBoundingBox();
        if (box == null || box.hasNaN()) return true;

        Vec3d center = box.getCenter();
        double distSq = cam.squareDistanceTo(center);

        double maxDist = MC.options.renderDistanceChunks * 16.0;
        if (distSq > maxDist * maxDist) return false;

        if (distSq > 32.0 * 32.0) return true;

        return rayVisible(cam, center);
    }

    private static boolean rayVisible(Vec3d start, Vec3d end) {
        IBlockReader world = MC.world;
        if (world == null) return true;

        RayTraceResult result = world.rayTraceBlocks(start, end, false, true, false);
        if (result == null || result.getType() == RayTraceResult.Type.MISS) return true;

        double hitDist = start.squareDistanceTo(result.getHitVec());
        double endDist = start.squareDistanceTo(end);
        return hitDist >= endDist - 0.5;
    }
}

package com.zirfps.client;

import net.minecraft.block.BlockState;
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

        double maxDist = MC.gameSettings.renderDistanceChunks * 16.0;
        if (distSq > maxDist * maxDist) return false;

        if (distSq > 32.0 * 32.0) return true;

        return multiRayVisible(cam, box);
    }

    private static boolean multiRayVisible(Vec3d cam, AxisAlignedBB box) {
        Vec3d min = new Vec3d(box.minX, box.minY, box.minZ);
        Vec3d max = new Vec3d(box.maxX, box.maxY, box.maxZ);

        Vec3d[] targets = {
            box.getCenter(),
            new Vec3d(min.x, min.y, min.z),
            new Vec3d(max.x, min.y, min.z),
            new Vec3d(min.x, min.y, max.z),
            new Vec3d(max.x, min.y, max.z),
        };

        int visibleRays = 0;
        for (Vec3d target : targets) {
            if (rayVisible(cam, target)) visibleRays++;
        }

        return visibleRays >= 1;
    }

    private static boolean rayVisible(Vec3d start, Vec3d end) {
        IBlockReader world = MC.world;
        if (world == null) return true;

        BlockRayTraceResult res = world.rayTraceBlocks(
            new RayTraceContext(
                start, end,
                RayTraceContext.BlockMode.COLLIDER,
                RayTraceContext.FluidMode.NONE,
                MC.player
            )
        );

        if (res.getType() == RayTraceResult.Type.MISS) return true;

        BlockState state = world.getBlockState(res.getPos());
        if (!state.isOpaqueCube(world, res.getPos())) return true;

        double hitDist = start.squareDistanceTo(res.getHitVec());
        double endDist = start.squareDistanceTo(end);
        return hitDist >= endDist - 0.5;
    }
}

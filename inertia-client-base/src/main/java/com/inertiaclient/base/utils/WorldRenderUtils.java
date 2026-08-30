package com.inertiaclient.base.utils;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.mixin.mixins.accessors.FrustumAccessor;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.renderpearl.api.pipeline.*;
import lombok.Getter;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.FrustumIntersection;
import org.joml.Vector3f;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Stack;

import static com.inertiaclient.base.InertiaBase.mc;

public class WorldRenderUtils {//3d render utils

    public static final AABB FULL_BOX = new AABB(0, 0, 0, 1, 1, 1);
    public static boolean frustumCheck = true;//probably wont use but you can disable and reenable it
    @Getter
    private static Stack<Boolean> subtractCamera = new Stack<>();


    //public static void enableGL() {
    //TODO:
        /*RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();*/
    //}

    public static final RenderPipeline DEBUG_TRIANGLE_FAN_PIPELINE = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET).withLocation("pipeline/debug_triangle_fan_inertia").withCull(false).withDepthStencilState(Optional.empty()).withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR).withPrimitiveTopology(PrimitiveTopology.TRIANGLE_STRIP).build());
    public static final RenderType DEBUG_TRIANGLE_FAN = RenderType.create("debug_triangle_fan_inertia", RenderSetup.builder(DEBUG_TRIANGLE_FAN_PIPELINE).createRenderSetup());
    public static final RenderType DEBUG_TRIANGLE_FAN_OUTLINE = RenderType.create("debug_triangle_fan_outline_inertia", RenderSetup.builder(DEBUG_TRIANGLE_FAN_PIPELINE).setOutline(RenderSetup.OutlineProperty.IS_OUTLINE).createRenderSetup());


    public static final RenderPipeline LINES_TRANSLUCENT_PIPELINE = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.LINES_SNIPPET).withLocation("pipeline/lines_translucent_inertia").withCull(false).withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT)).withDepthStencilState(Optional.empty()).build());
    public static final RenderType LINES_TRANSLUCENT = RenderType.create("lines_translucent_inertia", RenderSetup.builder(LINES_TRANSLUCENT_PIPELINE).createRenderSetup());


    //don't use this(I mean you can but there will be float precision errors when far from spawn /teleport IKnowImEZ 10000000 90 10000000)
    public static void subtractCameraPosition(PoseStack matrices) {
        matrices.translate(-mc.gameRenderer.mainCamera().position().x(), -mc.gameRenderer.mainCamera().position().y(), -mc.gameRenderer.mainCamera().position().z());
    }

    public static double getEntityInterpolatedX(Entity entity, float delta) {

        double posX = 0;
        if (entity.tickCount == 0) {//if the entity was just spawned, it doesn't have lastTickPosX, this renders better/nicer
            posX = entity.getX();
        } else {
            posX = entity.xOld + ((entity.getX() - entity.xOld) * delta);
        }

        return posX;
    }

    public static double getEntityInterpolatedY(Entity entity, float delta) {

        double posY = 0;
        if (entity.tickCount == 0) {//if the entity was just spawned, it doesn't have lastTickPosX, this renders better/nicer
            posY = entity.getY();
        } else {
            posY = entity.yOld + ((entity.getY() - entity.yOld) * delta);
        }

        return posY;
    }

    public static double getEntityInterpolatedZ(Entity entity, float delta) {

        double posZ = 0;
        if (entity.tickCount == 0) {//if the entity was just spawned, it doesn't have lastTickPosX, this renders better/nicer
            posZ = entity.getZ();
        } else {
            posZ = entity.zOld + ((entity.getZ() - entity.zOld) * delta);
        }

        return posZ;
    }

    public static float getEntityInterpolatedYaw(Entity entity, float delta) {
        if (entity.tickCount == 0) {//if the entity was just spawned, it doesn't have lastTickPosX, this renders better/nicer
            return entity.getYRot();
        }
        return entity.yRotO + ((entity.getYRot() - entity.yRotO) * delta);
    }

    public static void drawEntityESP(PoseStack poseStack, SubmitNodeStorage submitNodeStorage, Entity entity, float delta, boolean fill, boolean outline, Color color) {
        double renderX = getEntityInterpolatedX(entity, delta);
        double renderY = getEntityInterpolatedY(entity, delta);
        double renderZ = getEntityInterpolatedZ(entity, delta);


        double minX = entity.getBoundingBox().minX - entity.getX() + renderX;
        double minY = entity.getBoundingBox().minY - entity.getY() + renderY;
        double minZ = entity.getBoundingBox().minZ - entity.getZ() + renderZ;
        double maxX = entity.getBoundingBox().maxX - entity.getX() + renderX;
        double maxY = entity.getBoundingBox().maxY - entity.getY() + renderY;
        double maxZ = entity.getBoundingBox().maxZ - entity.getZ() + renderZ;
        if (fill) {
            drawBox(poseStack, submitNodeStorage, minX, minY, minZ, maxX, maxY, maxZ, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 0.15f);
        }
        if (outline) {
            drawBoxOutline(poseStack, submitNodeStorage, minX, minY, minZ, maxX, maxY, maxZ, 1.5f, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 0.5f);
        }
    }


    public static void drawBoxESP(PoseStack poseStack, SubmitNodeStorage submitNodeStorage, AABB box, boolean fill, boolean outline, Color color) {
        WorldRenderUtils.drawBoxESP(poseStack, submitNodeStorage, BlockPos.ZERO, box, fill, outline, color);
    }

    public static void drawBoxESP(PoseStack poseStack, SubmitNodeStorage submitNodeStorage, BlockPos blockPos, AABB box, boolean fill, boolean outline, Color color) {
        double minX = blockPos.getX() + box.minX;
        double minY = blockPos.getY() + box.minY;
        double minZ = blockPos.getZ() + box.minZ;
        double maxX = blockPos.getX() + box.maxX;
        double maxY = blockPos.getY() + box.maxY;
        double maxZ = blockPos.getZ() + box.maxZ;
        if (fill) {
            drawBox(poseStack, submitNodeStorage, minX, minY, minZ, maxX, maxY, maxZ, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 0.15f);
        }
        if (outline) {
            drawBoxOutline(poseStack, submitNodeStorage, minX, minY, minZ, maxX, maxY, maxZ, 1.5f, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 0.5f);
        }
    }

    public static void drawBox(PoseStack poseStack, SubmitNodeStorage submitNodeStorage, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha) {
        drawBox(poseStack, submitNodeStorage, DEBUG_TRIANGLE_FAN, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
    }

    public static void drawBox(PoseStack poseStack, SubmitNodeStorage submitNodeStorage, RenderType renderType, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha) {
        if (isVisibleInFrustum(minX, minY, minZ, maxX, maxY, maxZ)) {
            if (shouldSubtractCameraPosition()) {
                minX -= mc.gameRenderer.mainCamera().position().x();
                minY -= mc.gameRenderer.mainCamera().position().y();
                minZ -= mc.gameRenderer.mainCamera().position().z();
                maxX -= mc.gameRenderer.mainCamera().position().x();
                maxY -= mc.gameRenderer.mainCamera().position().y();
                maxZ -= mc.gameRenderer.mainCamera().position().z();
            }

            float finalMinX = (float) minX;
            float finalMinY = (float) minY;
            float finalMinZ = (float) minZ;
            float finalMaxX = (float) maxX;
            float finalMaxY = (float) maxY;
            float finalMaxZ = (float) maxZ;

            submitNodeStorage.submitCustomGeometry(poseStack, renderType, (pose, buffer) -> {
                buffer.addVertex(pose, finalMinX, finalMinY, finalMinZ).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, finalMinX, finalMinY, finalMinZ).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, finalMinX, finalMinY, finalMinZ).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, finalMinX, finalMinY, finalMaxZ).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, finalMinX, finalMaxY, finalMinZ).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, finalMinX, finalMaxY, finalMaxZ).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, finalMinX, finalMaxY, finalMaxZ).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, finalMinX, finalMinY, finalMaxZ).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, finalMaxX, finalMaxY, finalMaxZ).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, finalMaxX, finalMinY, finalMaxZ).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, finalMaxX, finalMinY, finalMaxZ).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, finalMaxX, finalMinY, finalMinZ).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, finalMaxX, finalMaxY, finalMaxZ).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, finalMaxX, finalMaxY, finalMinZ).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, finalMaxX, finalMaxY, finalMinZ).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, finalMaxX, finalMinY, finalMinZ).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, finalMinX, finalMaxY, finalMinZ).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, finalMinX, finalMinY, finalMinZ).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, finalMinX, finalMinY, finalMinZ).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, finalMaxX, finalMinY, finalMinZ).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, finalMinX, finalMinY, finalMaxZ).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, finalMaxX, finalMinY, finalMaxZ).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, finalMaxX, finalMinY, finalMaxZ).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, finalMinX, finalMaxY, finalMinZ).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, finalMinX, finalMaxY, finalMinZ).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, finalMinX, finalMaxY, finalMaxZ).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, finalMaxX, finalMaxY, finalMinZ).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, finalMaxX, finalMaxY, finalMaxZ).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, finalMaxX, finalMaxY, finalMaxZ).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, finalMaxX, finalMaxY, finalMaxZ).setColor(red, green, blue, alpha);
            });
        }
    }

    public static void drawBoxOutline(PoseStack poseStack, SubmitNodeStorage submitNodeStorage, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float lineWidth, float red, float green, float blue, float alpha) {
        drawBoxOutline(poseStack, submitNodeStorage, LINES_TRANSLUCENT, minX, minY, minZ, maxX, maxY, maxZ, lineWidth, red, green, blue, alpha);
    }

    public static void drawBoxOutline(PoseStack poseStack, SubmitNodeStorage submitNodeStorage, RenderType renderType, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float lineWidth, float red, float green, float blue, float alpha) {
        if (isVisibleInFrustum(minX, minY, minZ, maxX, maxY, maxZ)) {
            if (shouldSubtractCameraPosition()) {
                minX -= mc.gameRenderer.mainCamera().position().x();
                minY -= mc.gameRenderer.mainCamera().position().y();
                minZ -= mc.gameRenderer.mainCamera().position().z();
                maxX -= mc.gameRenderer.mainCamera().position().x();
                maxY -= mc.gameRenderer.mainCamera().position().y();
                maxZ -= mc.gameRenderer.mainCamera().position().z();
            }

            float finalMinX = (float) minX;
            float finalMinY = (float) minY;
            float finalMinZ = (float) minZ;
            float finalMaxX = (float) maxX;
            float finalMaxY = (float) maxY;
            float finalMaxZ = (float) maxZ;

            submitNodeStorage.submitCustomGeometry(poseStack, renderType, (pose, buffer) -> {
                buffer.addVertex(pose, finalMinX, finalMinY, finalMinZ).setColor(red, green, blue, alpha).setNormal(pose, 1.0F, 0.0F, 0.0F).setLineWidth(lineWidth);
                buffer.addVertex(pose, finalMaxX, finalMinY, finalMinZ).setColor(red, green, blue, alpha).setNormal(pose, 1.0F, 0.0F, 0.0F).setLineWidth(lineWidth);
                buffer.addVertex(pose, finalMinX, finalMinY, finalMinZ).setColor(red, green, blue, alpha).setNormal(pose, 0.0F, 1.0F, 0.0F).setLineWidth(lineWidth);
                buffer.addVertex(pose, finalMinX, finalMaxY, finalMinZ).setColor(red, green, blue, alpha).setNormal(pose, 0.0F, 1.0F, 0.0F).setLineWidth(lineWidth);
                buffer.addVertex(pose, finalMinX, finalMinY, finalMinZ).setColor(red, green, blue, alpha).setNormal(pose, 0.0F, 0.0F, 1.0F).setLineWidth(lineWidth);
                buffer.addVertex(pose, finalMinX, finalMinY, finalMaxZ).setColor(red, green, blue, alpha).setNormal(pose, 0.0F, 0.0F, 1.0F).setLineWidth(lineWidth);
                buffer.addVertex(pose, finalMaxX, finalMinY, finalMinZ).setColor(red, green, blue, alpha).setNormal(pose, 0.0F, 1.0F, 0.0F).setLineWidth(lineWidth);
                buffer.addVertex(pose, finalMaxX, finalMaxY, finalMinZ).setColor(red, green, blue, alpha).setNormal(pose, 0.0F, 1.0F, 0.0F).setLineWidth(lineWidth);
                buffer.addVertex(pose, finalMaxX, finalMaxY, finalMinZ).setColor(red, green, blue, alpha).setNormal(pose, -1.0F, 0.0F, 0.0F).setLineWidth(lineWidth);
                buffer.addVertex(pose, finalMinX, finalMaxY, finalMinZ).setColor(red, green, blue, alpha).setNormal(pose, -1.0F, 0.0F, 0.0F).setLineWidth(lineWidth);
                buffer.addVertex(pose, finalMinX, finalMaxY, finalMinZ).setColor(red, green, blue, alpha).setNormal(pose, 0.0F, 0.0F, 1.0F).setLineWidth(lineWidth);
                buffer.addVertex(pose, finalMinX, finalMaxY, finalMaxZ).setColor(red, green, blue, alpha).setNormal(pose, 0.0F, 0.0F, 1.0F).setLineWidth(lineWidth);
                buffer.addVertex(pose, finalMinX, finalMaxY, finalMaxZ).setColor(red, green, blue, alpha).setNormal(pose, 0.0F, -1.0F, 0.0F).setLineWidth(lineWidth);
                buffer.addVertex(pose, finalMinX, finalMinY, finalMaxZ).setColor(red, green, blue, alpha).setNormal(pose, 0.0F, -1.0F, 0.0F).setLineWidth(lineWidth);
                buffer.addVertex(pose, finalMinX, finalMinY, finalMaxZ).setColor(red, green, blue, alpha).setNormal(pose, 1.0F, 0.0F, 0.0F).setLineWidth(lineWidth);
                buffer.addVertex(pose, finalMaxX, finalMinY, finalMaxZ).setColor(red, green, blue, alpha).setNormal(pose, 1.0F, 0.0F, 0.0F).setLineWidth(lineWidth);
                buffer.addVertex(pose, finalMaxX, finalMinY, finalMaxZ).setColor(red, green, blue, alpha).setNormal(pose, 0.0F, 0.0F, -1.0F).setLineWidth(lineWidth);
                buffer.addVertex(pose, finalMaxX, finalMinY, finalMinZ).setColor(red, green, blue, alpha).setNormal(pose, 0.0F, 0.0F, -1.0F).setLineWidth(lineWidth);
                buffer.addVertex(pose, finalMinX, finalMaxY, finalMaxZ).setColor(red, green, blue, alpha).setNormal(pose, 1.0F, 0.0F, 0.0F).setLineWidth(lineWidth);
                buffer.addVertex(pose, finalMaxX, finalMaxY, finalMaxZ).setColor(red, green, blue, alpha).setNormal(pose, 1.0F, 0.0F, 0.0F).setLineWidth(lineWidth);
                buffer.addVertex(pose, finalMaxX, finalMinY, finalMaxZ).setColor(red, green, blue, alpha).setNormal(pose, 0.0F, 1.0F, 0.0F).setLineWidth(lineWidth);
                buffer.addVertex(pose, finalMaxX, finalMaxY, finalMaxZ).setColor(red, green, blue, alpha).setNormal(pose, 0.0F, 1.0F, 0.0F).setLineWidth(lineWidth);
                buffer.addVertex(pose, finalMaxX, finalMaxY, finalMinZ).setColor(red, green, blue, alpha).setNormal(pose, 0.0F, 0.0F, 1.0F).setLineWidth(lineWidth);
                buffer.addVertex(pose, finalMaxX, finalMaxY, finalMaxZ).setColor(red, green, blue, alpha).setNormal(pose, 0.0F, 0.0F, 1.0F).setLineWidth(lineWidth);
            });
        }
    }

    public static Frustum getFrustum() {
        return mc.gameRenderer.mainCamera().getCullFrustum();
    }

    public static boolean isVisibleInFrustum(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        if (!frustumCheck) {
            return true;
        }
        Frustum frustum = getFrustum();
        int result = ((FrustumAccessor) frustum).invokeCubeInFrustum(minX, minY, minZ, maxX, maxY, maxZ);
        return result == FrustumIntersection.INSIDE || result == FrustumIntersection.INTERSECT;
    }

    public static boolean isEntityInFrustum(Entity entity) {
        if (!frustumCheck) {
            return true;
        }
        Frustum frustum = getFrustum();
        return frustum.isVisible(entity.getBoundingBox());
    }

    public static boolean isBlockEntityInFrustum(BlockEntity blockEntity) {
        VoxelShape blockEntityCollisionShape = mc.level.getBlockState(blockEntity.getBlockPos()).getCollisionShape(mc.level, blockEntity.getBlockPos());
        if (blockEntityCollisionShape == Shapes.empty()) {
            return false;
        }

        AABB box = blockEntityCollisionShape.bounds().move(blockEntity.getBlockPos());
        return isVisibleInFrustum(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
    }

    public static Vector3f getNormalsForLine(double x1, double y1, double z1, double x2, double y2, double z2) {
        //code was modified from chat gbt
        double xDiff = (x2 - x1);
        double yDiff = (y2 - y1);
        double zDiff = (z2 - z1);
        double length = Math.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);
        xDiff /= length;
        yDiff /= length;
        zDiff /= length;
        return new Vector3f((float) xDiff, (float) yDiff, (float) zDiff);
    }

    private static boolean shouldSubtractCameraPosition() {
        if (subtractCamera.empty()) {
            return true;
        }
        return subtractCamera.peek();
    }

    public static ArrayList<BlockEntity> getAllBlockEntities() {
        ArrayList<BlockEntity> blockEntities = new ArrayList<>();

        int viewDistance = InertiaBase.mc.options.renderDistance().get();
        int playerChunkX = SectionPos.blockToSectionCoord(InertiaBase.mc.player.getBlockX());
        int playerChunkZ = SectionPos.blockToSectionCoord(InertiaBase.mc.player.getBlockZ());

        for (int x = -viewDistance; x <= viewDistance; x++) {
            for (int z = -viewDistance; z <= viewDistance; z++) {
                var chunk = mc.level.getChunk(playerChunkX + x, playerChunkZ + z);

                if (chunk != null) {
                    blockEntities.addAll(chunk.getBlockEntities().values());
                }
            }
        }

        return blockEntities;
    }


}
